import { Link } from "react-router-dom"

const LandingPage = () => {
  return (
    <div className="min-h-screen bg-black text-white px-6 relative overflow-hidden">
        <nav className="relative z-10 flex items-center justify-between py-6 max-w-7xl mx-auto">

  <h1 className="text-2xl font-bold">
    RoadGuardian AI
  </h1>

  <div className="flex gap-6 text-sm text-gray-300">
    <Link to="/">Home</Link>
    <a href="#features">Features</a>
    <Link to="/dashboard">Dashboard</Link>
    <Link to="/accident">Contact</Link>
  </div>

</nav>

      <div className="absolute inset-0 bg-gradient-to-b from-red-500/10 via-black to-black"></div>

      <div className="relative z-10 flex flex-col items-center justify-center min-h-[85vh]">

        <p className="uppercase tracking-[0.3em] text-red-400 text-sm mb-4">
          AI Powered Road Safety
        </p>

        <h1 className="text-6xl md:text-7xl font-bold text-center leading-tight">
          RoadGuardian AI
        </h1>

        <p className="mt-6 text-gray-400 text-center max-w-2xl text-lg">
          Intelligent accident detection, emergency response, and real-time road safety monitoring platform.
        </p>

        <div className="flex gap-4 mt-10">
          <Link
            to="/dashboard"
            className="bg-red-500 hover:bg-red-600 px-6 py-3 rounded-xl font-semibold transition"
          >
            Live Monitoring
          </Link>

          <Link
            to="/dashboard"
            className="border border-white/20 hover:border-white px-6 py-3 rounded-xl font-semibold transition"
          >
            Explore Dashboard
          </Link>
        </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-20 w-full max-w-5xl">

  <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-lg hover:scale-105 transition duration-300">
    <h2 className="text-4xl font-bold text-red-400">2.4s</h2>
    <p className="text-gray-400 mt-2">Average AI Detection Time</p>
  </div>

  <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-lg hover:scale-105 transition duration-300">
    <h2 className="text-4xl font-bold text-red-400">98%</h2>
    <p className="text-gray-400 mt-2">Accident Detection Accuracy</p>
  </div>

  <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-lg hover:scale-105 transition duration-300">
    <h2 className="text-4xl font-bold text-red-400">24/7</h2>
    <p className="text-gray-400 mt-2">Real-Time Emergency Monitoring</p>
  </div>

</div>
      </div>
    </div>
  )
}

export default LandingPage