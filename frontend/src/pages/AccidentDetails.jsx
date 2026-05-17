import { useEffect, useRef, useState } from "react"
import L from "leaflet"
import "leaflet/dist/leaflet.css"
import {
  fetchActiveAccidents,
  fetchAccidentById,
  createPublicAccident,
  dispatchAmbulance,
  notifyPolice,
  notifyHospital,
} from "../services/api"

const AccidentDetails = () => {
  const [accidents, setAccidents] = useState([])
  const [selectedAccident, setSelectedAccident] = useState(null)
  const [loading, setLoading] = useState(true)
  const [notification, setNotification] = useState("")
  const [toastType, setToastType] = useState("success")
  const [error, setError] = useState("")
  const [actionState, setActionState] = useState({
    ambulance: false,
    police: false,
    hospital: false,
  })
  const [socketStatus, setSocketStatus] = useState("DISCONNECTED")
  const mapElement = useRef(null)
  const mapInstance = useRef(null)
  const markerLayer = useRef(null)
  const socketRef = useRef(null)
  const selectedAccidentRef = useRef(null)

  const pushToast = (message, type = "success") => {
    setNotification(message)
    setToastType(type)
    setTimeout(() => setNotification(""), 3200)
  }

  const formatDate = (value) => {
    if (!value) return "--"
    return new Intl.DateTimeFormat("en-US", {
      dateStyle: "medium",
      timeStyle: "short",
    }).format(new Date(value))
  }

  const getResponderName = (responder) => {
    if (!responder) return "Pending"
    return [responder.firstName, responder.lastName].filter(Boolean).join(" ") || responder.username || "Assigned"
  }

  const getResponderEta = (severity) => {
    if (!severity) return "15 min"
    return severity === "CRITICAL" ? "4–6 min" : severity === "HIGH" ? "6–9 min" : severity === "MODERATE" ? "8–12 min" : "12–18 min"
  }

  const toastClass = toastType === "error"
    ? "bg-rose-500/95 text-white"
    : "bg-emerald-500/95 text-slate-950"

  const toastShadowClass = toastType === "error"
    ? "shadow-2xl shadow-rose-500/20"
    : "shadow-2xl shadow-emerald-500/20"

  useEffect(() => {
    selectedAccidentRef.current = selectedAccident
  }, [selectedAccident])

  const loadAccidents = async () => {
    setLoading(true)
    setError("")
    try {
      const list = await fetchActiveAccidents()
      const accidentList = Array.isArray(list) ? list : []
      setAccidents(accidentList)
      const active = accidentList.find((incident) => incident.status && incident.status !== "RESOLVED") || accidentList[0] || null
      setSelectedAccident(active)
    } catch (err) {
      console.error(err)
      setError("Unable to load accident feed from the backend.")
      pushToast("Unable to fetch live incident data", "error")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadAccidents()
  }, [])

  useEffect(() => {
    if (!mapElement.current || mapInstance.current) return

    const map = L.map(mapElement.current, {
      center: [13.0827, 80.2707],
      zoom: 11,
      scrollWheelZoom: false,
    })

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: 19,
    }).addTo(map)

    mapInstance.current = map
  }, [])

  useEffect(() => {
    if (!mapInstance.current || !selectedAccident?.latitude || !selectedAccident?.longitude) return

    const { latitude, longitude } = selectedAccident
    const map = mapInstance.current

    if (markerLayer.current) {
      markerLayer.current.remove()
    }

    markerLayer.current = L.circleMarker([latitude, longitude], {
      radius: 10,
      color: "#f43f5e",
      fillColor: "#f87171",
      fillOpacity: 0.6,
    }).addTo(map)

    map.setView([latitude, longitude], 13, { animate: true })
  }, [selectedAccident])

  useEffect(() => {
    if (socketRef.current) return

    const backendHost = import.meta.env.VITE_API_HOST || (window.location.hostname === "localhost" ? "localhost:8080" : window.location.host)
    const protocol = window.location.protocol === "https:" ? "wss" : "ws"
    const url = `${protocol}://${backendHost}/ws/incidents/websocket`

    const socket = new WebSocket(url)
    socketRef.current = socket

    socket.addEventListener("open", () => {
      setSocketStatus("LIVE")
      socket.send("CONNECT\naccept-version:1.2\nheart-beat:10000,10000\n\n\u0000")
      socket.send("SUBSCRIBE\nid:sub-0\ndestination:/topic/incidents\n\n\u0000")
    })

    socket.addEventListener("message", (event) => {
      const raw = String(event.data)
      if (!raw.startsWith("MESSAGE")) return
      const body = raw.slice(raw.indexOf("\n\n") + 2).replace(/\u0000/g, "").trim()
      if (!body) return

      try {
        const payload = JSON.parse(body)
        if (!payload?.id) return

        setAccidents((previous) => {
          const updated = previous.filter((item) => item.id !== payload.id)
          return [payload, ...updated]
        })

        if (!selectedAccidentRef.current || payload.id === selectedAccidentRef.current.id) {
          setSelectedAccident(payload)
          pushToast("Live accident status updated")
        }
      } catch (err) {
        console.warn("WebSocket parse error", err)
      }
    })

    socket.addEventListener("close", () => setSocketStatus("DISCONNECTED"))
    socket.addEventListener("error", () => setSocketStatus("ERROR"))

    return () => {
      if (socket && socket.readyState === WebSocket.OPEN) {
        socket.close()
      }
    }
  }, [])

  const refreshAccident = async (id) => {
    if (!id) return
    try {
      const latest = await fetchAccidentById(id)
      setSelectedAccident(latest)
      setAccidents((prev) => {
        const updated = prev.map((item) => (item.id === id ? latest : item))
        if (!updated.some((item) => item.id === id)) updated.unshift(latest)
        return updated
      })
    } catch (err) {
      console.warn(err)
    }
  }

  const handleAction = async (actionKey, apiCall, successMessage) => {
    if (!selectedAccident?.id) return
    setActionState((prev) => ({ ...prev, [actionKey]: true }))
    setError("")

    try {
      await apiCall(selectedAccident.id)
      await refreshAccident(selectedAccident.id)
      pushToast(successMessage, "success")
    } catch (err) {
      console.error(err)
      const message = err?.message || "Action failed. Please try again."
      setError(message)
      pushToast(message, "error")
    } finally {
      setActionState((prev) => ({ ...prev, [actionKey]: false }))
    }
  }

  const createDemoAccident = async () => {
    setLoading(true)
    setError("")

    try {
      const demo = await createPublicAccident({
        title: "Demo Accident Alert",
        description: "A high-risk traffic accident triggered for live emergency dispatch testing.",
        latitude: 13.0827,
        longitude: 80.2707,
        locationName: "Chennai Demo Route",
        severity: "HIGH",
        casualties: 2,
        weatherCondition: "RAINY",
        trafficDensity: "HIGH",
        roadType: "HIGHWAY",
        imageUrl: "",
        videoUrl: "",
      })
      setSelectedAccident(demo)
      setAccidents((prev) => [demo, ...prev.filter((item) => item.id !== demo.id)])
      pushToast("Demo accident created")
    } catch (err) {
      console.error(err)
      setError("Unable to generate demo accident.")
      pushToast("Demo workflow failed. Check backend connectivity.", "error")
    } finally {
      setLoading(false)
    }
  }

  const activeIncident = selectedAccident?.status === "DISPATCHED" || selectedAccident?.status === "IN_PROGRESS"
  const statusLabel = selectedAccident?.status ? selectedAccident.status.replaceAll("_", " ") : "No active accident"
  const ambulanceDisabled = actionState.ambulance || !selectedAccident || Boolean(selectedAccident.ambulanceAssigned)
  const policeDisabled = actionState.police || !selectedAccident || Boolean(selectedAccident.policeAssigned)
  const hospitalDisabled = actionState.hospital || !selectedAccident || Boolean(selectedAccident.hospitalAssigned)

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-6 sm:p-10">
      <div className="flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between">
        <div>
          <p className="text-sm uppercase tracking-[0.3em] text-emerald-400">Emergency Intelligence</p>
          <h1 className="mt-3 text-4xl font-semibold tracking-tight text-white">Accident Analysis</h1>
          <p className="mt-4 max-w-2xl text-slate-400">Live incident diagnostics, emergency dispatch controls, and response coordination for active accidents.</p>
        </div>

        <div className="flex flex-col gap-3 rounded-3xl border border-white/10 bg-slate-900/80 p-4 text-sm text-slate-300 shadow-xl shadow-slate-950/30">
          <div className="flex items-center justify-between">
            <span>WebSocket</span>
            <span className={`rounded-full px-3 py-1 text-xs ${socketStatus === "LIVE" ? "bg-emerald-500/15 text-emerald-300" : "bg-rose-500/10 text-rose-300"}`}>{socketStatus}</span>
          </div>
          <button onClick={createDemoAccident} className="rounded-2xl bg-emerald-500 px-4 py-2 text-sm font-semibold text-slate-950 transition hover:bg-emerald-400">Generate Demo Accident</button>
        </div>
      </div>

      {loading ? (
        <div className="mt-8 grid gap-6 xl:grid-cols-[1.8fr_1fr] animate-pulse">
          <div className="space-y-6">
            <div className="h-60 rounded-3xl bg-slate-900/80" />
            <div className="h-64 rounded-3xl bg-slate-900/80" />
            <div className="h-72 rounded-3xl bg-slate-900/80" />
          </div>
          <div className="space-y-6">
            <div className="h-52 rounded-3xl bg-slate-900/80" />
            <div className="h-44 rounded-3xl bg-slate-900/80" />
            <div className="h-64 rounded-3xl bg-slate-900/80" />
          </div>
        </div>
      ) : (
        <div className="mt-8 grid gap-6 xl:grid-cols-[1.8fr_1fr]">
          <section className="space-y-6">
            <div className="rounded-3xl border border-white/10 bg-slate-900/80 p-6 shadow-xl shadow-slate-950/20">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <p className="text-sm uppercase tracking-[0.3em] text-slate-400">Status</p>
                  <h2 className="mt-2 text-3xl font-semibold text-white">{statusLabel}</h2>
                </div>
                <span className={`rounded-2xl px-4 py-2 text-sm ${activeIncident ? "bg-rose-500/15 text-rose-300" : "bg-emerald-500/15 text-emerald-300"}`}>{activeIncident ? "Active dispatch" : "Awaiting incident"}</span>
              </div>

              <div className="mt-6 grid gap-4 sm:grid-cols-2">
                <div className="rounded-3xl bg-slate-950/50 p-4">
                  <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Location</p>
                  <p className="mt-2 text-lg font-semibold text-white">{selectedAccident?.locationName || "No active accident"}</p>
                </div>
                <div className="rounded-3xl bg-slate-950/50 p-4">
                  <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Detected</p>
                  <p className="mt-2 text-lg font-semibold text-white">{formatDate(selectedAccident?.createdAt)}</p>
                </div>
              </div>
            </div>

            <div className="rounded-3xl border border-white/10 bg-slate-900/80 p-6 shadow-xl shadow-slate-950/30">
              <div className="grid gap-4 sm:grid-cols-2">
                <div className="rounded-3xl bg-slate-950/70 p-5">
                  <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Severity</p>
                  <p className="mt-3 text-3xl font-semibold text-rose-400">{selectedAccident?.severity || "-"}</p>
                  <p className="mt-2 text-xs uppercase tracking-[0.2em] text-slate-500">Score {selectedAccident?.riskScore ?? "--"}%</p>
                </div>
                <div className="rounded-3xl bg-slate-950/70 p-5">
                  <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Vehicles</p>
                  <p className="mt-3 text-3xl font-semibold text-white">{selectedAccident?.casualties ?? 0}</p>
                  <p className="mt-2 text-xs uppercase tracking-[0.2em] text-slate-500">{selectedAccident?.riskStatus || "Risk evaluated"}</p>
                </div>
              </div>

              <div className="mt-6 rounded-3xl bg-slate-950/70 p-5">
                <p className="text-sm uppercase tracking-[0.25em] text-slate-500">AI Analysis</p>
                <p className="mt-3 text-slate-300">{selectedAccident?.description || "AI analysis is not available for this incident yet."}</p>
              </div>
            </div>

            <div className="rounded-3xl border border-white/10 bg-slate-900/80 p-6 shadow-xl shadow-slate-950/30">
              <h3 className="text-xl font-semibold text-white">Emergency Timeline</h3>
              <div className="mt-5 space-y-4">
                {selectedAccident ? (
                  <>
                    <div className="rounded-3xl bg-slate-950/70 p-4">
                      <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Reported</p>
                      <p className="mt-2 text-sm text-slate-300">Detected at {formatDate(selectedAccident.createdAt)}</p>
                    </div>
                    {selectedAccident.ambulanceAssigned ? (
                      <div className="rounded-3xl bg-slate-950/70 p-4">
                        <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Ambulance dispatched</p>
                        <p className="mt-2 text-sm text-slate-300">{getResponderName(selectedAccident.ambulanceAssigned)} en route · ETA {getResponderEta(selectedAccident.severity)}</p>
                      </div>
                    ) : null}
                    {selectedAccident.policeAssigned ? (
                      <div className="rounded-3xl bg-slate-950/70 p-4">
                        <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Police notified</p>
                        <p className="mt-2 text-sm text-slate-300">{getResponderName(selectedAccident.policeAssigned)} alerted · ETA {getResponderEta(selectedAccident.severity)}</p>
                      </div>
                    ) : null}
                    {selectedAccident.hospitalAssigned ? (
                      <div className="rounded-3xl bg-slate-950/70 p-4">
                        <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Hospital coordination</p>
                        <p className="mt-2 text-sm text-slate-300">{getResponderName(selectedAccident.hospitalAssigned)} standing by · ETA {getResponderEta(selectedAccident.severity)}</p>
                      </div>
                    ) : null}
                  </>
                ) : (
                  <p className="text-sm text-slate-400">No timeline entries available.</p>
                )}
              </div>
            </div>
          </section>

          <aside className="space-y-6">
            <div className="rounded-3xl border border-white/10 bg-slate-900/80 p-6 shadow-xl shadow-slate-950/20">
              <div className="mb-6 flex items-center justify-between gap-3">
                <div>
                  <h3 className="text-lg font-semibold text-white">Live Response Controls</h3>
                  <p className="text-xs uppercase tracking-[0.25em] text-slate-500">RoadGuardian dispatch actions</p>
                </div>
                <span className="rounded-full bg-slate-950/70 px-3 py-1 text-xs uppercase tracking-[0.2em] text-slate-400">Instant action</span>
              </div>

              <div className="space-y-4">
                <button
                  onClick={() => handleAction("ambulance", dispatchAmbulance, "Ambulance dispatched")}
                  disabled={ambulanceDisabled}
                  className={`w-full rounded-3xl px-4 py-3 text-sm font-semibold transition-all duration-200 ${ambulanceDisabled ? "cursor-not-allowed bg-slate-700 text-slate-400 shadow-none" : "bg-rose-500 text-white shadow-lg shadow-rose-500/20 hover:bg-rose-400 hover:-translate-y-0.5"}`}
                >
                  {actionState.ambulance ? "Dispatching ambulance..." : selectedAccident?.ambulanceAssigned ? "Ambulance dispatched" : "Send Ambulance"}
                </button>

                <button
                  onClick={() => handleAction("police", notifyPolice, "Police notified")}
                  disabled={policeDisabled}
                  className={`w-full rounded-3xl px-4 py-3 text-sm font-semibold transition-all duration-200 ${policeDisabled ? "cursor-not-allowed bg-slate-700 text-slate-400 shadow-none" : "bg-slate-100 text-slate-950 shadow-lg shadow-slate-500/10 hover:bg-slate-200 hover:-translate-y-0.5"}`}
                >
                  {actionState.police ? "Notifying police..." : selectedAccident?.policeAssigned ? "Police notified" : "Notify Police"}
                </button>

                <button
                  onClick={() => handleAction("hospital", notifyHospital, "Hospital notified")}
                  disabled={hospitalDisabled}
                  className={`w-full rounded-3xl px-4 py-3 text-sm font-semibold transition-all duration-200 ${hospitalDisabled ? "cursor-not-allowed bg-slate-700 text-slate-400 shadow-none" : "bg-emerald-500 text-slate-950 shadow-lg shadow-emerald-500/20 hover:bg-emerald-400 hover:-translate-y-0.5"}`}
                >
                  {actionState.hospital ? "Coordinating hospital..." : selectedAccident?.hospitalAssigned ? "Hospital notified" : "Notify Hospital"}
                </button>
              </div>

              {error ? <div className="mt-5 rounded-3xl bg-rose-500/10 p-4 text-sm text-rose-200">{error}</div> : null}
            </div>

            <div className="rounded-3xl border border-white/10 bg-slate-900/80 p-6 shadow-xl shadow-slate-950/20">
              <h3 className="text-lg font-semibold text-white">Incident Location</h3>
              <p className="mt-2 text-sm text-slate-400">Coordinates and route preview for the selected accident.</p>
              <div ref={mapElement} className="mt-4 h-72 w-full rounded-3xl border border-white/10" />
            </div>

            <div className="rounded-3xl border border-white/10 bg-slate-900/80 p-6 shadow-xl shadow-slate-950/20">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Emergency status</p>
                  <p className="mt-2 text-xl font-semibold text-white">{statusLabel}</p>
                </div>
                <div className="rounded-full bg-slate-950/70 px-3 py-1 text-xs text-slate-400">Risk {selectedAccident?.riskScore ?? "--"}%</div>
              </div>

              <div className="mt-6 grid gap-4 text-sm text-slate-300">
                <div className="rounded-3xl bg-slate-950/70 p-4">
                  <p className="text-slate-500">Traffic</p>
                  <p className="mt-1 text-white">{selectedAccident?.trafficDensity || "Unknown"}</p>
                </div>
                <div className="rounded-3xl bg-slate-950/70 p-4">
                  <p className="text-slate-500">Weather</p>
                  <p className="mt-1 text-white">{selectedAccident?.weatherCondition || "Unknown"}</p>
                </div>
              </div>
            </div>

            <div className="rounded-3xl border border-white/10 bg-slate-900/80 p-6 shadow-xl shadow-slate-950/20">
              <h3 className="text-lg font-semibold text-white">Incident List</h3>
              <div className="mt-5 flex flex-col gap-3">
                {accidents.length > 0 ? accidents.map((item) => (
                  <button
                    key={item.id}
                    onClick={() => setSelectedAccident(item)}
                    className={`rounded-3xl border p-4 text-left transition ${selectedAccident?.id === item.id ? "border-emerald-400 bg-emerald-500/10" : "border-white/10 bg-slate-950/70 hover:border-slate-400"}`}
                  >
                    <p className="text-sm uppercase tracking-[0.2em] text-slate-400">{item.locationName || "Unknown location"}</p>
                    <p className="mt-2 font-semibold text-white">{item.severity || "Unknown"}</p>
                    <p className="mt-1 text-xs text-slate-500">{formatDate(item.createdAt)}</p>
                  </button>
                )) : (
                  <p className="text-sm text-slate-400">No accident history available.</p>
                )}
              </div>
            </div>
          </aside>
        </div>
      )}

      {!loading && !selectedAccident && (
        <div className="mt-8 rounded-3xl border border-dashed border-slate-700 bg-slate-900/80 p-6 text-center text-slate-400">
          <p className="text-lg text-white">No active accidents found.</p>
          <p className="mt-2">Use the demo button above to simulate an incident.</p>
        </div>
      )}

      {notification && (
        <div className={`fixed right-6 top-6 rounded-3xl px-5 py-3 text-sm font-semibold ${toastClass} ${toastShadowClass}`}>
          {notification}
        </div>
      )}
    </div>
  )
}

export default AccidentDetails