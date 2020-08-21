
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class RelationBBTradeData extends BaseTradeData
{
    private String endDate;

    private String instId;

    private String modifyTag;

    private String orderno;

    private String relationTypeCode;

    private String remark;

    private String roleCodeA;

    private String roleCodeB;

    private String roleTypeCode;

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

    private String serialNumberA;

    private String serialNumberB;

    private String shortCode;

    private String startDate;

    private String userIdA;

    private String userIdB;

    public RelationBBTradeData()
    {

    }

    public RelationBBTradeData(IData data)
    {
        this.endDate = data.getString("END_DATE");
        this.instId = data.getString("INST_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.orderno = data.getString("ORDERNO");
        this.relationTypeCode = data.getString("RELATION_TYPE_CODE");
        this.remark = data.getString("REMARK");
        this.roleCodeA = data.getString("ROLE_CODE_A");
        this.roleCodeB = data.getString("ROLE_CODE_B");
        this.roleTypeCode = data.getString("ROLE_TYPE_CODE");
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
        this.serialNumberA = data.getString("SERIAL_NUMBER_A");
        this.serialNumberB = data.getString("SERIAL_NUMBER_B");
        this.shortCode = data.getString("SHORT_CODE");
        this.startDate = data.getString("START_DATE");
        this.userIdA = data.getString("USER_ID_A");
        this.userIdB = data.getString("USER_ID_B");
    }

    public RelationBBTradeData clone()
    {
        RelationBBTradeData relationTradeData = new RelationBBTradeData();
        relationTradeData.setEndDate(this.getEndDate());
        relationTradeData.setInstId(this.getInstId());
        relationTradeData.setModifyTag(this.getModifyTag());
        relationTradeData.setOrderno(this.getOrderno());
        relationTradeData.setRelationTypeCode(this.getRelationTypeCode());
        relationTradeData.setRemark(this.getRemark());
        relationTradeData.setRoleCodeA(this.getRoleCodeA());
        relationTradeData.setRoleCodeB(this.getRoleCodeB());
        relationTradeData.setRoleTypeCode(this.getRoleTypeCode());
        relationTradeData.setRsrvDate1(this.getRsrvDate1());
        relationTradeData.setRsrvDate2(this.getRsrvDate2());
        relationTradeData.setRsrvDate3(this.getRsrvDate3());
        relationTradeData.setRsrvNum1(this.getRsrvNum1());
        relationTradeData.setRsrvNum2(this.getRsrvNum2());
        relationTradeData.setRsrvNum3(this.getRsrvNum3());
        relationTradeData.setRsrvNum4(this.getRsrvNum4());
        relationTradeData.setRsrvNum5(this.getRsrvNum5());
        relationTradeData.setRsrvStr1(this.getRsrvStr1());
        relationTradeData.setRsrvStr2(this.getRsrvStr2());
        relationTradeData.setRsrvStr3(this.getRsrvStr3());
        relationTradeData.setRsrvStr4(this.getRsrvStr4());
        relationTradeData.setRsrvStr5(this.getRsrvStr5());
        relationTradeData.setRsrvTag1(this.getRsrvTag1());
        relationTradeData.setRsrvTag2(this.getRsrvTag2());
        relationTradeData.setRsrvTag3(this.getRsrvTag3());
        relationTradeData.setSerialNumberA(this.getSerialNumberA());
        relationTradeData.setSerialNumberB(this.getSerialNumberB());
        relationTradeData.setShortCode(this.getShortCode());
        relationTradeData.setStartDate(this.getStartDate());
        relationTradeData.setUserIdA(this.getUserIdA());
        relationTradeData.setUserIdB(this.getUserIdB());
        return relationTradeData;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getOrderno()
    {
        return orderno;
    }

    public String getRelationTypeCode()
    {
        return relationTypeCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRoleCodeA()
    {
        return roleCodeA;
    }

    public String getRoleCodeB()
    {
        return roleCodeB;
    }

    public String getRoleTypeCode()
    {
        return roleTypeCode;
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

    public String getSerialNumberA()
    {
        return serialNumberA;
    }

    public String getSerialNumberB()
    {
        return serialNumberB;
    }

    public String getShortCode()
    {
        return shortCode;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_RELATION_BB";
    }

    public String getUserIdA()
    {
        return userIdA;
    }

    public String getUserIdB()
    {
        return userIdB;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setOrderno(String orderno)
    {
        this.orderno = orderno;
    }

    public void setRelationTypeCode(String relationTypeCode)
    {
        this.relationTypeCode = relationTypeCode;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRoleCodeA(String roleCodeA)
    {
        this.roleCodeA = roleCodeA;
    }

    public void setRoleCodeB(String roleCodeB)
    {
        this.roleCodeB = roleCodeB;
    }

    public void setRoleTypeCode(String roleTypeCode)
    {
        this.roleTypeCode = roleTypeCode;
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

    public void setSerialNumberA(String serialNumberA)
    {
        this.serialNumberA = serialNumberA;
    }

    public void setSerialNumberB(String serialNumberB)
    {
        this.serialNumberB = serialNumberB;
    }

    public void setShortCode(String shortCode)
    {
        this.shortCode = shortCode;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

    public void setUserIdB(String userIdB)
    {
        this.userIdB = userIdB;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("END_DATE", this.endDate);
        data.put("INST_ID", this.instId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("ORDERNO", this.orderno);
        data.put("RELATION_TYPE_CODE", this.relationTypeCode);
        data.put("REMARK", this.remark);
        data.put("ROLE_CODE_A", this.roleCodeA);
        data.put("ROLE_CODE_B", this.roleCodeB);
        data.put("ROLE_TYPE_CODE", this.roleTypeCode);
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
        data.put("SERIAL_NUMBER_A", this.serialNumberA);
        data.put("SERIAL_NUMBER_B", this.serialNumberB);
        data.put("SHORT_CODE", this.shortCode);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID_A", this.userIdA);
        data.put("USER_ID_B", this.userIdB);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
