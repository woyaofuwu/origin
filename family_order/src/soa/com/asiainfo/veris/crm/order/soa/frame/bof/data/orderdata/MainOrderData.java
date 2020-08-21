
package com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

/**
 * @author Administrator
 */
public class MainOrderData
{
    private String acceptDate;

    private String actorName;

    private String actorPhone;

    private String actorPsptId;

    private String actorPsptTypeCode;

    private String advancePay;

    private String appType;

    private String authCode;

    private String batchCount;

    private String batchId;

    private String cancelCityCode;

    private String cancelDate;

    private String cancelDepartId;

    private String cancelEparchyCode;

    private String cancelStaffId;

    private String cancelTag;

    private String cityCode;

    private String contractId;

    private String custContactId;

    private String custId;

    private String custIdea;

    private String custName;

    private String decomposeRuleId;

    private String dispatchRuleId;

    private String eparchyCode;

    private String execAction;

    private String execDesc;

    private String execResult;

    private String execTime;

    private String failTotal;

    private String feeStaffId;

    private String feeState;

    private String feeTime;

    private String finishDate;

    private String foregift;

    private String hqTag;

    private String invoiceNo;

    private String inModeCode;

    private String isNeedHumancheck;

    private String nextDealTag;

    private String operFee;

    private String orderId;

    private String orderInstanceState;

    private String orderState;

    private String orderTypeCode;

    private String priority;

    private String priorityType;

    private String processTagSet;

    private String psptId;

    private String psptTypeCode;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr10;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String servReqId;

    private String solutionId;

    private String succTotal;

    private String termIp;

    private String tradeCityCode;

    private String tradeDepartId;

    private String tradeEparchyCode;

    private String tradeStaffId;

    private String tradeTypeCode;

    private String orderKindCode;

    private String subscribeType;

    public MainOrderData()
    {

    }

    public MainOrderData(IData data)
    {
        this.setAcceptDate(data.getString("ACCEPT_DATE"));
        this.setActorName(data.getString("ACTOR_NAME"));
        this.setActorPhone(data.getString("ACTOR_PHONE"));
        this.setActorPsptId(data.getString("ACTOR_PSPT_ID"));
        this.setActorPsptTypeCode(data.getString("ACTOR_PSPT_TYPE_CODE"));
        this.setAdvancePay(data.getString("ADVANCE_PAY"));
        this.setAppType(data.getString("APP_TYPE"));
        this.setAuthCode(data.getString("AUTH_CODE"));
        this.setBatchCount(data.getString("BATCH_COUNT"));
        this.setBatchId(data.getString("BATCH_ID"));
        this.setCancelCityCode(data.getString("CANCEL_CITY_CODE"));
        this.setCancelDate(data.getString("CANCEL_DATE"));
        this.setCancelDepartId(data.getString("CANCEL_DEPART_ID"));
        this.setCancelEparchyCode(data.getString("CANCEL_EPARCHY_CODE"));
        this.setCancelStaffId(data.getString("CANCEL_STAFF_ID"));
        this.setCancelTag(data.getString("CANCEL_TAG"));
        this.setCityCode(data.getString("CITY_CODE"));
        this.setContractId(data.getString("CONTRACT_ID"));
        this.setCustContactId(data.getString("CUST_CONTACT_ID"));
        this.setCustId(data.getString("CUST_ID"));
        this.setCustIdea(data.getString("CUST_IDEA"));
        this.setCustName(data.getString("CUST_NAME"));
        this.setDecomposeRuleId(data.getString("DECOMPOSE_RULE_ID"));
        this.setDispatchRuleId(data.getString("DISPATCH_RULE_ID"));
        this.setEparchyCode(data.getString("EPARCHY_CODE"));
        this.setExecAction(data.getString("EXEC_ACTION"));
        this.setExecDesc(data.getString("EXEC_DESC"));
        this.setExecResult(data.getString("EXEC_RESULT"));
        this.setExecTime(data.getString("EXEC_TIME"));
        this.setFailTotal(data.getString("FAIL_TOTAL"));
        this.setFeeStaffId(data.getString("FEE_STAFF_ID"));
        this.setFeeState(data.getString("FEE_STATE"));
        this.setFeeTime(data.getString("FEE_TIME"));
        this.setFinishDate(data.getString("FINISH_DATE"));
        this.setForegift(data.getString("FOREGIFT"));
        this.setHqTag(data.getString("HQ_TAG"));
        this.setInvoiceNo(data.getString("INVOICE_NO"));
        this.setInModeCode(data.getString("IN_MODE_CODE"));
        this.setIsNeedHumancheck(data.getString("IS_NEED_HUMANCHECK"));
        this.setNextDealTag(data.getString("NEXT_DEAL_TAG"));
        this.setOperFee(data.getString("OPER_FEE"));
        this.setOrderId(data.getString("ORDER_ID"));
        this.setOrderInstanceState(data.getString("ORDER_INSTANCE_STATE"));
        this.setOrderState(data.getString("ORDER_STATE"));
        this.setOrderTypeCode(data.getString("ORDER_TYPE_CODE"));
        this.setPriority(data.getString("PRIORITY"));
        this.setPriorityType(data.getString("PRIORITY_TYPE"));
        this.setProcessTagSet(data.getString("PROCESS_TAG_SET"));
        this.setPsptId(data.getString("PSPT_ID"));
        this.setPsptTypeCode(data.getString("PSPT_TYPE_CODE"));
        this.setRemark(data.getString("REMARK"));
        this.setRsrvStr1(data.getString("RSRV_STR1"));
        this.setRsrvStr10(data.getString("RSRV_STR10"));
        this.setRsrvStr2(data.getString("RSRV_STR2"));
        this.setRsrvStr3(data.getString("RSRV_STR3"));
        this.setRsrvStr4(data.getString("RSRV_STR4"));
        this.setRsrvStr5(data.getString("RSRV_STR5"));
        this.setRsrvStr6(data.getString("RSRV_STR6"));
        this.setRsrvStr7(data.getString("RSRV_STR7"));
        this.setRsrvStr8(data.getString("RSRV_STR8"));
        this.setRsrvStr9(data.getString("RSRV_STR9"));
        this.setServReqId(data.getString("SERV_REQ_ID"));
        this.setSolutionId(data.getString("SOLUTION_ID"));
        this.setSuccTotal(data.getString("SUCC_TOTAL"));
        this.setTermIp(data.getString("TERM_IP"));
        this.setTradeCityCode(data.getString("TRADE_CITY_CODE"));
        this.setTradeDepartId(data.getString("TRADE_DEPART_ID"));
        this.setTradeEparchyCode(data.getString("TRADE_EPARCHY_CODE"));
        this.setTradeStaffId(data.getString("TRADE_STAFF_ID"));
        this.setTradeTypeCode(data.getString("TRADE_TYPE_CODE"));
        this.setOrderKindCode(data.getString("ORDER_KIND_CODE"));
        this.setSubscribeType(data.getString("SUBSCRIBE_TYPE"));
    }

    public String getAcceptDate()
    {
        return acceptDate;
    }

    public String getActorName()
    {
        return actorName;
    }

    public String getActorPhone()
    {
        return actorPhone;
    }

    public String getActorPsptId()
    {
        return actorPsptId;
    }

    public String getActorPsptTypeCode()
    {
        return actorPsptTypeCode;
    }

    public String getAdvancePay()
    {
        return advancePay;
    }

    public String getAppType()
    {
        return appType;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public String getBatchCount()
    {
        return batchCount;
    }

    public String getBatchId()
    {
        return batchId;
    }

    public String getCancelCityCode()
    {
        return cancelCityCode;
    }

    public String getCancelDate()
    {
        return cancelDate;
    }

    public String getCancelDepartId()
    {
        return cancelDepartId;
    }

    public String getCancelEparchyCode()
    {
        return cancelEparchyCode;
    }

    public String getCancelStaffId()
    {
        return cancelStaffId;
    }

    public String getCancelTag()
    {
        return cancelTag;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getContractId()
    {
        return contractId;
    }

    public String getCustContactId()
    {
        return custContactId;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getCustIdea()
    {
        return custIdea;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getDecomposeRuleId()
    {
        return decomposeRuleId;
    }

    public String getDispatchRuleId()
    {
        return dispatchRuleId;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getExecAction()
    {
        return execAction;
    }

    public String getExecDesc()
    {
        return execDesc;
    }

    public String getExecResult()
    {
        return execResult;
    }

    public String getExecTime()
    {
        return execTime;
    }

    public String getFailTotal()
    {
        return failTotal;
    }

    public String getFeeStaffId()
    {
        return feeStaffId;
    }

    public String getFeeState()
    {
        return feeState;
    }

    public String getFeeTime()
    {
        return feeTime;
    }

    public String getFinishDate()
    {
        return finishDate;
    }

    public String getForegift()
    {
        return foregift;
    }

    public String getHqTag()
    {
        return hqTag;
    }

    public String getInModeCode()
    {
        return inModeCode;
    }

    public String getInvoiceNo()
    {
        return invoiceNo;
    }

    public String getIsNeedHumancheck()
    {
        return isNeedHumancheck;
    }

    public String getNextDealTag()
    {
        return nextDealTag;
    }

    public String getOperFee()
    {
        return operFee;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getOrderInstanceState()
    {
        return orderInstanceState;
    }

    public String getOrderKindCode()
    {
        return orderKindCode;
    }

    public String getOrderState()
    {
        return orderState;
    }

    public String getOrderTypeCode()
    {
        return orderTypeCode;
    }

    public String getPriority()
    {
        return priority;
    }

    public String getPriorityType()
    {
        return priorityType;
    }

    public String getProcessTagSet()
    {
        return processTagSet;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getPsptTypeCode()
    {
        return psptTypeCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return rsrvStr9;
    }

    public String getServReqId()
    {
        return servReqId;
    }

    public String getSolutionId()
    {
        return solutionId;
    }

    public String getSubscribeType()
    {
        return subscribeType;
    }

    public String getSuccTotal()
    {
        return succTotal;
    }

    public String getTableName()
    {
        return "TF_B_ORDER";
    }

    public String getTermIp()
    {
        return termIp;
    }

    public String getTradeCityCode()
    {
        return tradeCityCode;
    }

    public String getTradeDepartId()
    {
        return tradeDepartId;
    }

    public String getTradeEparchyCode()
    {
        return tradeEparchyCode;
    }

    public String getTradeStaffId()
    {
        return tradeStaffId;
    }

    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }

    public void setAcceptDate(String acceptDate)
    {
        this.acceptDate = acceptDate;
    }

    public void setActorName(String actorName)
    {
        this.actorName = actorName;
    }

    public void setActorPhone(String actorPhone)
    {
        this.actorPhone = actorPhone;
    }

    public void setActorPsptId(String actorPsptId)
    {
        this.actorPsptId = actorPsptId;
    }

    public void setActorPsptTypeCode(String actorPsptTypeCode)
    {
        this.actorPsptTypeCode = actorPsptTypeCode;
    }

    public void setAdvancePay(String advancePay)
    {
        this.advancePay = advancePay;
    }

    public void setAppType(String appType)
    {
        this.appType = appType;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public void setBatchCount(String batchCount)
    {
        this.batchCount = batchCount;
    }

    public void setBatchId(String batchId)
    {
        this.batchId = batchId;
    }

    public void setCancelCityCode(String cancelCityCode)
    {
        this.cancelCityCode = cancelCityCode;
    }

    public void setCancelDate(String cancelDate)
    {
        this.cancelDate = cancelDate;
    }

    public void setCancelDepartId(String cancelDepartId)
    {
        this.cancelDepartId = cancelDepartId;
    }

    public void setCancelEparchyCode(String cancelEparchyCode)
    {
        this.cancelEparchyCode = cancelEparchyCode;
    }

    public void setCancelStaffId(String cancelStaffId)
    {
        this.cancelStaffId = cancelStaffId;
    }

    public void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public void setCustContactId(String custContactId)
    {
        this.custContactId = custContactId;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setCustIdea(String custIdea)
    {
        this.custIdea = custIdea;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setDecomposeRuleId(String decomposeRuleId)
    {
        this.decomposeRuleId = decomposeRuleId;
    }

    public void setDispatchRuleId(String dispatchRuleId)
    {
        this.dispatchRuleId = dispatchRuleId;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setExecAction(String execAction)
    {
        this.execAction = execAction;
    }

    public void setExecDesc(String execDesc)
    {
        this.execDesc = execDesc;
    }

    public void setExecResult(String execResult)
    {
        this.execResult = execResult;
    }

    public void setExecTime(String execTime)
    {
        this.execTime = execTime;
    }

    public void setFailTotal(String failTotal)
    {
        this.failTotal = failTotal;
    }

    public void setFeeStaffId(String feeStaffId)
    {
        this.feeStaffId = feeStaffId;
    }

    public void setFeeState(String feeState)
    {
        this.feeState = feeState;
    }

    public void setFeeTime(String feeTime)
    {
        this.feeTime = feeTime;
    }

    public void setFinishDate(String finishDate)
    {
        this.finishDate = finishDate;
    }

    public void setForegift(String foregift)
    {
        this.foregift = foregift;
    }

    public void setHqTag(String hqTag)
    {
        this.hqTag = hqTag;
    }

    public void setInModeCode(String inModeCode)
    {
        this.inModeCode = inModeCode;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public void setIsNeedHumancheck(String isNeedHumancheck)
    {
        this.isNeedHumancheck = isNeedHumancheck;
    }

    public void setNextDealTag(String nextDealTag)
    {
        this.nextDealTag = nextDealTag;
    }

    public void setOperFee(String operFee)
    {
        this.operFee = operFee;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setOrderInstanceState(String orderInstanceState)
    {
        this.orderInstanceState = orderInstanceState;
    }

    public void setOrderKindCode(String orderKindCode)
    {
        this.orderKindCode = orderKindCode;
    }

    public void setOrderState(String orderState)
    {
        this.orderState = orderState;
    }

    public void setOrderTypeCode(String orderTypeCode)
    {
        this.orderTypeCode = orderTypeCode;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public void setPriorityType(String priorityType)
    {
        this.priorityType = priorityType;
    }

    public void setProcessTagSet(String processTagSet)
    {
        this.processTagSet = processTagSet;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setPsptTypeCode(String psptTypeCode)
    {
        this.psptTypeCode = psptTypeCode;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
    }

    public void setRsrvStr8(String rsrvStr8)
    {
        this.rsrvStr8 = rsrvStr8;
    }

    public void setRsrvStr9(String rsrvStr9)
    {
        this.rsrvStr9 = rsrvStr9;
    }

    public void setServReqId(String servReqId)
    {
        this.servReqId = servReqId;
    }

    public void setSolutionId(String solutionId)
    {
        this.solutionId = solutionId;
    }

    public void setSubscribeType(String subscribeType)
    {
        this.subscribeType = subscribeType;
    }

    public void setSuccTotal(String succTotal)
    {
        this.succTotal = succTotal;
    }

    public void setTermIp(String termIp)
    {
        this.termIp = termIp;
    }

    public void setTradeCityCode(String tradeCityCode)
    {
        this.tradeCityCode = tradeCityCode;
    }

    public void setTradeDepartId(String tradeDepartId)
    {
        this.tradeDepartId = tradeDepartId;
    }

    public void setTradeEparchyCode(String tradeEparchyCode)
    {
        this.tradeEparchyCode = tradeEparchyCode;
    }

    public void setTradeStaffId(String tradeStaffId)
    {
        this.tradeStaffId = tradeStaffId;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACCEPT_DATE", this.acceptDate);
        data.put("ACTOR_NAME", this.actorName);
        data.put("ACTOR_PHONE", this.actorPhone);
        data.put("ACTOR_PSPT_ID", this.actorPsptId);
        data.put("ACTOR_PSPT_TYPE_CODE", this.actorPsptTypeCode);
        data.put("ADVANCE_PAY", this.advancePay);
        data.put("APP_TYPE", this.appType);
        data.put("AUTH_CODE", this.authCode);
        data.put("BATCH_COUNT", this.batchCount);
        data.put("BATCH_ID", this.batchId);
        data.put("CANCEL_CITY_CODE", this.cancelCityCode);
        data.put("CANCEL_DATE", this.cancelDate);
        data.put("CANCEL_DEPART_ID", this.cancelDepartId);
        data.put("CANCEL_EPARCHY_CODE", this.cancelEparchyCode);
        data.put("CANCEL_STAFF_ID", this.cancelStaffId);
        data.put("CANCEL_TAG", this.cancelTag);
        data.put("CITY_CODE", this.cityCode);
        data.put("CONTRACT_ID", this.contractId);
        data.put("CUST_CONTACT_ID", this.custContactId);
        data.put("CUST_ID", this.custId);
        data.put("CUST_IDEA", this.custIdea);
        data.put("CUST_NAME", this.custName);
        data.put("DECOMPOSE_RULE_ID", this.decomposeRuleId);
        data.put("DISPATCH_RULE_ID", this.dispatchRuleId);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("EXEC_ACTION", this.execAction);
        data.put("EXEC_DESC", this.execDesc);
        data.put("EXEC_RESULT", this.execResult);
        data.put("EXEC_TIME", this.execTime);
        data.put("FAIL_TOTAL", this.failTotal);
        data.put("FEE_STAFF_ID", this.feeStaffId);
        data.put("FEE_STATE", this.feeState);
        data.put("FEE_TIME", this.feeTime);
        data.put("FINISH_DATE", this.finishDate);
        data.put("FOREGIFT", this.foregift);
        data.put("HQ_TAG", this.hqTag);
        data.put("INVOICE_NO", this.invoiceNo);
        data.put("IN_MODE_CODE", this.inModeCode);
        data.put("IS_NEED_HUMANCHECK", this.isNeedHumancheck);
        data.put("NEXT_DEAL_TAG", this.nextDealTag);
        data.put("OPER_FEE", this.operFee);
        data.put("ORDER_ID", this.orderId);
        data.put("ORDER_INSTANCE_STATE", this.orderInstanceState);
        data.put("ORDER_STATE", this.orderState);
        data.put("ORDER_TYPE_CODE", this.orderTypeCode);
        data.put("PRIORITY", this.priority);
        data.put("PRIORITY_TYPE", this.priorityType);
        data.put("PROCESS_TAG_SET", this.processTagSet);
        data.put("PSPT_ID", this.psptId);
        data.put("PSPT_TYPE_CODE", this.psptTypeCode);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("SERV_REQ_ID", this.servReqId);
        data.put("SOLUTION_ID", this.solutionId);
        data.put("SUCC_TOTAL", this.succTotal);
        data.put("TERM_IP", this.termIp);
        data.put("TRADE_CITY_CODE", this.tradeCityCode);
        data.put("TRADE_DEPART_ID", this.tradeDepartId);
        data.put("TRADE_EPARCHY_CODE", this.tradeEparchyCode);
        data.put("TRADE_STAFF_ID", this.tradeStaffId);
        data.put("TRADE_TYPE_CODE", this.tradeTypeCode);
        data.put("SUBSCRIBE_TYPE", this.subscribeType);

        return data;
    }

    @Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
