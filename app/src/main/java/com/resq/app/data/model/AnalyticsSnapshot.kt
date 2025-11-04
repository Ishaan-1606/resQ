package com.resq.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsSnapshot(
    val timestamp: String? = null,
    val sensors: Sensors,
    @SerialName("ai_prediction")
    val aiPrediction: AiPrediction,
    @SerialName("hybrid_analysis")
    val hybridAnalysis: HybridAnalysis? = null,
    val servos: Servos? = null,
    val alerts: List<Alert>? = null
)

@Serializable
data class Sensors(
    val it: Float,
    val ih: Float,
    val ot: Float,
    val oh: Float,
    val gas: Int,
    val cur: Float,
    val iv: Int,
    val ov: Int,
    val ir: Int,
    val ax: Float,
    val ay: Float,
    val az: Float,
    @SerialName("rms_accel")
    val rmsAccel: Float? = null
)

@Serializable
data class AiPrediction(
    val prediction: String,
    val confidence: Float,
    val probabilities: Map<String, Float>,
    @SerialName("ml_prediction")
    val mlPrediction: String? = null,
    @SerialName("ml_confidence")
    val mlConfidence: Float? = null,
    @SerialName("decision_method")
    val decisionMethod: String? = null,
    @SerialName("rules_triggered")
    val rulesTriggered: String? = null
)

@Serializable
data class HybridAnalysis(
    @SerialName("rules_prediction")
    val rulesPrediction: String,
    @SerialName("rules_confidence")
    val rulesConfidence: Float,
    @SerialName("combined_decision")
    val combinedDecision: String,
    val explanation: String
)

@Serializable
data class Servos(
    val s1: Int
)

@Serializable
data class Alert(
    val message: String,
    val timestamp: String
)
