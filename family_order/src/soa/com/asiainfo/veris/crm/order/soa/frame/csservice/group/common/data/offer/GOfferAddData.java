package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan.PricePlanAddData;

public class GOfferAddData extends OfferData
{

    private String offerInsId;

    private String offerNmae;

    private String validDate;

    private String expireDate;

    private String enableMode;

    private String operCode;

    private String offerType;

    private String isBund;

    private List<ChaAddData> offerChaSpecs;

    private List<PricePlanAddData> pricePlans;

    private List<GOfferRelAddData> offerRels;

    private String roleId;

    private String subscriberInsId;

    private String remark;

    private String relOfferId;
    
    private String groupId;
    
    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getRelOfferId()
    {
        return relOfferId;
    }

    public void setRelOfferId(String relOfferId)
    {
        this.relOfferId = relOfferId;
    }

    public String getSubscriberInsId()
    {
        return subscriberInsId;
    }

    public void setSubscriberInsId(String subscriberInsId)
    {
        this.subscriberInsId = subscriberInsId;
    }

    public GOfferAddData()
    {

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

    public String getOfferType()
    {
        return offerType;
    }

    public void setOfferType(String offerType)
    {
        this.offerType = offerType;
    }

    public String getEnableMode()
    {
        return enableMode;
    }

    public void setEnableMode(String enableMode)
    {
        this.enableMode = enableMode;
    }

    public List<ChaAddData> getOfferChaSpecs()
    {
        return offerChaSpecs;
    }

    public void setOfferChaSpecs(List<ChaAddData> offerChaSpecs)
    {
        this.offerChaSpecs = offerChaSpecs;
    }

    public void addOfferChaSpec(ChaAddData chaSpec)
    {
        if (chaSpec == null)
            return;
        if (offerChaSpecs == null)
            offerChaSpecs = new ArrayList<ChaAddData>();
        offerChaSpecs.add(chaSpec);
    }

    public List<PricePlanAddData> getPricePlans()
    {
        return pricePlans;
    }

    public void setPricePlans(List<PricePlanAddData> pricePlans)
    {
        this.pricePlans = pricePlans;
    }

    public void addPricePlan(PricePlanAddData price)
    {
        if (price == null)
            return;
        if (pricePlans == null)
            pricePlans = new ArrayList<PricePlanAddData>();
        pricePlans.add(price);
    }

    public List<GOfferRelAddData> getOfferRels()
    {
        return offerRels;
    }

    public void setOfferRels(List<GOfferRelAddData> offerRels)
    {
        this.offerRels = offerRels;
    }

    public void addOfferRel(GOfferRelAddData offerRel)
    {
        if (offerRel == null)
            return;
        if (offerRels == null)
            offerRels = new ArrayList<GOfferRelAddData>();
        offerRels.add(offerRel);
    }

    public void addOfferRel(String offerCode, String offerType, String offerRelInsId, String relType)
    {
        GOfferRelAddData offerRel = new GOfferRelAddData();
        offerRel.setRelOfferInsId(offerRelInsId);
        offerRel.setRelType(relType);
        offerRel.setRelOfferCode(offerCode);
        offerRel.setRelOfferType(offerType);
        addOfferRel(offerRel);
    }

    public String getRoleId()
    {
        return roleId;
    }

    public void setRoleId(String roleId)
    {
        this.roleId = roleId;
    }

    public static GOfferAddData getInstance(IData offerData) throws Exception
    {
        GOfferAddData offer = new GOfferAddData();
        if (offerData == null)
            return offer;
        offer.setOfferId(offerData.getString("OFFER_ID"));
        offer.setOfferNmae(offerData.getString("OFFER_NAME"));
        offer.setRoleId(offerData.getString("ROLE_ID"));
        offer.setOfferInsId(offerData.getString("OFFER_INS_ID"));
        offer.setIsBund(offerData.getString("IS_BUND"));
        offer.setOperCode(offerData.getString("OPER_CODE"));
        offer.setSubscriberInsId(offerData.getString("SUBSCRIBER_INS_ID"));
        if (StringUtils.isNotBlank(offerData.getString("VALID_DATE")))
            offer.setValidDate(offerData.getString("VALID_DATE"));
        if (StringUtils.isNotBlank(offerData.getString("EXPIRE_DATE")))
            offer.setExpireDate(offerData.getString("EXPIRE_DATE"));

        offer.setRemark(offerData.getString("REMARK", ""));

        // 销售品特征信息
        IDataset offerSpecs = offerData.getDataset("OFFER_CHA_SPECS");

        if (IDataUtil.isNotEmpty(offerSpecs))
        {
            for (Object offerSpec : offerSpecs)
            {
                offer.addOfferChaSpec(ChaAddData.getInstance((IData) offerSpec));
            }
        }

        // 定价计划信息-包含定价计划 +价格+特征信息
        IDataset pricePlans = offerData.getDataset("PRICE_PLANS");
        if (StringUtils.equals(offerData.getString("OPER_CODE"), TRADE_MODIFY_TAG.Add.getValue()))
        {
            pricePlans = mergerDefaultRequiredPricePlans(pricePlans, offerData.getString("OFFER_ID"));
        }

        if (IDataUtil.isNotEmpty(pricePlans))
        {
            for (Object pricePlan : pricePlans)
            {
                offer.addPricePlan(PricePlanAddData.getInstance((IData) pricePlan));
            }
        }

        // 关联的销售品信息
        IDataset offerRels = offerData.getDataset("REL_OFFERS");
        if (IDataUtil.isNotEmpty(offerRels))
        {
            for (int i = 0, size = offerRels.size(); i < size; i++)
            {
                IData offerRel = offerRels.getData(i);
                GOfferRelAddData rel = new GOfferRelAddData();
                rel.setRelOfferCode(offerRel.getString("REL_OFFER_ID"));
                rel.setRelType(offerRel.getString("REL_TYPE"));
                rel.setRelOfferInsId(offerRel.getString("REL_OFFER_INS_ID"));
                offer.addOfferRel(rel);
            }
        }

        // 处理集团和成员及主产品和附加产品的关系
        // OffersResolverTool.dealBundOfferRelInfo(offer, offerData);

        return offer;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"OFFER_ID\":\"" + this.getOfferId() + "\",");
        sb.append("\"IS_BUND\":\"" + this.getIsBund() + "\",");
        sb.append("\"OPER_CODE\":\"" + this.getOperCode() + "\",");
        sb.append("\"VALID_DATE\":\"" + this.getValidDate() + "\",");
        sb.append("\"EXPIRE_DATE\":\"" + this.getExpireDate() + "\",");
        sb.append("\"ENABLE_MODE\":\"" + this.getEnableMode() + "\",");
        sb.append("\"OFFER_INS_ID\":\"" + this.getOfferInsId() + "\"");

        if (this.offerChaSpecs != null)
        {
            sb.append(",\"OFFER_CHA\":[");
            for (ChaAddData offerCha : this.offerChaSpecs)
            {
                sb.append(offerCha.toString() + ",");
            }
            sb = sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("]");
        }

        sb.append("}");
        return sb.toString();
    }

    public String getOperCode()
    {
        return operCode;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public String getIsBund()
    {
        return isBund;
    }

    public void setIsBund(String isBund)
    {
        this.isBund = isBund;
    }

    public String getOfferInsId()
    {
        return offerInsId;
    }

    public void setOfferInsId(String offerInsId)
    {
        this.offerInsId = offerInsId;
    }

    public String getOfferNmae()
    {
        return offerNmae;
    }

    public void setOfferNmae(String offerNmae)
    {
        this.offerNmae = offerNmae;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    /**
     * 合并默认必选的定价计划, 有时候接口过来的业务没把必选的产品传过来
     * 
     * @param pricePlans
     * @param offerId
     * @throws Exception
     */
    public static IDataset mergerDefaultRequiredPricePlans(IDataset pricePlans, String offerId) throws Exception
    {
        return null;
    }
}
