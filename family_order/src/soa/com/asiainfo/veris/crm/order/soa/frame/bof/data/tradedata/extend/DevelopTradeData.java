
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class DevelopTradeData extends BaseTradeData
{
    private String developCityCode;

    private String developDate;

    private String developDepartId;

    private String developEparchyCode;

    private String developStaffId;

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

    private String rsrvStr10;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String instId;

    public DevelopTradeData()
    {

    }

    public DevelopTradeData(IData data)
    {
        this.developCityCode = data.getString("DEVELOP_CITY_CODE");
        this.developDate = data.getString("DEVELOP_DATE");
        this.developDepartId = data.getString("DEVELOP_DEPART_ID");
        this.developEparchyCode = data.getString("DEVELOP_EPARCHY_CODE");
        this.developStaffId = data.getString("DEVELOP_STAFF_ID");
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
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.instId = data.getString("INST_ID");
    }

    public DevelopTradeData clone()
    {
        DevelopTradeData developTradeData = new DevelopTradeData();
        developTradeData.setDevelopCityCode(this.getDevelopCityCode());
        developTradeData.setDevelopDate(this.getDevelopDate());
        developTradeData.setDevelopDepartId(this.getDevelopDepartId());
        developTradeData.setDevelopEparchyCode(this.getDevelopEparchyCode());
        developTradeData.setDevelopStaffId(this.getDevelopStaffId());
        developTradeData.setModifyTag(this.getModifyTag());
        developTradeData.setRemark(this.getRemark());
        developTradeData.setRsrvDate1(this.getRsrvDate1());
        developTradeData.setRsrvDate2(this.getRsrvDate2());
        developTradeData.setRsrvDate3(this.getRsrvDate3());
        developTradeData.setRsrvNum1(this.getRsrvNum1());
        developTradeData.setRsrvNum2(this.getRsrvNum2());
        developTradeData.setRsrvNum3(this.getRsrvNum3());
        developTradeData.setRsrvNum4(this.getRsrvNum4());
        developTradeData.setRsrvNum5(this.getRsrvNum5());
        developTradeData.setRsrvStr1(this.getRsrvStr1());
        developTradeData.setRsrvStr10(this.getRsrvStr10());
        developTradeData.setRsrvStr2(this.getRsrvStr2());
        developTradeData.setRsrvStr3(this.getRsrvStr3());
        developTradeData.setRsrvStr4(this.getRsrvStr4());
        developTradeData.setRsrvStr5(this.getRsrvStr5());
        developTradeData.setRsrvStr6(this.getRsrvStr6());
        developTradeData.setRsrvStr7(this.getRsrvStr7());
        developTradeData.setRsrvStr8(this.getRsrvStr8());
        developTradeData.setRsrvStr9(this.getRsrvStr9());
        developTradeData.setRsrvTag1(this.getRsrvTag1());
        developTradeData.setRsrvTag2(this.getRsrvTag2());
        developTradeData.setRsrvTag3(this.getRsrvTag3());
        developTradeData.setInstId(this.getInstId());
        return developTradeData;
    }

    public String getDevelopCityCode()
    {
        return developCityCode;
    }

    public String getDevelopDate()
    {
        return developDate;
    }

    public String getDevelopDepartId()
    {
        return developDepartId;
    }

    public String getDevelopEparchyCode()
    {
        return developEparchyCode;
    }

    public String getDevelopStaffId()
    {
        return developStaffId;
    }

    public String getInstId()
    {
        return instId;
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

    public String getTableName()
    {
        return "TF_B_TRADE_DEVELOP";
    }

    public void setDevelopCityCode(String developCityCode)
    {
        this.developCityCode = developCityCode;
    }

    public void setDevelopDate(String developDate)
    {
        this.developDate = developDate;
    }

    public void setDevelopDepartId(String developDepartId)
    {
        this.developDepartId = developDepartId;
    }

    public void setDevelopEparchyCode(String developEparchyCode)
    {
        this.developEparchyCode = developEparchyCode;
    }

    public void setDevelopStaffId(String developStaffId)
    {
        this.developStaffId = developStaffId;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
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

    public IData toData()
    {
        IData data = new DataMap();

        data.put("DEVELOP_CITY_CODE", this.developCityCode);
        data.put("DEVELOP_DATE", this.developDate);
        data.put("DEVELOP_DEPART_ID", this.developDepartId);
        data.put("DEVELOP_EPARCHY_CODE", this.developEparchyCode);
        data.put("DEVELOP_STAFF_ID", this.developStaffId);
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
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("INST_ID", this.instId);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
