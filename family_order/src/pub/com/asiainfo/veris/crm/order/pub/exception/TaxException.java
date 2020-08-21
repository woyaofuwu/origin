/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TaxException implements IBusiException
{
    CRM_TAX_1("税率配置错误，请检查"), CRM_TAX_2("根据台账ID[%s]获取打印无数据!"), CRM_TAX_3("获取发票号出现异常!"), CRM_TAX_4("请先获取发票号！"), CRM_TAX_5("没有可打的费用金额！"), CRM_TAX_6("未知的发票类型！"), CRM_TAX_7("发票检验出错！"), CRM_TAX_8("调用发票 占用流程ITicketdUse出错!"), CRM_TAX_9(
            "该增值税发票[tradeId:%s]已于[%s | %s]打印!"), CRM_TAX_10("根据[ELEMENT_ID:%s]查询TD_B_ELEMENT_TAX税率配置错误, 请检查!"), CRM_TAX_11("根据[TRADE_TYPE_CODE:%s]查询TD_B_FEEITEM_TAX税率无数据, 请检查!"), CRM_TAX_12("查询到当前客户号码[GROUP_ID:%s]无相应的生效已审批资质信息！"), CRM_TAX_13(
            "该客户为非增值税纳税人!"), CRM_TAX_14("您不是该集团的客户经理，不能受理该业务！该集团归属客户经理标识=[%s]，归属客户经理姓名=[%s]!"), CRM_TAX_15("根据集团客户编码[%s]查询集团纳税人资质信息无数据!"), CRM_TAX_16("根据TRADE_ID=[%s]查询受理日志信息无数据!"), CRM_TAX_17("根据TRADE_ID=[%s]查询增值税信息无数据!"), CRM_TAX_18(
            "插入[TF_LOG_APPROVE_RECEIPT]表数据失败!"), CRM_TAX_19("调用账务接口[AM_CRM_VatInvoiceApply]未返回[TASK_ID]信息!"), CRM_TAX_20("调用客户管理接口[CM.CustomerSVC.getTaxApplyInfo]出错:%s!");

    private final String value;

    private TaxException(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

}
