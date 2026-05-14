const BASE_URL = "http://localhost:8080/api/v1/accidents"

export const fetchAccidents = async () => {

  const response = await fetch(BASE_URL)

  return response.json()
}

export const fetchMetrics = async () => {

  const response =
    await fetch("http://localhost:8080/api/v1/analytics/dashboard")

  return response.json()
}

export const createAccident = async (accidentData) => {

  const response = await fetch(BASE_URL, {

    method: "POST",

    headers: {
      "Content-Type": "application/json",
    },

    body: JSON.stringify(accidentData),
  })

  return response.json()
}