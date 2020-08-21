
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.IAcctDayChangeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;

/**
 * @author Administrator
 */
public class DiscntTradeData extends ProductModuleTradeData implements IAcctDayChangeData
{

    private String relationTypeCode;

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

    private String specTag = "0";

    private String userIdA = "-1";

    private String operCode;

    private String isNeedPf;

    public DiscntTradeData()
    {
        setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
    }

    public DiscntTradeData(IData data)
    {
        this.campnId = data.getString("CAMPN_ID");
        this.elementType = BofConst.ELEMENT_TYPE_CODE_DISCNT;
        if (StringUtils.isNotBlank(data.getString("DISCNT_CODE")))
        {
            this.elementId = data.getString("DISCNT_CODE");
        }
        else
        {
            this.elementId = data.getString("ELEMENT_ID");
        }
        this.endDate = data.getString("END_DATE");
        this.instId = data.getString("INST_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.packageId = data.getString("PACKAGE_ID");
        this.productId = data.getString("PRODUCT_ID");
        this.relationTypeCode = data.getString("RELATION_TYPE_CODE");
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
        this.specTag = data.getString("SPEC_TAG", "0");
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.userIdA = data.getString("USER_ID_A", "-1");
        this.operCode = data.getString("OPER_CODE", "");
        this.isNeedPf = data.getString("IS_NEED_PF", "");
    }

    @Override
    public DiscntTradeData clone()
    {
        DiscntTradeData discntTradeData = new DiscntTradeData();
        discntTradeData.setCampnId(this.getCampnId());
        discntTradeData.setElementId(this.getElementId());
        discntTradeData.setEndDate(this.getEndDate());
        discntTradeData.setInstId(this.getInstId());
        discntTradeData.setModifyTag(this.getModifyTag());
        discntTradeData.setPackageId(this.getPackageId());
        discntTradeData.setProductId(this.getProductId());
        discntTradeData.setRelationTypeCode(this.getRelationTypeCode());
        discntTradeData.setRemark(this.getRemark());
        discntTradeData.setRsrvDate1(this.getRsrvDate1());
        discntTradeData.setRsrvDate2(this.getRsrvDate2());
        discntTradeData.setRsrvDate3(this.getRsrvDate3());
        discntTradeData.setRsrvNum1(this.getRsrvNum1());
        discntTradeData.setRsrvNum2(this.getRsrvNum2());
        discntTradeData.setRsrvNum3(this.getRsrvNum3());
        discntTradeData.setRsrvNum4(this.getRsrvNum4());
        discntTradeData.setRsrvNum5(this.getRsrvNum5());
        discntTradeData.setRsrvStr1(this.getRsrvStr1());
        discntTradeData.setRsrvStr2(this.getRsrvStr2());
        discntTradeData.setRsrvStr3(this.getRsrvStr3());
        discntTradeData.setRsrvStr4(this.getRsrvStr4());
        discntTradeData.setRsrvStr5(this.getRsrvStr5());
        discntTradeData.setRsrvTag1(this.getRsrvTag1());
        discntTradeData.setRsrvTag2(this.getRsrvTag2());
        discntTradeData.setRsrvTag3(this.getRsrvTag3());
        discntTradeData.setSpecTag(this.getSpecTag());
        discntTradeData.setStartDate(this.getStartDate());
        discntTradeData.setUserId(this.getUserId());
        discntTradeData.setUserIdA(this.getUserIdA());
        discntTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
        discntTradeData.setOperCode(this.getOperCode());
        discntTradeData.setIsNeedPf(this.getIsNeedPf());
        return discntTradeData;
    }

    public String getDiscntCode()
    {
        return elementId;
    }

    public String getIsNeedPf()
    {
        return isNeedPf;
    }

    public String getOperCode()
    {
        return operCode;
    }

    public String getRelationTypeCode()
    {
        return relationTypeCode;
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

    public String getSpecTag()
    {
        return specTag;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_DISCNT";
    }

    public String getUserIdA()
    {
        return userIdA;
    }

    public void setIsNeedPf(String isNeedPf)
    {
        this.isNeedPf = isNeedPf;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public void setRelationTypeCode(String relationTypeCode)
    {
        this.relationTypeCode = relationTypeCode;
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

    public void setSpecTag(String specTag)
    {
        this.specTag = specTag;
    }

    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();

        data.put("CAMPN_ID", this.campnId);
        data.put("DISCNT_CODE", this.elementId);
        data.put("END_DATE", this.endDate);
        data.put("INST_ID", this.instId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("PACKAGE_ID", this.packageId);
        data.put("PRODUCT_ID", this.productId);
        data.put("RELATION_TYPE_CODE", this.relationTypeCode);
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
        data.put("SPEC_TAG", this.specTag);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("USER_ID_A", this.userIdA);
        data.put("OPER_CODE", this.operCode);
        data.put("IS_NEED_PF", this.isNeedPf);

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
