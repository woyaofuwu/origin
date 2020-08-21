
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class OtherFeeTradeData extends BaseTradeData
{
    private String acctId;

    private String acctId2;

    private String actionCode;

    private String endDate;

    private String inDepositCode;

    private String isAcctday;

    private String operFee;

    private String operType;

    private String outDepositCode;

    private String paymentId;

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

    private String startDate;

    private String userId;

    private String userId2;

    public OtherFeeTradeData()
    {

    }

    public OtherFeeTradeData(IData data)
    {
        this.acctId = data.getString("ACCT_ID");
        this.acctId2 = data.getString("ACCT_ID2");
        this.actionCode = data.getString("ACTION_CODE");
        this.endDate = data.getString("END_DATE");
        this.inDepositCode = data.getString("IN_DEPOSIT_CODE");
        this.isAcctday = data.getString("IS_ACCTDAY");
        this.operFee = data.getString("OPER_FEE");
        this.operType = data.getString("OPER_TYPE");
        this.outDepositCode = data.getString("OUT_DEPOSIT_CODE");
        this.paymentId = data.getString("PAYMENT_ID");
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
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.userId2 = data.getString("USER_ID2");
    }

    public String getAcctId()
    {
        return acctId;
    }

    public String getAcctId2()
    {
        return acctId2;
    }

    public String getActionCode()
    {
        return actionCode;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getInDepositCode()
    {
        return inDepositCode;
    }

    public String getIsAcctday()
    {
        return isAcctday;
    }

    public String getOperFee()
    {
        return operFee;
    }

    public String getOperType()
    {
        return operType;
    }

    public String getOutDepositCode()
    {
        return outDepositCode;
    }

    public String getPaymentId()
    {
        return paymentId;
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

    public String getStartDate()
    {
        return startDate;
    }

    @Override
    public String getTableName()
    {
        // TODO Auto-generated method stub
        return "TF_B_TRADEFEE_OTHERFEE";
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserId2()
    {
        return userId2;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAcctId2(String acctId2)
    {
        this.acctId2 = acctId2;
    }

    public void setActionCode(String actionCode)
    {
        this.actionCode = actionCode;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInDepositCode(String inDepositCode)
    {
        this.inDepositCode = inDepositCode;
    }

    public void setIsAcctday(String isAcctday)
    {
        this.isAcctday = isAcctday;
    }

    public void setOperFee(String operFee)
    {
        this.operFee = operFee;
    }

    public void setOperType(String operType)
    {
        this.operType = operType;
    }

    public void setOutDepositCode(String outDepositCode)
    {
        this.outDepositCode = outDepositCode;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
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

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserId2(String userId2)
    {
        this.userId2 = userId2;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACCT_ID", this.acctId);
        data.put("ACCT_ID2", this.acctId2);
        data.put("ACTION_CODE", this.actionCode);
        data.put("END_DATE", this.endDate);
        data.put("IN_DEPOSIT_CODE", this.inDepositCode);
        data.put("IS_ACCTDAY", this.isAcctday);
        data.put("OPER_FEE", this.operFee);
        data.put("OPER_TYPE", this.operType);
        data.put("OUT_DEPOSIT_CODE", this.outDepositCode);
        data.put("PAYMENT_ID", this.paymentId);
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
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("USER_ID2", this.userId2);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
