
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class ResTradeData extends BaseTradeData
{
    private String campnId;

    private String endDate;

    private String imsi;

    private String instId;

    private String ki;

    private String modifyTag;

    private String remark;

    private String resCode;

    private String resTypeCode;

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

    private String userIdA;

    public ResTradeData()
    {

    }

    public ResTradeData(IData data)
    {
        this.campnId = data.getString("CAMPN_ID");
        this.endDate = data.getString("END_DATE");
        this.imsi = data.getString("IMSI");
        this.instId = data.getString("INST_ID");
        this.ki = data.getString("KI");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.remark = data.getString("REMARK");
        this.resCode = data.getString("RES_CODE");
        this.resTypeCode = data.getString("RES_TYPE_CODE");
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
        this.userIdA = data.getString("USER_ID_A");
    }

    public ResTradeData clone()
    {
        ResTradeData resTradeData = new ResTradeData();
        resTradeData.setCampnId(this.getCampnId());
        resTradeData.setEndDate(this.getEndDate());
        resTradeData.setImsi(this.getImsi());
        resTradeData.setInstId(this.getInstId());
        resTradeData.setKi(this.getKi());
        resTradeData.setModifyTag(this.getModifyTag());
        resTradeData.setRemark(this.getRemark());
        resTradeData.setResCode(this.getResCode());
        resTradeData.setResTypeCode(this.getResTypeCode());
        resTradeData.setRsrvDate1(this.getRsrvDate1());
        resTradeData.setRsrvDate2(this.getRsrvDate2());
        resTradeData.setRsrvDate3(this.getRsrvDate3());
        resTradeData.setRsrvNum1(this.getRsrvNum1());
        resTradeData.setRsrvNum2(this.getRsrvNum2());
        resTradeData.setRsrvNum3(this.getRsrvNum3());
        resTradeData.setRsrvNum4(this.getRsrvNum4());
        resTradeData.setRsrvNum5(this.getRsrvNum5());
        resTradeData.setRsrvStr1(this.getRsrvStr1());
        resTradeData.setRsrvStr2(this.getRsrvStr2());
        resTradeData.setRsrvStr3(this.getRsrvStr3());
        resTradeData.setRsrvStr4(this.getRsrvStr4());
        resTradeData.setRsrvStr5(this.getRsrvStr5());
        resTradeData.setRsrvTag1(this.getRsrvTag1());
        resTradeData.setRsrvTag2(this.getRsrvTag2());
        resTradeData.setRsrvTag3(this.getRsrvTag3());
        resTradeData.setStartDate(this.getStartDate());
        resTradeData.setUserId(this.getUserId());
        resTradeData.setUserIdA(this.getUserIdA());
        return resTradeData;
    }

    public String getCampnId()
    {
        return campnId;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getImsi()
    {
        return imsi;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getKi()
    {
        return ki;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getResCode()
    {
        return resCode;
    }

    public String getResTypeCode()
    {
        return resTypeCode;
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
        return "TF_B_TRADE_RES";
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserIdA()
    {
        return userIdA;
    }

    public void setCampnId(String campnId)
    {
        this.campnId = campnId;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setImsi(String imsi)
    {
        this.imsi = imsi;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setKi(String ki)
    {
        this.ki = ki;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setResCode(String resCode)
    {
        this.resCode = resCode;
    }

    public void setResTypeCode(String resTypeCode)
    {
        this.resTypeCode = resTypeCode;
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

    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("CAMPN_ID", this.campnId);
        data.put("END_DATE", this.endDate);
        data.put("IMSI", this.imsi);
        data.put("INST_ID", this.instId);
        data.put("KI", this.ki);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("REMARK", this.remark);
        data.put("RES_CODE", this.resCode);
        data.put("RES_TYPE_CODE", this.resTypeCode);
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
        data.put("USER_ID_A", this.userIdA);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
