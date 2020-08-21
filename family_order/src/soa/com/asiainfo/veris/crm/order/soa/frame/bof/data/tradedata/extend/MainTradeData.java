
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class MainTradeData extends BaseTradeData
{
    private String acctId;

    private String acctIdB;

    private String advancePay = "0";

    private String batchId;

    private String bpmId;

    private String brandCode;

    private String campnId;

    private String cancelCityCode;

    private String cancelDate;

    private String cancelDepartId;

    private String cancelEparchyCode;

    private String cancelStaffId;

    private String cancelTag;

    private String cityCode;

    private String custContactId;

    private String custId;

    private String custIdB;

    private String custName;

    private String eparchyCode;

    private String execAction;

    private String execDesc;

    private String execResult;

    private String execTime;

    private String feeStaffId;

    private String feeState = "0";

    private String feeTime;

    private String finishDate;

    private String foregift = "0";

    private String freeResourceTag;

    private String intfId;

    private String invoiceNo;

    private String inModeCode;

    private String isNeedHumancheck;

    private String netTypeCode;

    private String nextDealTag;

    private String olcomTag;

    private String operFee = "0";

    private String orderId;

    private String pfType;

    private String priority;

    private String processTagSet;

    private String productId;

    private String prodOrderId;

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

    private String serialNumber;

    private String serialNumberB;

    private String servReqId;

    private String subscribeState;

    private String subscribeType;

    private String termIp;

    private String tradeTypeCode;

    private String userId;

    private String userIdB;

    private String pfWait;

    public MainTradeData()
    {

    }

    public MainTradeData(IData data)
    {
        this.acctId = data.getString("ACCT_ID");
        this.acctIdB = data.getString("ACCT_ID_B");
        this.advancePay = data.getString("ADVANCE_PAY");
        this.batchId = data.getString("BATCH_ID");
        this.bpmId = data.getString("BPM_ID");
        this.brandCode = data.getString("BRAND_CODE");
        this.campnId = data.getString("CAMPN_ID");
        this.cancelCityCode = data.getString("CANCEL_CITY_CODE");
        this.cancelDate = data.getString("CANCEL_DATE");
        this.cancelDepartId = data.getString("CANCEL_DEPART_ID");
        this.cancelEparchyCode = data.getString("CANCEL_EPARCHY_CODE");
        this.cancelStaffId = data.getString("CANCEL_STAFF_ID");
        this.cancelTag = data.getString("CANCEL_TAG");
        this.cityCode = data.getString("CITY_CODE");
        this.custContactId = data.getString("CUST_CONTACT_ID");
        this.custId = data.getString("CUST_ID");
        this.custIdB = data.getString("CUST_ID_B");
        this.custName = data.getString("CUST_NAME");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.execAction = data.getString("EXEC_ACTION");
        this.execDesc = data.getString("EXEC_DESC");
        this.execResult = data.getString("EXEC_RESULT");
        this.execTime = data.getString("EXEC_TIME");
        this.feeStaffId = data.getString("FEE_STAFF_ID");
        this.feeState = data.getString("FEE_STATE");
        this.feeTime = data.getString("FEE_TIME");
        this.finishDate = data.getString("FINISH_DATE");
        this.foregift = data.getString("FOREGIFT");
        this.freeResourceTag = data.getString("FREE_RESOURCE_TAG");
        this.intfId = data.getString("INTF_ID");
        this.invoiceNo = data.getString("INVOICE_NO");
        this.inModeCode = data.getString("IN_MODE_CODE");
        this.isNeedHumancheck = data.getString("IS_NEED_HUMANCHECK");
        this.netTypeCode = data.getString("NET_TYPE_CODE");
        this.nextDealTag = data.getString("NEXT_DEAL_TAG");
        this.olcomTag = data.getString("OLCOM_TAG");
        this.operFee = data.getString("OPER_FEE");
        this.orderId = data.getString("ORDER_ID");
        this.pfType = data.getString("PF_TYPE");
        this.priority = data.getString("PRIORITY");
        this.processTagSet = data.getString("PROCESS_TAG_SET");
        this.productId = data.getString("PRODUCT_ID");
        this.prodOrderId = data.getString("PROD_ORDER_ID");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.serialNumberB = data.getString("SERIAL_NUMBER_B");
        this.servReqId = data.getString("SERV_REQ_ID");
        this.subscribeState = data.getString("SUBSCRIBE_STATE");
        this.subscribeType = data.getString("SUBSCRIBE_TYPE");
        this.termIp = data.getString("TERM_IP");
        this.tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        this.userId = data.getString("USER_ID");
        this.userIdB = data.getString("USER_ID_B");
        this.pfWait = data.getString("PF_WAIT");
    }

    public MainTradeData clone()
    {
        MainTradeData MainTradeData = new MainTradeData();
        MainTradeData.setAcctId(this.getAcctId());
        MainTradeData.setAcctIdB(this.getAcctIdB());
        MainTradeData.setAdvancePay(this.getAdvancePay());
        MainTradeData.setBatchId(this.getBatchId());
        MainTradeData.setBpmId(this.getBpmId());
        MainTradeData.setBrandCode(this.getBrandCode());
        MainTradeData.setCampnId(this.getCampnId());
        MainTradeData.setCancelCityCode(this.getCancelCityCode());
        MainTradeData.setCancelDate(this.getCancelDate());
        MainTradeData.setCancelDepartId(this.getCancelDepartId());
        MainTradeData.setCancelEparchyCode(this.getCancelEparchyCode());
        MainTradeData.setCancelStaffId(this.getCancelStaffId());
        MainTradeData.setCancelTag(this.getCancelTag());
        MainTradeData.setCityCode(this.getCityCode());
        MainTradeData.setCustContactId(this.getCustContactId());
        MainTradeData.setCustId(this.getCustId());
        MainTradeData.setCustIdB(this.getCustIdB());
        MainTradeData.setCustName(this.getCustName());
        MainTradeData.setEparchyCode(this.getEparchyCode());
        MainTradeData.setExecAction(this.getExecAction());
        MainTradeData.setExecDesc(this.getExecDesc());
        MainTradeData.setExecResult(this.getExecResult());
        MainTradeData.setExecTime(this.getExecTime());
        MainTradeData.setFeeStaffId(this.getFeeStaffId());
        MainTradeData.setFeeState(this.getFeeState());
        MainTradeData.setFeeTime(this.getFeeTime());
        MainTradeData.setFinishDate(this.getFinishDate());
        MainTradeData.setForegift(this.getForegift());
        MainTradeData.setFreeResourceTag(this.getFreeResourceTag());
        MainTradeData.setIntfId(this.getIntfId());
        MainTradeData.setInvoiceNo(this.getInvoiceNo());
        MainTradeData.setInModeCode(this.getInModeCode());
        MainTradeData.setIsNeedHumancheck(this.getIsNeedHumancheck());
        MainTradeData.setNetTypeCode(this.getNetTypeCode());
        MainTradeData.setNextDealTag(this.getNextDealTag());
        MainTradeData.setOlcomTag(this.getOlcomTag());
        MainTradeData.setOperFee(this.getOperFee());
        MainTradeData.setOrderId(this.getOrderId());
        MainTradeData.setPfType(this.getPfType());
        MainTradeData.setPriority(this.getPriority());
        MainTradeData.setProcessTagSet(this.getProcessTagSet());
        MainTradeData.setProductId(this.getProductId());
        MainTradeData.setProdOrderId(this.getProdOrderId());
        MainTradeData.setRemark(this.getRemark());
        MainTradeData.setRsrvStr1(this.getRsrvStr1());
        MainTradeData.setRsrvStr10(this.getRsrvStr10());
        MainTradeData.setRsrvStr2(this.getRsrvStr2());
        MainTradeData.setRsrvStr3(this.getRsrvStr3());
        MainTradeData.setRsrvStr4(this.getRsrvStr4());
        MainTradeData.setRsrvStr5(this.getRsrvStr5());
        MainTradeData.setRsrvStr6(this.getRsrvStr6());
        MainTradeData.setRsrvStr7(this.getRsrvStr7());
        MainTradeData.setRsrvStr8(this.getRsrvStr8());
        MainTradeData.setRsrvStr9(this.getRsrvStr9());
        MainTradeData.setSerialNumber(this.getSerialNumber());
        MainTradeData.setSerialNumberB(this.getSerialNumberB());
        MainTradeData.setServReqId(this.getServReqId());
        MainTradeData.setSubscribeState(this.getSubscribeState());
        MainTradeData.setSubscribeType(this.getSubscribeType());
        MainTradeData.setTermIp(this.getTermIp());
        MainTradeData.setTradeTypeCode(this.getTradeTypeCode());
        MainTradeData.setUserId(this.getUserId());
        MainTradeData.setUserIdB(this.getUserIdB());
        MainTradeData.setPfWait(this.getPfWait());
        return MainTradeData;
    }

    public String getAcctId()
    {
        return acctId;
    }

    public String getAcctIdB()
    {
        return acctIdB;
    }

    public String getAdvancePay()
    {
        return advancePay;
    }

    public String getBatchId()
    {
        return batchId;
    }

    public String getBpmId()
    {
        return bpmId;
    }

    public String getBrandCode()
    {
        return brandCode;
    }

    public String getCampnId()
    {
        return campnId;
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

    public String getCustContactId()
    {
        return custContactId;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getCustIdB()
    {
        return custIdB;
    }

    public String getCustName()
    {
        return custName;
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

    public String getFreeResourceTag()
    {
        return freeResourceTag;
    }

    public String getInModeCode()
    {
        return inModeCode;
    }

    public String getIntfId()
    {
        return intfId;
    }

    public String getInvoiceNo()
    {
        return invoiceNo;
    }

    public String getIsNeedHumancheck()
    {
        return isNeedHumancheck;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public String getNextDealTag()
    {
        return nextDealTag;
    }

    public String getOlcomTag()
    {
        return olcomTag;
    }

    public String getOperFee()
    {
        return operFee;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getPfType()
    {
        return pfType;
    }

    /***
     * * @return Returns the pfWait.
     */
    public String getPfWait()
    {
        return pfWait;
    }

    public String getPriority()
    {
        return priority;
    }

    public String getProcessTagSet()
    {
        return processTagSet;
    }

    public String getProdOrderId()
    {
        return prodOrderId;
    }

    public String getProductId()
    {
        return productId;
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

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSerialNumberB()
    {
        return serialNumberB;
    }

    public String getServReqId()
    {
        return servReqId;
    }

    public String getSubscribeState()
    {
        return subscribeState;
    }

    public String getSubscribeType()
    {
        return subscribeType;
    }

    public String getTableName()
    {
        return "TF_B_TRADE";
    }

    public String getTermIp()
    {
        return termIp;
    }

    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserIdB()
    {
        return userIdB;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAcctIdB(String acctIdB)
    {
        this.acctIdB = acctIdB;
    }

    public void setAdvancePay(String advancePay)
    {
        this.advancePay = advancePay;
    }

    public void setBatchId(String batchId)
    {
        this.batchId = batchId;
    }

    public void setBpmId(String bpmId)
    {
        this.bpmId = bpmId;
    }

    public void setBrandCode(String brandCode)
    {
        this.brandCode = brandCode;
    }

    public void setCampnId(String campnId)
    {
        this.campnId = campnId;
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

    public void setCustContactId(String custContactId)
    {
        this.custContactId = custContactId;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setCustIdB(String custIdB)
    {
        this.custIdB = custIdB;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
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

    public void setFreeResourceTag(String freeResourceTag)
    {
        this.freeResourceTag = freeResourceTag;
    }

    public void setInModeCode(String inModeCode)
    {
        this.inModeCode = inModeCode;
    }

    public void setIntfId(String intfId)
    {
        this.intfId = intfId;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public void setIsNeedHumancheck(String isNeedHumancheck)
    {
        this.isNeedHumancheck = isNeedHumancheck;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setNextDealTag(String nextDealTag)
    {
        this.nextDealTag = nextDealTag;
    }

    public void setOlcomTag(String olcomTag)
    {
        this.olcomTag = olcomTag;
    }

    public void setOperFee(String operFee)
    {
        this.operFee = operFee;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setPfType(String pfType)
    {
        this.pfType = pfType;
    }

    /***
     * @param pfWait
     *            The pfWait to set.
     */
    public void setPfWait(String pfWait)
    {
        this.pfWait = pfWait;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public void setProcessTagSet(String processTagSet)
    {
        this.processTagSet = processTagSet;
    }

    public void setProdOrderId(String prodOrderId)
    {
        this.prodOrderId = prodOrderId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
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

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSerialNumberB(String serialNumberB)
    {
        this.serialNumberB = serialNumberB;
    }

    public void setServReqId(String servReqId)
    {
        this.servReqId = servReqId;
    }

    public void setSubscribeState(String subscribeState)
    {
        this.subscribeState = subscribeState;
    }

    public void setSubscribeType(String subscribeType)
    {
        this.subscribeType = subscribeType;
    }

    public void setTermIp(String termIp)
    {
        this.termIp = termIp;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserIdB(String userIdB)
    {
        this.userIdB = userIdB;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACCT_ID", this.acctId);
        data.put("ACCT_ID_B", this.acctIdB);
        data.put("ADVANCE_PAY", this.advancePay);
        data.put("BATCH_ID", this.batchId);
        data.put("BPM_ID", this.bpmId);
        data.put("BRAND_CODE", this.brandCode);
        data.put("CAMPN_ID", this.campnId);
        data.put("CANCEL_CITY_CODE", this.cancelCityCode);
        data.put("CANCEL_DATE", this.cancelDate);
        data.put("CANCEL_DEPART_ID", this.cancelDepartId);
        data.put("CANCEL_EPARCHY_CODE", this.cancelEparchyCode);
        data.put("CANCEL_STAFF_ID", this.cancelStaffId);
        data.put("CANCEL_TAG", this.cancelTag);
        data.put("CITY_CODE", this.cityCode);
        data.put("CUST_CONTACT_ID", this.custContactId);
        data.put("CUST_ID", this.custId);
        data.put("CUST_ID_B", this.custIdB);
        data.put("CUST_NAME", this.custName);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("EXEC_ACTION", this.execAction);
        data.put("EXEC_DESC", this.execDesc);
        data.put("EXEC_RESULT", this.execResult);
        data.put("EXEC_TIME", this.execTime);
        data.put("FEE_STAFF_ID", this.feeStaffId);
        data.put("FEE_STATE", this.feeState);
        data.put("FEE_TIME", this.feeTime);
        data.put("FINISH_DATE", this.finishDate);
        data.put("FOREGIFT", this.foregift);
        data.put("FREE_RESOURCE_TAG", this.freeResourceTag);
        data.put("INTF_ID", this.intfId);
        data.put("INVOICE_NO", this.invoiceNo);
        data.put("IN_MODE_CODE", this.inModeCode);
        data.put("IS_NEED_HUMANCHECK", this.isNeedHumancheck);
        data.put("NET_TYPE_CODE", this.netTypeCode);
        data.put("NEXT_DEAL_TAG", this.nextDealTag);
        data.put("OLCOM_TAG", this.olcomTag);
        data.put("OPER_FEE", this.operFee);
        data.put("ORDER_ID", this.orderId);
        data.put("PF_TYPE", this.pfType);
        data.put("PRIORITY", this.priority);
        data.put("PROCESS_TAG_SET", this.processTagSet);
        data.put("PRODUCT_ID", this.productId);
        data.put("PROD_ORDER_ID", this.prodOrderId);
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
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("SERIAL_NUMBER_B", this.serialNumberB);
        data.put("SERV_REQ_ID", this.servReqId);
        data.put("SUBSCRIBE_STATE", this.subscribeState);
        data.put("SUBSCRIBE_TYPE", this.subscribeType);
        data.put("TERM_IP", this.termIp);
        data.put("TRADE_TYPE_CODE", this.tradeTypeCode);
        data.put("USER_ID", this.userId);
        data.put("USER_ID_B", this.userIdB);
        data.put("PF_WAIT", this.pfWait);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
