const AccidentDetails = () => {
  return (
    <div className="min-h-screen bg-black text-white p-8">

      <h1 className="text-3xl font-bold mb-2">
        Accident Analysis
      </h1>

      <p className="text-gray-400 mb-8">
        AI-generated accident report and emergency response details
      </p>

      {/* Main Box */}
      <div className="bg-white/5 border border-white/10 rounded-xl p-6">

        <h2 className="text-xl font-semibold text-red-400">
          CRITICAL ACCIDENT DETECTED
        </h2>

        <p className="text-gray-300 mt-2">
          Location: Chennai Highway (NH-45)
        </p>

        <p className="text-gray-300">
          Severity: High impact collision detected by AI model
        </p>

        <p className="text-gray-300">
          Vehicles involved: 2
        </p>

      </div>

      {/* AI Analysis */}
      <div className="mt-6 bg-white/5 border border-white/10 rounded-xl p-6">

        <h2 className="text-xl font-semibold mb-2">
          AI Analysis
        </h2>

        <p className="text-gray-400">
          The system detected sudden deceleration, impact patterns, and visual damage suggesting a high severity road accident. Immediate emergency response recommended.
        </p>

      </div>

      {/* Actions */}
      <div className="mt-6 flex gap-4">

        <button className="bg-red-500 px-6 py-3 rounded-xl font-semibold">
          Send Ambulance
        </button>

        <button className="bg-white/10 px-6 py-3 rounded-xl font-semibold">
          Notify Police
        </button>

      </div>

    </div>
  )
}

export default AccidentDetails