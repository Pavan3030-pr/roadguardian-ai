const BASE_API_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1/accidents"
const ANALYTICS_URL = import.meta.env.VITE_API_ANALYTICS_URL || "http://localhost:8080/api/v1/analytics/dashboard"
const BASE_URL = BASE_API_URL

async function parseApiResponse(response) {
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    const errorMessage = body?.message || body?.error || response.statusText
    throw new Error(errorMessage || "API request failed")
  }
  return body?.data ?? body
}

export const fetchAccidents = async () => {
  const response = await fetch(BASE_URL)
  return parseApiResponse(response)
}

export const fetchActiveAccidents = async () => {
  const response = await fetch(`${BASE_URL}/active`)
  return parseApiResponse(response)
}

export const fetchAccidentById = async (id) => {
  const response = await fetch(`${BASE_URL}/${id}`)
  return parseApiResponse(response)
}

export const createAccident = async (accidentData) => {
  const response = await fetch(BASE_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(accidentData),
  })
  return parseApiResponse(response)
}

export const createPublicAccident = async (accidentData) => {
  const response = await fetch(`${BASE_URL}/public`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(accidentData),
  })
  return parseApiResponse(response)
}

export const dispatchAmbulance = async (accidentId) => {
  const response = await fetch(`${BASE_URL}/${accidentId}/ambulance`, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const notifyPolice = async (accidentId) => {
  const response = await fetch(`${BASE_URL}/${accidentId}/police`, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const notifyHospital = async (accidentId) => {
  const response = await fetch(`${BASE_URL}/${accidentId}/hospital`, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const assignAmbulance = async (accidentId) => {
  const response = await fetch(`${BASE_URL}/${accidentId}/assign-ambulance`, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const assignPolice = async (accidentId) => {
  const response = await fetch(`${BASE_URL}/${accidentId}/assign-police`, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const assignHospital = async (accidentId) => {
  const response = await fetch(`${BASE_URL}/${accidentId}/assign-hospital`, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const fetchMetrics = async () => {
  const response = await fetch(ANALYTICS_URL)
  return parseApiResponse(response)
}
