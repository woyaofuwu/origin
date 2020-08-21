package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;

public class GAddOfferRelVerb extends GVerb
{
    private String offerCode;
    
    private String offerType;
    
    private String relOfferCode;
    
    private String relOfferType;
    
    private String offerInsId;
    
    private String relOfferInsId;

    private String relType;
    
    private boolean isNeedBackfillOfferInsId = false;// 是否需要回填offerInsId, 因为之前可能还没有生成offerInsId只有subscribeInsId
    
    private String validDate;

    private String expireDate;
    
    private String regionId;
    
    private String groupId;
    
    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getRegionId()
    {
        return regionId;
    }
    
    public String getOfferType()
    {
        return offerType;
    }

    public void setOfferType(String offerType)
    {
        this.offerType = offerType;
    }

    public String getRelOfferType()
    {
        return relOfferType;
    }

    public void setRelOfferType(String relOfferType)
    {
        this.relOfferType = relOfferType;
    }

    public void setRegionId(String regionId)
    {
        this.regionId = regionId;
    }

    public String getValidDate()
    {
        return validDate;
    }

    public void setValidDate(String validDate)
    {
        this.validDate = validDate;
    }

    public String getExpireDate()
    {
        return expireDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }
    
    public String getOfferCode()
    {
        return offerCode;
    }

    public void setOfferCode(String offerCode)
    {
        this.offerCode = offerCode;
    }

    public String getRelOfferCode()
    {
        return relOfferCode;
    }

    public void setRelOfferCode(String relOfferCode)
    {
        this.relOfferCode = relOfferCode;
    }

    public GAddOfferRelVerb() throws Exception
    {
    	super();
    }

    public GAddOfferRelVerb(String offerInsId, String offerCode, String offerType, String relOfferInsId, String relOfferCode, String relOfferType, String relType) throws Exception
    {
    	super();
        this.offerInsId = offerInsId;
        this.offerCode = offerCode;
        this.offerType = offerType;
        this.relOfferInsId = relOfferInsId;
        this.relOfferCode = relOfferCode;
        this.relOfferType = relOfferType;
        this.relType = relType;
    }

    public IData run(GroupBaseReqData reqData) throws Exception
    {
        
        String offerRelInsId = SeqMgr.getInstId();
        if(StringUtils.isEmpty(validDate))
        	validDate = SysDateMgr.getSysDate();
        if(StringUtils.isEmpty(expireDate))
        	expireDate =  SysDateMgr.getTheLastTime();
        
        IData omOfferRel = new DataMap();
        omOfferRel.put("BUNDLE_OFFER_REL_INS_ID", offerRelInsId);
        omOfferRel.put("OFFER_INS_ID", offerInsId);
        omOfferRel.put("OFFER_CODE", offerCode);
        omOfferRel.put("OFFER_TYPE", offerType);
        omOfferRel.put("REL_OFFER_INS_ID", relOfferInsId);
        omOfferRel.put("REL_OFFER_CODE", relOfferCode);
        omOfferRel.put("REL_OFFER_TYPE", relOfferType);
        omOfferRel.put("REL_TYPE", relType);
        omOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        omOfferRel.put("START_DATE", validDate);
        omOfferRel.put("END_DATE", expireDate);
        omOfferRel.put("GROUP_ID", groupId);
        
        reqData.cd.getOfferRel().add(omOfferRel);
        return omOfferRel;
    }

    public boolean isNeedBackfillOfferInsId()
    {
        return isNeedBackfillOfferInsId;
    }

    public void setNeedBackfillOfferInsId(boolean isNeedBackfillOfferInsId)
    {
        this.isNeedBackfillOfferInsId = isNeedBackfillOfferInsId;
    }
}
