
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class WideNetActTradeData extends BaseTradeData
{
    private String acctId;

    private String acctPasswd;

    private String endDate;

    private String mainTag;

    private String modifyTag;

    private String remark;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String startDate;

    private String userId;

    private String instId;

    public WideNetActTradeData()
    {

    }

    public WideNetActTradeData(IData data)
    {
        this.acctId = data.getString("ACCT_ID");
        this.acctPasswd = data.getString("ACCT_PASSWD");
        this.endDate = data.getString("END_DATE");
        this.mainTag = data.getString("MAIN_TAG");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.remark = data.getString("REMARK");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvNum4 = data.getString("RSRV_NUM4");
        this.rsrvNum5 = data.getString("RSRV_NUM5");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.instId = data.getString("INST_ID");
    }

    public WideNetActTradeData clone()
    {
        WideNetActTradeData wideNetActTradeData = new WideNetActTradeData();
        wideNetActTradeData.setAcctId(this.getAcctId());
        wideNetActTradeData.setAcctPasswd(this.getAcctPasswd());
        wideNetActTradeData.setEndDate(this.getEndDate());
        wideNetActTradeData.setMainTag(this.getMainTag());
        wideNetActTradeData.setModifyTag(this.getModifyTag());
        wideNetActTradeData.setRemark(this.getRemark());
        wideNetActTradeData.setRsrvDate1(this.getRsrvDate1());
        wideNetActTradeData.setRsrvDate2(this.getRsrvDate2());
        wideNetActTradeData.setRsrvDate3(this.getRsrvDate3());
        wideNetActTradeData.setRsrvNum1(this.getRsrvNum1());
        wideNetActTradeData.setRsrvNum2(this.getRsrvNum2());
        wideNetActTradeData.setRsrvNum3(this.getRsrvNum3());
        wideNetActTradeData.setRsrvNum4(this.getRsrvNum4());
        wideNetActTradeData.setRsrvNum5(this.getRsrvNum5());
        wideNetActTradeData.setRsrvStr1(this.getRsrvStr1());
        wideNetActTradeData.setRsrvStr2(this.getRsrvStr2());
        wideNetActTradeData.setRsrvStr3(this.getRsrvStr3());
        wideNetActTradeData.setRsrvStr4(this.getRsrvStr4());
        wideNetActTradeData.setRsrvStr5(this.getRsrvStr5());
        wideNetActTradeData.setRsrvTag1(this.getRsrvTag1());
        wideNetActTradeData.setRsrvTag2(this.getRsrvTag2());
        wideNetActTradeData.setRsrvTag3(this.getRsrvTag3());
        wideNetActTradeData.setStartDate(this.getStartDate());
        wideNetActTradeData.setUserId(this.getUserId());
        wideNetActTradeData.setInstId(this.getInstId());
        return wideNetActTradeData;
    }

    public String getAcctId()
    {
        return acctId;
    }

    public String getAcctPasswd()
    {
        return acctPasswd;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getMainTag()
    {
        return mainTag;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return rsrvDate3;
    }

    public String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return rsrvNum3;
    }

    public String getRsrvNum4()
    {
        return rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return rsrvNum5;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
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

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_WIDENET_ACT";
    }

    public String getUserId()
    {
        return userId;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAcctPasswd(String acctPasswd)
    {
        this.acctPasswd = acctPasswd;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setMainTag(String mainTag)
    {
        this.mainTag = mainTag;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
    }

    public void setRsrvNum1(String rsrvNum1)
    {
        this.rsrvNum1 = rsrvNum1;
    }

    public void setRsrvNum2(String rsrvNum2)
    {
        this.rsrvNum2 = rsrvNum2;
    }

    public void setRsrvNum3(String rsrvNum3)
    {
        this.rsrvNum3 = rsrvNum3;
    }

    public void setRsrvNum4(String rsrvNum4)
    {
        this.rsrvNum4 = rsrvNum4;
    }

    public void setRsrvNum5(String rsrvNum5)
    {
        this.rsrvNum5 = rsrvNum5;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
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

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACCT_ID", this.acctId);
        data.put("ACCT_PASSWD", this.acctPasswd);
        data.put("END_DATE", this.endDate);
        data.put("MAIN_TAG", this.mainTag);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("REMARK", this.remark);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("RSRV_NUM5", this.rsrvNum5);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("INST_ID", this.instId);
        return data;
    }

}
