package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaModData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IData;

public class GOfferModuleData
{
    private List<ChaModData> chaSpecs = new ArrayList<ChaModData>();

    private String relOfferId;

    private String relOfferInsId;
    
    private String relOfferType;

    private String offerId;

    private String offerInsId;
    
    private String groupId;

    private String offerType;

    private String validDate;

    private String expireDate;

    private String operCode;
    
    private boolean isQzNeedChgOffer = false;// 是否强制执行变更操作 

    public String getRelOfferId()
    {
        return relOfferId;
    }

    public void setRelOfferId(String relOfferId)
    {
        this.relOfferId = relOfferId;
    }
    
    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }
    
    public String getRelOfferType()
    {
        return relOfferType;
    }

    public void setRelOfferType(String relOfferType)
    {
        this.relOfferType = relOfferType;
    }

    public String getOperCode()
    {
        return operCode;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public String getRelOfferInsId()
    {
        return relOfferInsId;
    }

    public void setRelOfferInsId(String relOfferInsId)
    {
        this.relOfferInsId = relOfferInsId;
    }

    public String getOfferInsId()
    {
        return offerInsId;
    }

    public void setOfferInsId(String offerInsId)
    {
        this.offerInsId = offerInsId;
    }

    public String getOfferId()
    {
        return offerId;
    }

    public String getOfferType()
    {
        return offerType;
    }

    public void setOfferId(String elementId)
    {
        this.offerId = elementId;
    }

    public void setOfferType(String elementType)
    {
        this.offerType = elementType;
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

    public void setChaSpecs(List<ChaModData> chaSpecs)
    {
        this.chaSpecs = chaSpecs;
    }

    public void addChaSpec(ChaModData chaSpec)
    {
        this.chaSpecs.add(chaSpec);
    }

    public List<ChaModData> getChaSpecs()
    {
        return this.chaSpecs;
    }

    public static GOfferModuleData getInstance(IData offerModuleData)
    {

        if (offerModuleData == null)
            return null;

        GOfferModuleData gomd = new GOfferModuleData();
        String operCode = offerModuleData.getString("MODIFY_TAG");
        gomd.setOperCode(operCode);
        
        gomd.setOfferId(offerModuleData.getString("ELEMENT_ID"));
        gomd.setOfferInsId(offerModuleData.getString("INST_ID"));
        gomd.setRelOfferId(offerModuleData.getString("PRODUCT_ID"));
        gomd.setGroupId(offerModuleData.getString("PACKAGE_ID"));
        gomd.setOfferType(offerModuleData.getString("ELEMENT_TYPE_CODE"));
        gomd.setValidDate(offerModuleData.getString("START_DATE"));
        gomd.setExpireDate(offerModuleData.getString("END_DATE"));
        
//        gomd.setRelOfferType(offerModuleData.getString("REL_OFFER_TYPE"));
//        gomd.setRelOfferInsId(offerModuleData.getString("REL_OFFER_INS_ID"));
        
//        gomd.setOfferId(offerModuleData.getString("OFFER_ID"));
//        gomd.setOfferInsId(offerModuleData.getString("OFFER_INS_ID"));
//        gomd.setRelOfferId(offerModuleData.getString("REL_OFFER_ID"));
//        gomd.setRelOfferInsId(offerModuleData.getString("REL_OFFER_INS_ID"));
//        gomd.setOfferType(offerModuleData.getString("OFFER_TYPE"));
//        gomd.setRelOfferType(offerModuleData.getString("REL_OFFER_TYPE"));
//        gomd.setValidDate(offerModuleData.getString("VALID_DATE"));
//        gomd.setExpireDate(offerModuleData.getString("EXPIRE_DATE"));
        if (StringUtils.equals("true", offerModuleData.getString("QZ_MODIFY")))
        {
            gomd.setQzNeedChgOffer(true);
        }

        IDataset chaSpecs = offerModuleData.getDataset("ATTR_PARAMS");
        if (IDataUtil.isNotEmpty(chaSpecs))
        {

            for (int i = 0, size = chaSpecs.size(); i < size; i++)
            {
                gomd.addChaSpec(ChaModData.getInstance(chaSpecs.getData(i)));
            }
        }

        return gomd;
    }

    public boolean isQzNeedChgOffer()
    {
        return isQzNeedChgOffer;
    }

    public void setQzNeedChgOffer(boolean isQzNeedChgOffer)
    {
        this.isQzNeedChgOffer = isQzNeedChgOffer;
    }

}
