package com.poicraft.bot.v4.plugin.data.http

import kotlinx.serialization.Serializable

@Serializable
data class AlertManagerWebhookData(
    val receiver: String,
    val status: String,
    val alerts: List<Alert>,
    val groupLabels: GroupLabels,
    val commonLabels: Labels,
    val commonAnnotations: Annotations,
    val externalURL: String,
    val version: String,
    val groupKey: String,
    val truncatedAlerts: Long
)

@Serializable
data class Alert(
    val status: String,
    val labels: Labels,
    val annotations: Annotations,
    val startsAt: String,
    val endsAt: String,
    val generatorURL: String,
    val fingerprint: String
)

@Serializable
data class Annotations(
    val description: String? = "",
    val summary: String? = ""
)

@Serializable
data class Labels(
    val alertname: String? = "",
    val instance: String? = "",
    val severity: String? = ""
)

@Serializable
class GroupLabels
