import { BrowserRouter, Routes, Route } from "react-router-dom"
import LandingPage from "./pages/LandingPage"
import Dashboard from "./pages/Dashboard"
import AccidentDetails from "./pages/AccidentDetails"

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/accident" element={<AccidentDetails />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App