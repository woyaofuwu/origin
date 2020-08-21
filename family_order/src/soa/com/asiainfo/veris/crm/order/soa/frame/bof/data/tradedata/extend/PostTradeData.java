
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class PostTradeData extends BaseTradeData
{
    private String custType;

    private String email;

    private String endDate;

    private String faxNbr;

    private String id;

    private String instId;

    private String idType;

    private String modifyTag;

    private String postAddress;

    private String postCode;

    private String postContent;

    private String postCyc;

    private String postName;

    private String postTag;

    private String postTypeset;

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

    public PostTradeData()
    {

    }

    public PostTradeData(IData data)
    {
        this.setCustType(data.getString("CUST_TYPE"));
        this.setEmail(data.getString("EMAIL"));
        this.setEndDate(data.getString("END_DATE"));
        this.setFaxNbr(data.getString("FAX_NBR"));
        this.setId(data.getString("ID"));
        this.setIdType(data.getString("ID_TYPE"));
        this.setInstId(data.getString("INST_ID"));
        this.setModifyTag(data.getString("MODIFY_TAG"));
        this.setPostAddress(data.getString("POST_ADDRESS"));
        this.setPostCode(data.getString("POST_CODE"));
        this.setPostContent(data.getString("POST_CONTENT"));
        this.setPostCyc(data.getString("POST_CYC"));
        this.setPostName(data.getString("POST_NAME"));
        this.setPostTag(data.getString("POST_TAG"));
        this.setPostTypeset(data.getString("POST_TYPESET"));
        this.setRemark(data.getString("REMARK"));
        this.setRsrvDate1(data.getString("RSRV_DATE1"));
        this.setRsrvDate2(data.getString("RSRV_DATE2"));
        this.setRsrvDate3(data.getString("RSRV_DATE3"));
        this.setRsrvNum1(data.getString("RSRV_NUM1"));
        this.setRsrvNum2(data.getString("RSRV_NUM2"));
        this.setRsrvNum3(data.getString("RSRV_NUM3"));
        this.setRsrvNum4(data.getString("RSRV_NUM4"));
        this.setRsrvNum5(data.getString("RSRV_NUM5"));
        this.setRsrvStr1(data.getString("RSRV_STR1"));
        this.setRsrvStr2(data.getString("RSRV_STR2"));
        this.setRsrvStr3(data.getString("RSRV_STR3"));
        this.setRsrvStr4(data.getString("RSRV_STR4"));
        this.setRsrvStr5(data.getString("RSRV_STR5"));
        this.setRsrvTag1(data.getString("RSRV_TAG1"));
        this.setRsrvTag2(data.getString("RSRV_TAG2"));
        this.setRsrvTag3(data.getString("RSRV_TAG3"));
        this.setStartDate(data.getString("START_DATE"));
    }

    @Override
    public PostTradeData clone()
    {
        PostTradeData postTradeData = new PostTradeData();
        postTradeData.setCustType(this.getCustType());
        postTradeData.setEmail(this.getEmail());
        postTradeData.setEndDate(this.getEndDate());
        postTradeData.setFaxNbr(this.getFaxNbr());
        postTradeData.setId(this.getId());
        postTradeData.setIdType(this.getIdType());
        postTradeData.setModifyTag(this.getModifyTag());
        postTradeData.setPostAddress(this.getPostAddress());
        postTradeData.setPostCode(this.getPostCode());
        postTradeData.setPostContent(this.getPostContent());
        postTradeData.setPostCyc(this.getPostCyc());
        postTradeData.setPostName(this.getPostName());
        postTradeData.setInstId(this.getInstId());
        postTradeData.setPostTag(this.getPostTag());
        postTradeData.setPostTypeset(this.getPostTypeset());
        postTradeData.setRemark(this.getRemark());
        postTradeData.setRsrvDate1(this.getRsrvDate1());
        postTradeData.setRsrvDate2(this.getRsrvDate2());
        postTradeData.setRsrvDate3(this.getRsrvDate3());
        postTradeData.setRsrvNum1(this.getRsrvNum1());
        postTradeData.setRsrvNum2(this.getRsrvNum2());
        postTradeData.setRsrvNum3(this.getRsrvNum3());
        postTradeData.setRsrvNum4(this.getRsrvNum4());
        postTradeData.setRsrvNum5(this.getRsrvNum5());
        postTradeData.setRsrvStr1(this.getRsrvStr1());
        postTradeData.setRsrvStr2(this.getRsrvStr2());
        postTradeData.setRsrvStr3(this.getRsrvStr3());
        postTradeData.setRsrvStr4(this.getRsrvStr4());
        postTradeData.setRsrvStr5(this.getRsrvStr5());
        postTradeData.setRsrvTag1(this.getRsrvTag1());
        postTradeData.setRsrvTag2(this.getRsrvTag2());
        postTradeData.setRsrvTag3(this.getRsrvTag3());
        postTradeData.setStartDate(this.getStartDate());
        return postTradeData;
    }

    public String getCustType()
    {
        return custType;
    }

    public String getEmail()
    {
        return email;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getFaxNbr()
    {
        return faxNbr;
    }

    public String getId()
    {
        return id;
    }

    public String getIdType()
    {
        return idType;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getPostAddress()
    {
        return postAddress;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getPostContent()
    {
        return postContent;
    }

    public String getPostCyc()
    {
        return postCyc;
    }

    public String getPostName()
    {
        return postName;
    }

    public String getPostTag()
    {
        return postTag;
    }

    public String getPostTypeset()
    {
        return postTypeset;
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

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_POST";
    }

    public void setCustType(String custType)
    {
        this.custType = custType;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setFaxNbr(String faxNbr)
    {
        this.faxNbr = faxNbr;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setIdType(String idType)
    {
        this.idType = idType;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setPostAddress(String postAddress)
    {
        this.postAddress = postAddress;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setPostContent(String postContent)
    {
        this.postContent = postContent;
    }

    public void setPostCyc(String postCyc)
    {
        this.postCyc = postCyc;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
    }

    public void setPostTag(String postTag)
    {
        this.postTag = postTag;
    }

    public void setPostTypeset(String postTypeset)
    {
        this.postTypeset = postTypeset;
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

    @Override
    public IData toData()
    {
        IData data = new DataMap();

        data.put("CUST_TYPE", this.custType);
        data.put("EMAIL", this.email);
        data.put("END_DATE", this.endDate);
        data.put("FAX_NBR", this.faxNbr);
        data.put("ID", this.id);
        data.put("ID_TYPE", this.idType);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("POST_ADDRESS", this.postAddress);
        data.put("POST_CODE", this.postCode);
        data.put("POST_CONTENT", this.postContent);
        data.put("POST_CYC", this.postCyc);
        data.put("POST_NAME", this.postName);
        data.put("POST_TAG", this.postTag);
        data.put("POST_TYPESET", this.postTypeset);
        data.put("REMARK", this.remark);
        data.put("INST_ID", this.instId);
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
