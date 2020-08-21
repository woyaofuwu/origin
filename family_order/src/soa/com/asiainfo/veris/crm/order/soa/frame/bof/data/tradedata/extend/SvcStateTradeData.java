
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class SvcStateTradeData extends BaseTradeData
{
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

    private String serviceId;

    private String startDate;

    private String stateCode;

    private String userId;

    private String instId;
    
    private String isNeedPf;

    public SvcStateTradeData()
    {

    }

    public SvcStateTradeData(IData data)
    {
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
        this.serviceId = data.getString("SERVICE_ID");
        this.startDate = data.getString("START_DATE");
        this.stateCode = data.getString("STATE_CODE");
        this.userId = data.getString("USER_ID");
        this.instId = data.getString("INST_ID");
        this.isNeedPf = data.getString("IS_NEED_PF", "");
    }

    public SvcStateTradeData clone()
    {
        SvcStateTradeData svcStateTradeData = new SvcStateTradeData();
        svcStateTradeData.setEndDate(this.getEndDate());
        svcStateTradeData.setMainTag(this.getMainTag());
        svcStateTradeData.setModifyTag(this.getModifyTag());
        svcStateTradeData.setRemark(this.getRemark());
        svcStateTradeData.setRsrvDate1(this.getRsrvDate1());
        svcStateTradeData.setRsrvDate2(this.getRsrvDate2());
        svcStateTradeData.setRsrvDate3(this.getRsrvDate3());
        svcStateTradeData.setRsrvNum1(this.getRsrvNum1());
        svcStateTradeData.setRsrvNum2(this.getRsrvNum2());
        svcStateTradeData.setRsrvNum3(this.getRsrvNum3());
        svcStateTradeData.setRsrvNum4(this.getRsrvNum4());
        svcStateTradeData.setRsrvNum5(this.getRsrvNum5());
        svcStateTradeData.setRsrvStr1(this.getRsrvStr1());
        svcStateTradeData.setRsrvStr2(this.getRsrvStr2());
        svcStateTradeData.setRsrvStr3(this.getRsrvStr3());
        svcStateTradeData.setRsrvStr4(this.getRsrvStr4());
        svcStateTradeData.setRsrvStr5(this.getRsrvStr5());
        svcStateTradeData.setRsrvTag1(this.getRsrvTag1());
        svcStateTradeData.setRsrvTag2(this.getRsrvTag2());
        svcStateTradeData.setRsrvTag3(this.getRsrvTag3());
        svcStateTradeData.setServiceId(this.getServiceId());
        svcStateTradeData.setStartDate(this.getStartDate());
        svcStateTradeData.setStateCode(this.getStateCode());
        svcStateTradeData.setUserId(this.getUserId());
        svcStateTradeData.setInstId(this.getInstId());
        svcStateTradeData.setIsNeedPf(this.isNeedPf);
        return svcStateTradeData;
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

    public String getServiceId()
    {
        return serviceId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getStateCode()
    {
        return stateCode;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_SVCSTATE";
    }

    public String getUserId()
    {
        return userId;
    }
    /***
     * * @return Returns the isNeedPf.
     */
    public String getIsNeedPf()
    {
        return isNeedPf;
    }
    
    /***
     * @param isNeedPf
     *            The isNeedPf to set.
     */
    public void setIsNeedPf(String isNeedPf)
    {
        this.isNeedPf = isNeedPf;
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

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setStateCode(String stateCode)
    {
        this.stateCode = stateCode;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

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
        data.put("SERVICE_ID", this.serviceId);
        data.put("START_DATE", this.startDate);
        data.put("STATE_CODE", this.stateCode);
        data.put("USER_ID", this.userId);
        data.put("INST_ID", this.instId);
        data.put("IS_NEED_PF", this.isNeedPf);
        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
