package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaDelData;

public class PricePlanDelData
{

    private String pricePlanInsId;

    private String pricePlanId;

    private String offerInsId;

    private String expireDate;

    private List<ChaDelData> pricePlanSpecs;

    public String getPriceValidType()
    {
        return priceValidType;
    }

    public String getOfferInsId()
    {
        return offerInsId;
    }

    public void setOfferInsId(String offerInsId)
    {
        this.offerInsId = offerInsId;
    }

    public void setPriceValidType(String priceValidType)
    {
        this.priceValidType = priceValidType;
    }

    private List<PriceDelData> priceDatas;

    private String priceValidType;// 生效方式 0-次日生效 1-次月生效

    public String getPricePlanInsId()
    {
        return this.pricePlanInsId;
    }

    public void setPricePlanInsId(String pricePlanInsId)
    {
        this.pricePlanInsId = pricePlanInsId;
    }

    public String getPricePlanId()
    {
        return this.pricePlanId;
    }

    public void setPricePlanId(String pricePlanId)
    {
        this.pricePlanId = pricePlanId;
    }

    public String getExpireDate()
    {
        return this.expireDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

    public static PricePlanDelData getInstance(Map pricePlanInfo)
    {
        PricePlanDelData ppcd = new PricePlanDelData();
        if (pricePlanInfo == null)
            return ppcd;
        ppcd.setPricePlanInsId((String) pricePlanInfo.get("PRICE_PLAN_INS_ID"));
        ppcd.setPricePlanId((String) pricePlanInfo.get("PRICE_PLAN_ID"));
        ppcd.setExpireDate((String) pricePlanInfo.get("EXPIRE_DATE"));
        ppcd.setPriceValidType((String) pricePlanInfo.get("PRICE_VALID_TYPE"));
        ppcd.setOfferInsId((String) pricePlanInfo.get("OFFER_INS_ID"));
        return ppcd;
    }

    public static PricePlanDelData getInstance(IData pricePlanInfo)
    {
        PricePlanDelData ppcd = new PricePlanDelData();
        if (pricePlanInfo == null)
            return ppcd;
        ppcd.setPricePlanInsId(pricePlanInfo.getString("PRICE_PLAN_INS_ID"));
        ppcd.setPricePlanId(pricePlanInfo.getString("PRICE_PLAN_ID"));
        ppcd.setExpireDate(pricePlanInfo.getString("EXPIRE_DATE"));
        ppcd.setPriceValidType(pricePlanInfo.getString("PRICE_VALID_TYPE"));
        ppcd.setOfferInsId(pricePlanInfo.getString("OFFER_INS_ID"));
        // 以下为bboss要求传
        // 处理定价计划特征信息
        IDataset pricePlanSpecs = pricePlanInfo.getDataset("PRICEPLAN_CHA_SPECS");
        if (IDataUtil.isNotEmpty(pricePlanSpecs))
        {
            for (Object pricePlanSpec : pricePlanSpecs)
            {
                ChaDelData chaDelData = ChaDelData.getInstance((IData) pricePlanSpec);
                ppcd.addChaDelSpec(chaDelData);
            }
        }

        // 处理价格信息
        IDataset prices = pricePlanInfo.getDataset("PRICES");
        if (IDataUtil.isNotEmpty(prices))
        {
            for (Object price : prices)
            {
                PriceDelData delPrice = PriceDelData.getInstance((IData) price);
                ppcd.addPriceDelData(delPrice);
            }
        }
        return ppcd;
    }

    public IData toIData()
    {
        IDataset sheet = new DatasetList();
        if (pricePlanSpecs != null && pricePlanSpecs.size() > 0)
        {
            for (ChaDelData cha : pricePlanSpecs)
            {
                sheet.add(cha.toMap());
            }
        }

        IDataset list = new DatasetList();
        if (priceDatas != null && priceDatas.size() > 0)
        {
            for (PriceDelData price : priceDatas)
            {
                list.add(price.toMap());
            }
        }

        IData part = new DataMap();

        part.put("PRICEPLAN_CHA_SPECS", sheet);
        part.put("PRICES", list);
        part.put("PRICE_PLAN_ID", this.pricePlanId);
        part.put("PRICE_PLAN_INS_ID", this.pricePlanInsId);
        part.put("OPER_CODE", TRADE_MODIFY_TAG.DEL.getValue());
        part.put("PRICE_VALID_TYPE", this.priceValidType);// 生效方式
        part.put("OFFER_INS_ID", this.offerInsId);

        return part;

    }

    public List<ChaDelData> getPricePlanSpecs()
    {
        return pricePlanSpecs;
    }

    public void setPricePlanSpecs(List<ChaDelData> pricePlanSpecs)
    {
        this.pricePlanSpecs = pricePlanSpecs;
    }

    public List<PriceDelData> getPriceDatas()
    {
        return priceDatas;
    }

    public void setPriceDatas(List<PriceDelData> priceDatas)
    {
        this.priceDatas = priceDatas;
    }

    public void addChaDelSpec(ChaDelData delSpec)
    {
        if (delSpec == null)
            return;
        if (pricePlanSpecs == null)
            pricePlanSpecs = new ArrayList<ChaDelData>();
        pricePlanSpecs.add(delSpec);
    }

    public void addPriceDelData(PriceDelData delPrice)
    {
        if (delPrice == null)
            return;
        if (priceDatas == null)
            priceDatas = new ArrayList<PriceDelData>();
        priceDatas.add(delPrice);
    }
}
