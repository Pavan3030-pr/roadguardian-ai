const BASE_URL = "http://localhost:8081/api/accidents"

export const fetchAccidents = async () => {

  const response = await fetch(BASE_URL)

  return response.json()
}

export const fetchMetrics = async () => {

  const response =
    await fetch(`${BASE_URL}/metrics`)

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