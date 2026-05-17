const DEFAULT_API_PATH = "/api/v1/accidents"
const DEFAULT_ANALYTICS_PATH = "/api/v1/analytics/dashboard"

const normalizeUrl = (rawUrl, defaultPath) => {
  if (!rawUrl) return null
  const url = rawUrl.trim().replace(/\/+$/, "")
  if (url.startsWith("http://") || url.startsWith("https://")) {
    return url.includes(defaultPath) ? url : `${url}${defaultPath}`
  }
  if (url.startsWith("/")) {
    return url.includes(defaultPath) ? url : `${url}${defaultPath}`
  }
  return url.includes(defaultPath) ? `https://${url}` : `https://${url}${defaultPath}`
}

const BASE_API_URL = normalizeUrl(import.meta.env.VITE_API_BASE_URL, DEFAULT_API_PATH) || `http://localhost:8080${DEFAULT_API_PATH}`
const ANALYTICS_URL = normalizeUrl(import.meta.env.VITE_API_ANALYTICS_URL, DEFAULT_ANALYTICS_PATH)
  || normalizeUrl(import.meta.env.VITE_API_BASE_URL, DEFAULT_ANALYTICS_PATH)
  || `http://localhost:8080${DEFAULT_ANALYTICS_PATH}`
const BASE_URL = BASE_API_URL

console.debug("[API] BASE_URL:", BASE_URL)
console.debug("[API] ANALYTICS_URL:", ANALYTICS_URL)

const fetchWithLogging = async (url, options = {}) => {
  const method = (options.method || "GET").toUpperCase()
  console.debug(`[API] Request: ${method} ${url}`, options.body ? { body: options.body } : {})
  const response = await fetch(url, options)
  const clone = response.clone()
  clone.text().then((text) => {
    let parsedBody = text
    try {
      parsedBody = JSON.parse(text)
    } catch (ignore) {
      // preserve raw text
    }
    console.debug(`[API] Response: ${method} ${url}`, {
      status: response.status,
      ok: response.ok,
      body: parsedBody,
    })
  }).catch((err) => {
    console.warn("[API] Response parse failed", err)
  })
  return response
}

async function parseApiResponse(response) {
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    const errorMessage = body?.message || body?.error || response.statusText
    throw new Error(errorMessage || "API request failed")
  }
  return body?.data ?? body
}

export const fetchAccidents = async () => {
  const response = await fetchWithLogging(BASE_URL)
  return parseApiResponse(response)
}

export const fetchActiveAccidents = async () => {
  const url = `${BASE_URL}/active`
  const response = await fetchWithLogging(url)
  return parseApiResponse(response)
}

export const fetchAccidentById = async (id) => {
  const url = `${BASE_URL}/${id}`
  const response = await fetchWithLogging(url)
  return parseApiResponse(response)
}

export const createAccident = async (accidentData) => {
  const response = await fetchWithLogging(BASE_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(accidentData),
  })
  return parseApiResponse(response)
}

export const createPublicAccident = async (accidentData) => {
  const url = `${BASE_URL}/public`
  const response = await fetchWithLogging(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(accidentData),
  })
  return parseApiResponse(response)
}

export const createDemoAccident = async () => {
  const url = `${BASE_URL}/demo`
  const response = await fetchWithLogging(url, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const dispatchAmbulance = async (accidentId) => {
  const url = `${BASE_URL}/${accidentId}/ambulance`
  const response = await fetchWithLogging(url, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const notifyPolice = async (accidentId) => {
  const url = `${BASE_URL}/${accidentId}/police`
  const response = await fetchWithLogging(url, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const notifyHospital = async (accidentId) => {
  const url = `${BASE_URL}/${accidentId}/hospital`
  const response = await fetchWithLogging(url, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const assignAmbulance = async (accidentId) => {
  const url = `${BASE_URL}/${accidentId}/assign-ambulance`
  const response = await fetchWithLogging(url, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const assignPolice = async (accidentId) => {
  const url = `${BASE_URL}/${accidentId}/assign-police`
  const response = await fetchWithLogging(url, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const assignHospital = async (accidentId) => {
  const url = `${BASE_URL}/${accidentId}/assign-hospital`
  const response = await fetchWithLogging(url, {
    method: "POST",
  })
  return parseApiResponse(response)
}

export const fetchMetrics = async () => {
  const response = await fetchWithLogging(ANALYTICS_URL)
  return parseApiResponse(response)
}
