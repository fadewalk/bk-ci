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
 */

package com.tencent.devops.store.pojo.atom

import com.tencent.devops.store.pojo.atom.enums.JobTypeEnum
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("插件运行时信息")
data class AtomRunInfo(
    @ApiModelProperty("插件代码", required = true)
    val atomCode: String,
    @ApiModelProperty("插件名称", required = true)
    var atomName: String,
    @ApiModelProperty("插件版本号", required = true)
    val version: String,
    @ApiModelProperty("插件状态")
    val atomStatus: Byte? = null,
    @ApiModelProperty("插件初始化项目代码", required = true)
    val initProjectCode: String,
    @ApiModelProperty("Job类型", required = false)
    var jobType: JobTypeEnum?,
    @ApiModelProperty("无构建环境插件是否可以在有构建环境运行标识", required = false)
    var buildLessRunFlag: Boolean?,
    @ApiModelProperty("输入字段类型信息数据", required = false)
    var inputTypeInfos: Map<String, String>?
)
