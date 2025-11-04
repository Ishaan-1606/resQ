package com.resq.app.data.util

object AnalyticsFixture {

    const val sampleJson = """
    {
      "timestamp":"2025-10-27T12:34:56Z",
      "sensors":{
        "it":24.1,"ih":55.2,"ot":23.0,"oh":49.5,
        "gas":123,"cur":0.02,"iv":0,"ov":0,"ir":1,
        "ax":0.01,"ay":0.02,"az":0.00,"rms_accel":0.025
      },
      "ai_prediction":{
        "prediction":"Normal",
        "confidence":98.2,
        "probabilities":{"Normal":98.2,"Fire":0.8},
        "ml_prediction":"Normal",
        "ml_confidence":98.2,
        "decision_method":"Hybrid",
        "rules_triggered":""
      },
      "hybrid_analysis": {
        "rules_prediction":"Normal",
        "rules_confidence":95.0,
        "combined_decision":"Normal",
        "explanation":"ML and rules agree"
      },
      "servos":{"s1":90},
      "alerts":[{"message":"Gas spike detected","timestamp":"2025-10-27T12:34:50Z"}]
    }
    """
}
