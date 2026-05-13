import { useEffect, useState, useCallback } from "react"
import {
  fetchAccidents,
  fetchMetrics,
  createAccident
} from "../services/api"
import { AlertTriangle, MapPin, Activity, Car } from "lucide-react"
import { Link } from "react-router-dom"

const Dashboard = () => {

  const [accidents, setAccidents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [metrics, setMetrics] = useState({})
  const [formData, setFormData] = useState({
    location: "",
    severity: "MODERATE",
    status: "Emergency Active"
  })

  const loadData = useCallback(async () => {
    try {
      setLoading(true)
      setError("")
      const accidentData = await fetchAccidents()
      const metricsData = await fetchMetrics()
      setAccidents(accidentData)
      setMetrics(metricsData)
    } catch (err) {
      console.error("Error fetching accidents:", err)
      setError("Unable to load accident data.")
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadData()

    const interval = setInterval(() => {
      loadData()
    }, 5000)

    return () => clearInterval(interval)
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()

    await createAccident(formData)

    setFormData({
      location: "",
      severity: "MODERATE",
      status: "Emergency Active"
    })

    loadData()
  }

  const activeAlerts = accidents.length
  const criticalCases = accidents.filter(
    (item) => (item.severity || item.status || "").toString().toLowerCase() === "critical"
  ).length
  const locationsMonitored = new Set(
    accidents.map((item) => item.location || item.city || item.area || "Unknown")
  ).size

  const stats = accidents.length
    ? { activeAlerts, criticalCases, locationsMonitored }
    : { activeAlerts: metrics.totalAccidents, criticalCases: metrics.criticalCases, locationsMonitored: 8 }

  if (loading) {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center text-white">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-red-500 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-xl font-semibold">
            Loading Emergency Dashboard...
          </p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-black text-white flex">

      {/* Sidebar */}
      <div className="hidden md:block w-64 bg-black border-r border-white/10 p-6">

        <h1 className="text-xl font-bold mb-8">
          RoadGuardian AI
        </h1>

        <div className="space-y-6 text-gray-300">

          <Link to="/" className="hover:text-white block transition">
            Home
          </Link>

          <Link to="/dashboard" className="text-white font-semibold block">
            Dashboard
          </Link>

          <Link to="/accident" className="hover:text-white block transition">
            Accident Details
          </Link>

        </div>

      </div>

      {/* Main Content */}
      <div className="flex-1 p-4 md:p-10 bg-gradient-to-b from-black via-red-950/10 to-black">

        {/* Header */}
        <div className="flex flex-wrap items-center gap-4">

          <h1 className="text-3xl md:text-4xl font-bold">
            Emergency Dashboard
          </h1>

          {/* LIVE Badge */}
          <div className="relative flex items-center justify-center">

            <span className="absolute inline-flex h-full w-full rounded-full bg-red-500 opacity-75 animate-ping"></span>

            <span className="relative bg-red-600 text-white text-xs px-4 py-1 rounded-full">
              LIVE
            </span>

          </div>

        </div>

        {/* Status Bar */}
        <div className="flex flex-wrap items-center gap-3 mt-3">

          <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>

          <p className="text-sm text-gray-400">
            System Operational • AI Monitoring Active • 24/7 Protection Enabled
          </p>

        </div>

        {/* Subtitle */}
        <p className="text-gray-400 mt-4 mb-8 text-base md:text-lg">
          Real-time accident monitoring system
        </p>

        <div className="h-px bg-white/10 my-8"></div>

        {/* Report Accident Form */}
        <div className="mb-10">

          <h2 className="text-2xl font-bold mb-4">
            Report New Accident
          </h2>

          <form
            onSubmit={handleSubmit}
            className="bg-white/5 border border-white/10 rounded-2xl p-6 space-y-4"
          >

            <input
              type="text"
              placeholder="Accident Location"
              value={formData.location}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  location: e.target.value
                })
              }
              className="w-full bg-black border border-white/10 rounded-xl p-3 text-white outline-none"
              required
            />

            <select
              value={formData.severity}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  severity: e.target.value
                })
              }
              className="w-full bg-black border border-white/10 rounded-xl p-3 text-white outline-none"
            >

              <option>CRITICAL</option>
              <option>MODERATE</option>
              <option>LOW</option>

            </select>

            <button
              type="submit"
              className="bg-red-600 hover:bg-red-700 transition px-6 py-3 rounded-xl font-semibold"
            >
              Submit Accident Report
            </button>

          </form>

        </div>

        {/* Live Accident Feed */}
        <div>

          <h2 className="text-2xl font-bold mb-4">
            Live Accident Feed
          </h2>

          <div className="space-y-4">
            {loading ? (
              <div className="bg-white/5 border border-white/10 rounded-xl p-6 text-center text-gray-300">
                Loading accident feed...
              </div>
            ) : error ? (
              <div className="bg-white/5 border border-white/10 rounded-xl p-6 text-center text-red-300">
                {error}
              </div>
            ) : accidents.length === 0 ? (
              <div className="text-center text-gray-500 py-10">
                No active accidents detected
              </div>
            ) : (
              accidents.map((accident) => (
                <div
                  key={accident.id}
                  className="bg-red-500/10 border border-red-500/30 rounded-xl p-4 hover:scale-[1.02] transition duration-300"
                >
                  <p className="text-red-400 font-semibold">
                    {accident.severity}
                  </p>
                  <p className="text-gray-300">
                    {accident.location}
                  </p>
                  <p className="text-gray-500 text-sm">
                    {accident.status}
                  </p>
                  <p className="text-white text-sm mt-2">
                    AI Risk Score: {accident.riskScore}
                  </p>
                </div>
              ))
            )}
          </div>

        </div>

        <div className="h-px bg-white/10 my-8"></div>

        {/* Map Section */}
        <div>

          <h2 className="text-2xl font-bold mb-4">
            Live Accident Map
          </h2>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-8 hover:scale-[1.01] transition duration-300">

            <div className="text-center">

              <p className="text-gray-300 text-xl font-semibold">
                Map Integration Coming
              </p>

              <p className="text-gray-500 text-sm mt-2">
                (Google Maps / OpenStreetMap will be connected in backend phase)
              </p>

            </div>

          </div>

        </div>

        <div className="h-px bg-white/10 my-8"></div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6">

          {/* Active Alerts */}
          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 shadow-lg hover:shadow-red-500/10 hover:scale-[1.03] transition duration-300">

            <Activity className="text-red-400 mb-3" size={28} />

            <h2 className="text-3xl font-bold">
              {stats.activeAlerts}
            </h2>

            <p className="text-gray-400 mt-1">
              Active Alerts
            </p>

          </div>

          {/* Critical Cases */}
          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 shadow-lg hover:shadow-red-500/10 hover:scale-[1.03] transition duration-300">

            <AlertTriangle className="text-red-400 mb-3" size={28} />

            <h2 className="text-3xl font-bold">
              {stats.criticalCases}
            </h2>

            <p className="text-gray-400 mt-1">
              Critical Cases
            </p>

          </div>

          {/* Locations */}
          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 shadow-lg hover:shadow-red-500/10 hover:scale-[1.03] transition duration-300">

            <MapPin className="text-red-400 mb-3" size={28} />

            <h2 className="text-3xl font-bold">
              {stats.locationsMonitored}
            </h2>

            <p className="text-gray-400 mt-1">
              Locations Monitored
            </p>

          </div>

          {/* System Active */}
          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 shadow-lg hover:shadow-red-500/10 hover:scale-[1.03] transition duration-300">

            <Car className="text-red-400 mb-3" size={28} />

            <h2 className="text-3xl font-bold">
              24/7
            </h2>

            <p className="text-gray-400 mt-1">
              System Active
            </p>

          </div>

        </div>

        {/* Footer */}
        <div className="mt-16 text-center text-gray-500 text-sm">

          <p>
            RoadGuardian AI © 2026
          </p>

          <p className="mt-2">
            AI-Powered Smart Emergency Monitoring System
          </p>

        </div>

      </div>

    </div>
  )
}

export default Dashboard