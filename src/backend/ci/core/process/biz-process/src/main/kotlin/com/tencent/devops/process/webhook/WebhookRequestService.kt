/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.tencent.devops.process.webhook

import com.tencent.devops.common.api.enums.ScmType
import com.tencent.devops.common.api.exception.ErrorCodeException
import com.tencent.devops.common.client.Client
import com.tencent.devops.common.pipeline.enums.ChannelCode
import com.tencent.devops.common.webhook.pojo.WebhookRequest
import com.tencent.devops.common.webhook.pojo.WebhookRequestReplay
import com.tencent.devops.common.webhook.pojo.code.github.GithubCheckRunEvent
import com.tencent.devops.process.api.service.ServiceBuildResource
import com.tencent.devops.process.constant.ProcessMessageCode
import com.tencent.devops.process.trigger.WebhookTriggerService
import com.tencent.devops.repository.api.ServiceRepositoryWebhookResource
import com.tencent.devops.repository.pojo.RepositoryWebhookRequest
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class WebhookRequestService(
    private val client: Client,
    private val webhookEventFactory: WebhookEventFactory,
    private val webhookTriggerService: WebhookTriggerService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(WebhookRequestService::class.java)
    }

    fun handleRequest(scmType: ScmType, request: WebhookRequest) {
        val event = webhookEventFactory.parseEvent(scmType = scmType, request = request) ?: run {
            logger.warn("Failed to parse webhook event")
            return
        }
        if (event is GithubCheckRunEvent) {
            githubRetry(event)
            return
        }
        val matcher = webhookEventFactory.createScmWebHookMatcher(scmType = scmType, event = event)

        val eventTime = LocalDateTime.now()
        val repositoryWebhookRequest = RepositoryWebhookRequest(
            externalId = matcher.getExternalId(),
            eventType = matcher.getEventType().name,
            triggerUser = matcher.getUsername(),
            eventMessage = matcher.getMessage() ?: "",
            repositoryType = scmType.name,
            requestHeader = request.headers,
            requestParam = request.queryParams,
            requestBody = request.body,
            createTime = eventTime
        )
        val hookRequestId = client.get(ServiceRepositoryWebhookResource::class).saveWebhookRequest(
            repositoryWebhookRequest = repositoryWebhookRequest
        ).data!!
        webhookTriggerService.trigger(
            scmType = scmType,
            matcher = matcher,
            hookRequestId = hookRequestId,
            eventTime = eventTime
        )
    }

    fun replay(scmType: ScmType, request: WebhookRequestReplay) {
        val repoWebhookRequest = client.get(ServiceRepositoryWebhookResource::class).getWebhookRequest(
            requestId = request.hookRequestId
        ).data ?: throw ErrorCodeException(
            errorCode = ProcessMessageCode.ERROR_WEBHOOK_REQUEST_NOT_FOUND,
            params = arrayOf(request.hookRequestId.toString())
        )
        val webhookRequest = WebhookRequest(
            headers = repoWebhookRequest.requestHeader,
            body = repoWebhookRequest.requestBody
        )
        val event = webhookEventFactory.parseEvent(scmType = scmType, request = webhookRequest) ?: run {
            logger.warn("Failed to parse webhook event")
            return
        }
        val matcher = webhookEventFactory.createScmWebHookMatcher(scmType = scmType, event = event)

        val eventTime = LocalDateTime.now()
        webhookTriggerService.replay(
            scmType = scmType,
            request = request,
            matcher = matcher,
            eventTime = eventTime
        )
    }

    private fun githubRetry(event: GithubCheckRunEvent) {
        if (event.action != "rerequested") {
            logger.info("Unsupported check run action:${event.action}")
            return
        }
        if (event.checkRun.externalId == null) {
            logger.info("github check run externalId is empty")
            return
        }
        val buildInfo = event.checkRun.externalId!!.split("_")
        if (buildInfo.size < 4) {
            logger.info("the buildInfo of github check run is error")
            return
        }
        client.get(ServiceBuildResource::class).retry(
            userId = buildInfo[0],
            projectId = buildInfo[1],
            pipelineId = buildInfo[2],
            buildId = buildInfo[3],
            channelCode = ChannelCode.BS
        )
    }
}
