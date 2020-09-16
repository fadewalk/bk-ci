package com.tencent.devops.environment.websocket.push

import com.fasterxml.jackson.databind.ObjectMapper
import com.tencent.devops.common.redis.RedisOperation
import com.tencent.devops.common.websocket.dispatch.message.SendMessage
import com.tencent.devops.common.websocket.dispatch.push.WebsocketPush
import com.tencent.devops.common.websocket.pojo.NotifyPost
import com.tencent.devops.common.websocket.pojo.WebSocketType
import com.tencent.devops.environment.websocket.NodeMessage

data class NodeWebsocketPush(
		val projectId: String,
		override val userId: String,
		override val pushType: WebSocketType,
		override val redisOperation: RedisOperation,
		override val objectMapper: ObjectMapper,
		override var page: String?,
		override var notifyPost: NotifyPost
): WebsocketPush(userId, pushType, redisOperation, objectMapper, page, notifyPost) {
	override fun findSession(page: String): List<String>? {
		return super.findSession(page)
	}

	override fun buildMqMessage(): SendMessage? {
		return NodeMessage(
				project = projectId,
				userId =  userId,
				page = page,
				sessionList = findSession(page!!),
				notifyPost = notifyPost
		)
	}

	override fun buildNotifyMessage(message: SendMessage) {

	}
}