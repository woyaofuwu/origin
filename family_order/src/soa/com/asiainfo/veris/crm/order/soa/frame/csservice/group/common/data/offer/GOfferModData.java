package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaDelData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan.PricePlanAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan.PricePlanDelData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan.PricePlanModData;

public class GOfferModData extends OfferData
{

    private String offerInsId;

    private String validDate;

    private String expireDate;

    private String offerType;

    private List<ChaAddData> addSpecs;

    private List<ChaModData> modSpecs;

    private List<ChaDelData> delSpecs;

    private List<PricePlanAddData> addPricePlans;

    private List<PricePlanModData> modPricePlans;

    private List<PricePlanDelData> delPricePlans;

    private List<GOfferRelAddData> addOfferRels;

    private List<GOfferRelModData> modOfferRels;

    private List<GOfferRelDelData> delOfferRels;

    private String roleId;

    private String remark;

    private String operCode;
    
    private boolean isQzNeedChgOffer = false;// 强制修改 默认不修改

    public boolean isQzNeedChgOffer()
    {
        return isQzNeedChgOffer;
    }

    public void setQzNeedChgOffer(boolean isQzNeedChgOffer)
    {
        this.isQzNeedChgOffer = isQzNeedChgOffer;
    }

    public String getOperCode()
    {
        return operCode;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public GOfferModData()
    {

    }

    public String getOfferInsId()
    {
        return offerInsId;
    }

    public void setOfferInsId(String offerInsId)
    {
        this.offerInsId = offerInsId;
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

    public void setAddChaSpecs(List<ChaAddData> addSpecs)
    {
        this.addSpecs = addSpecs;
    }

    public List<ChaAddData> getAddChaSpecs()
    {
        return this.addSpecs;
    }

    public void addChaAddSpec(ChaAddData addSpec)
    {
        if (addSpec == null)
            return;
        if (addSpecs == null)
            addSpecs = new ArrayList<ChaAddData>();
        addSpecs.add(addSpec);
    }

    public void setModChaSpecs(List<ChaModData> modSpecs)
    {
        this.modSpecs = modSpecs;
    }

    public List<ChaModData> getModChaSpecs()
    {
        return this.modSpecs;
    }

    public void addChaModSpec(ChaModData modSpec)
    {
        if (modSpec == null)
            return;
        if (modSpecs == null)
            modSpecs = new ArrayList<ChaModData>();
        modSpecs.add(modSpec);
    }

    public void setDelChaSpecs(List<ChaDelData> delSpecs)
    {
        this.delSpecs = delSpecs;
    }

    public List<ChaDelData> getDelChaSpecs()
    {
        return this.delSpecs;
    }

    public void addChaDelSpec(ChaDelData delSpec)
    {
        if (delSpec == null)
            return;
        if (delSpecs == null)
            delSpecs = new ArrayList<ChaDelData>();
        delSpecs.add(delSpec);
    }

    public List<PricePlanAddData> getAddPricePlans()
    {
        return addPricePlans;
    }

    public void setAddPricePlans(List<PricePlanAddData> addPricePlans)
    {
        this.addPricePlans = addPricePlans;
    }

    public void addPricePlanAddData(PricePlanAddData addPricePlan)
    {
        if (addPricePlan == null)
            return;
        if (addPricePlans == null)
            addPricePlans = new ArrayList<PricePlanAddData>();
        addPricePlans.add(addPricePlan);
    }

    public List<PricePlanModData> getModPricePlans()
    {
        return modPricePlans;
    }

    public void setModPricePlans(List<PricePlanModData> modPricePlans)
    {
        this.modPricePlans = modPricePlans;
    }

    public void addPricePlanModData(PricePlanModData modPricePlan)
    {
        if (modPricePlan == null)
            return;
        if (modPricePlans == null)
            modPricePlans = new ArrayList<PricePlanModData>();
        modPricePlans.add(modPricePlan);
    }

    public List<PricePlanDelData> getDelPricePlans()
    {
        return delPricePlans;
    }

    public void setDelPricePlans(List<PricePlanDelData> delPricePlans)
    {
        this.delPricePlans = delPricePlans;
    }

    public void addPricePlanDelData(PricePlanDelData delPricePlan)
    {
        if (delPricePlan == null)
            return;
        if (delPricePlans == null)
            delPricePlans = new ArrayList<PricePlanDelData>();
        delPricePlans.add(delPricePlan);
    }

    public void addOfferRel(GOfferRelAddData offerRel)
    {
        if (offerRel == null)
            return;
        if (addOfferRels == null)
            addOfferRels = new ArrayList<GOfferRelAddData>();
        addOfferRels.add(offerRel);
    }

    public void addOfferRel(String offerRelInsId, String relType)
    {
        GOfferRelAddData offerRel = new GOfferRelAddData();
        offerRel.setRelOfferInsId(offerRelInsId);
        offerRel.setRelType(relType);
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

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public static GOfferModData getInstance(IData offerData) throws Exception
    {
        GOfferModData offer = new GOfferModData();
        if (offerData == null)
            return offer;
        offer.setOfferInsId(offerData.getString("OFFER_INS_ID"));
        offer.setOfferId(offerData.getString("OFFER_ID"));
        offer.setRoleId(offerData.getString("ROLE_ID"));
        offer.setRemark(offerData.getString("REMARK", ""));
        offer.setOperCode(offerData.getString("OPER_CODE"));
        offer.setOfferType(offerData.getString("OFFER_TYPE"));

        // 销售品特征信息
        IDataset offerSpecs = offerData.getDataset("OFFER_CHA_SPECS");
        if (IDataUtil.isNotEmpty(offerSpecs))
        {
            for (Object offerSpec : offerSpecs)
            {
                offer.addChaModSpec(ChaModData.getInstance((IData) offerSpec));
            }
        }

        // 定价计划信息-包含定价计划 +价格+特征信息
        IDataset pricePlans = offerData.getDataset("PRICE_PLANS");
        if (IDataUtil.isNotEmpty(pricePlans))
        {
            for (int i = 0, size = pricePlans.size(); i < size; i++)
            {
                IData pricePlan = pricePlans.getData(i);
                String operCode = pricePlan.getString("OPER_CODE");

                if (TRADE_MODIFY_TAG.Add.getValue().equals(operCode))
                {
                    offer.addPricePlanAddData(PricePlanAddData.getInstance(pricePlan));
                }
                else if (TRADE_MODIFY_TAG.DEL.getValue().equals(operCode))
                {
                    offer.addPricePlanDelData(PricePlanDelData.getInstance(pricePlan));
                }
                else
                {
                    offer.addPricePlanModData(PricePlanModData.getInstance(pricePlan));
                }
            }
        }

        // 关联的销售品信息
        IDataset offerRels = offerData.getDataset("REL_OFFERS");
        if (IDataUtil.isNotEmpty(offerRels))
        {
            for (int i = 0, size = offerRels.size(); i < size; i++)
            {
                IData offerRel = offerRels.getData(i);
                String operCode = offerRel.getString("OPER_CODE");

                GOfferRelAddData rel = new GOfferRelAddData();
                rel.setRelOfferCode(offerRel.getString("REL_OFFER_ID"));
                rel.setRelType(offerRel.getString("REL_TYPE"));
                rel.setRelOfferInsId(offerRel.getString("REL_OFFER_INS_ID"));
                offer.addOfferRel(rel);
            }
        }

        return offer;

    }

    public List<GOfferRelAddData> getAddOfferRels()
    {
        return addOfferRels;
    }

    public void setAddOfferRels(List<GOfferRelAddData> addOfferRels)
    {
        this.addOfferRels = addOfferRels;
    }

    public List<GOfferRelModData> getModOfferRels()
    {
        return modOfferRels;
    }

    public void setModOfferRels(List<GOfferRelModData> modOfferRels)
    {
        this.modOfferRels = modOfferRels;
    }

    public List<GOfferRelDelData> getDelOfferRels()
    {
        return delOfferRels;
    }

    public void setDelOfferRels(List<GOfferRelDelData> delOfferRels)
    {
        this.delOfferRels = delOfferRels;
    }

    public List<ChaAddData> getAddSpecs()
    {
        return addSpecs;
    }

    public void setAddSpecs(List<ChaAddData> addSpecs)
    {
        this.addSpecs = addSpecs;
    }

    public List<ChaModData> getModSpecs()
    {
        return modSpecs;
    }

    public void setModSpecs(List<ChaModData> modSpecs)
    {
        this.modSpecs = modSpecs;
    }

    public List<ChaDelData> getDelSpecs()
    {
        return delSpecs;
    }

    public void setDelSpecs(List<ChaDelData> delSpecs)
    {
        this.delSpecs = delSpecs;
    }

}
