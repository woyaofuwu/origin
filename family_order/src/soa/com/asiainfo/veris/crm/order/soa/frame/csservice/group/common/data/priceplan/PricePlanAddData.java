package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;

public class PricePlanAddData
{

    private String pricePlanId;

    private List<ChaAddData> pricePlanSpecs;

    private List<PriceAddData> priceDatas;

    private String validDate;

    private String expireDate;
    
    private String priceValidType;// 生效方式 0-次日生效 1-次月生效

    public String getPriceValidType()
    {
        return priceValidType;
    }

    public void setPriceValidType(String priceValidType)
    {
        this.priceValidType = priceValidType;
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

    public String getPricePlanId()
    {
        return this.pricePlanId;
    }

    public void setPricePlanId(String pricePlanId)
    {
        this.pricePlanId = pricePlanId;
    }

    public List<ChaAddData> getPricePlanSpecs()
    {
        return this.pricePlanSpecs;
    }

    public void setPricePlanSpecs(List<ChaAddData> pricePlanSpecs)
    {
        this.pricePlanSpecs = pricePlanSpecs;
    }

    public void addPricePlanSpec(ChaAddData pricePlanSpec)
    {
        if (pricePlanSpec == null)
            return;
        if (pricePlanSpecs == null)
            pricePlanSpecs = new ArrayList<ChaAddData>();
        pricePlanSpecs.add(pricePlanSpec);
    }

    public List<PriceAddData> getPriceDatas()
    {
        return this.priceDatas;
    }

    public void setPriceDatas(List<PriceAddData> priceDatas)
    {
        this.priceDatas = priceDatas;
    }

    public void addPriceData(PriceAddData priceData)
    {
        if (priceData == null)
            return;
        if (priceDatas == null)
            priceDatas = new ArrayList<PriceAddData>();
        priceDatas.add(priceData);
    }

    public static PricePlanAddData getInstance(IData pricePlanInfo)
    {
        PricePlanAddData ppcd = new PricePlanAddData();
        if (pricePlanInfo == null)
            return ppcd;
        ppcd.setPricePlanId(pricePlanInfo.getString("PRICE_PLAN_ID"));
        ppcd.setValidDate(pricePlanInfo.getString("VALID_DATE"));
        ppcd.setExpireDate(pricePlanInfo.getString("EXPIRE_DATE"));
        ppcd.setPriceValidType(pricePlanInfo.getString("PRICE_VALID_TYPE"));

        // 处理定价计划特征信息
        IDataset pricePlanSpecs = pricePlanInfo.getDataset("PRICEPLAN_CHA_SPECS");
        if (IDataUtil.isNotEmpty(pricePlanSpecs))
        {
            for (Object pricePlanSpec : pricePlanSpecs)
            {
                ppcd.addPricePlanSpec(ChaAddData.getInstance((IData)pricePlanSpec));
            }
        }

        return ppcd;
    }

    public static PricePlanAddData getInstance(Map pricePlanInfo)
    {
        PricePlanAddData ppcd = new PricePlanAddData();
        if (pricePlanInfo == null)
            return ppcd;
        ppcd.setPricePlanId((String) pricePlanInfo.get("PRICE_PLAN_ID"));
        ppcd.setValidDate((String) pricePlanInfo.get("VALID_DATE"));
        ppcd.setExpireDate((String) pricePlanInfo.get("EXPIRE_DATE"));
        ppcd.setPriceValidType((String) pricePlanInfo.get("PRICE_VALID_TYPE"));

        // 处理定价计划特征信息
        List<Map> pricePlanSpecs = (List) pricePlanInfo.get("PRICEPLAN_CHA_SPECS");
        if (pricePlanSpecs != null && pricePlanSpecs.size() > 0)
        {
            for (Map pricePlanSpec : pricePlanSpecs)
            {
                ChaAddData chaData = ChaAddData.getInstance(pricePlanSpec);
                ppcd.addPricePlanSpec(chaData);
            }
        }

        // 处理价格信息
        List<Map> prices = (List) pricePlanInfo.get("PRICES");
        if (prices != null && prices.size() > 0)
        {
            for (Map price : prices)
            {
                PriceAddData pd = PriceAddData.getInstance(price);
                ppcd.addPriceData(pd);
            }
        }
        return ppcd;
    }

}
