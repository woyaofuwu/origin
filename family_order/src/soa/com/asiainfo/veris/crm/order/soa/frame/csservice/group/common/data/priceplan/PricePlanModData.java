package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaDelData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaModData;

public class PricePlanModData
{

    private String pricePlanInsId;

    private String pricePlanId;

    private String offerInsId;

	private List<ChaAddData> addSpecs;

    private List<ChaModData> modSpecs;

    private List<ChaDelData> delSpecs;

    private List<PriceAddData> addPrices;

    private List<PriceModData> modPrices;

    private List<PriceDelData> delPrices;
    
    private String expireDate;

    public String getExpireDate()
    {
        return expireDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

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

    public void setAddPrices(List<PriceAddData> addPrices)
    {
        this.addPrices = addPrices;
    }

    public List<PriceAddData> getAddPrices()
    {
        return this.addPrices;
    }

    public void addPriceAddData(PriceAddData addPrice)
    {
        if (addPrice == null)
            return;
        if (addPrices == null)
            addPrices = new ArrayList<PriceAddData>();
        addPrices.add(addPrice);
    }

    public void setModPrices(List<PriceModData> modPrices)
    {
        this.modPrices = modPrices;
    }

    public List<PriceModData> getModPrices()
    {
        return this.modPrices;
    }

    public void addPriceModData(PriceModData modPrice)
    {
        if (modPrice == null)
            return;
        if (modPrices == null)
            modPrices = new ArrayList<PriceModData>();
        modPrices.add(modPrice);
    }

    public void setDelPrices(List<PriceDelData> delPrices)
    {
        this.delPrices = delPrices;
    }

    public List<PriceDelData> getDelPrices()
    {
        return this.delPrices;
    }

    public void addPriceDelData(PriceDelData delPrice)
    {
        if (delPrice == null)
            return;
        if (delPrices == null)
            delPrices = new ArrayList<PriceDelData>();
        delPrices.add(delPrice);
    }
    
    public String getOfferInsId() {
		return offerInsId;
	}

	public void setOfferInsId(String offerInsId) {
		this.offerInsId = offerInsId;
	}

    public static PricePlanModData getInstance(Map pricePlanInfo)
    {
        PricePlanModData ppcd = new PricePlanModData();
        if (pricePlanInfo == null)
            return ppcd;
        ppcd.setPricePlanInsId((String) pricePlanInfo.get("PRICE_PLAN_INS_ID"));
        ppcd.setPricePlanId((String) pricePlanInfo.get("PRICE_PLAN_ID"));
        ppcd.setExpireDate((String) pricePlanInfo.get("EXPIRE_DATE"));
        ppcd.setOfferInsId((String) pricePlanInfo.get("OFFER_INS_ID"));
        // 处理定价计划特征信息
        List<Map> pricePlanSpecs = (List) pricePlanInfo.get("PRICEPLAN_CHA_SPECS");
        if (pricePlanSpecs != null && pricePlanSpecs.size() > 0)
        {
            for (Map pricePlanSpec : pricePlanSpecs)
            {
                ChaModData chaModData = ChaModData.getInstance((Map) pricePlanSpec);
                ppcd.addChaModSpec(chaModData);
            }
        }

        // 处理价格信息
        List<Map> prices = (List) pricePlanInfo.get("PRICES");
        if (prices != null && prices.size() > 0)
        {
            for (Map price : prices)
            {
                String operCode = (String) ((Map) price).get("OPER_CODE");
                if (StringUtils.isBlank(operCode))
                {
                    // todo
                }
                else if (operCode.equals(TRADE_MODIFY_TAG.Add.getValue()))
                {
                    PriceAddData addPrice = PriceAddData.getInstance((Map) price);
                    ppcd.addPriceAddData(addPrice);
                }
                else if (operCode.equals(TRADE_MODIFY_TAG.DEL.getValue()))
                {
                    PriceDelData delPrice = PriceDelData.getInstance((Map) price);
                    ppcd.addPriceDelData(delPrice);
                }
                else if (operCode.equals(TRADE_MODIFY_TAG.MODI.getValue()))
                {
                    PriceModData modPrice = PriceModData.getInstance((Map) price);
                    if (StringUtils.isBlank(modPrice.getExpireDate()) && StringUtils.isNotBlank(ppcd.getExpireDate()))
                    {
                        modPrice.setExpireDate(ppcd.getExpireDate());
                    }
                    ppcd.addPriceModData(modPrice);
                }
            }
        }
        return ppcd;
    }

    public static PricePlanModData getInstance(IData pricePlanInfo)
    {
        PricePlanModData ppcd = new PricePlanModData();
        if (pricePlanInfo == null)
            return ppcd;
        ppcd.setPricePlanInsId(pricePlanInfo.getString("PRICE_PLAN_INS_ID"));
        ppcd.setPricePlanId(pricePlanInfo.getString("PRICE_PLAN_ID"));
        ppcd.setExpireDate(pricePlanInfo.getString("EXPIRE_DATE"));
        ppcd.setOfferInsId(pricePlanInfo.getString("OFFER_INS_ID"));
        // 处理定价计划特征信息
        IDataset pricePlanSpecs = pricePlanInfo.getDataset("PRICEPLAN_CHA_SPECS");
        if (IDataUtil.isNotEmpty(pricePlanSpecs))
        {
            for (Object pricePlanSpec : pricePlanSpecs)
            {
                ChaModData chaModData = ChaModData.getInstance((IData)pricePlanSpec);
                ppcd.addChaModSpec(chaModData);
            }
        }

        // 处理价格信息
        IDataset prices = pricePlanInfo.getDataset("PRICES");
        if (IDataUtil.isNotEmpty(prices))
        {
            for (int i = 0, size = prices.size(); i < size; i++)
            {
                IData price = prices.getData(i);
                String operCode = price.getString("OPER_CODE");

                if (TRADE_MODIFY_TAG.Add.getValue().equals(operCode))
                {
                    ppcd.addPriceAddData(PriceAddData.getInstance(price));
                }
                else if (TRADE_MODIFY_TAG.DEL.getValue().equals(operCode))
                {
                    ppcd.addPriceDelData(PriceDelData.getInstance(price));
                }
                else
                {
                    PriceModData modPrice = PriceModData.getInstance(price);
                    if (StringUtils.isBlank(modPrice.getExpireDate()) && StringUtils.isNotBlank(ppcd.getExpireDate()))
                    {
                        modPrice.setExpireDate(ppcd.getExpireDate());
                    }
                    ppcd.addPriceModData(modPrice);
                }

            }
        }
        return ppcd;
    }

    public IData toIData()
    {
        IDataset sheet = new DatasetList();
        if (addSpecs != null && addSpecs.size() > 0)
        {
            for (ChaAddData cha : addSpecs)
            {
                sheet.add(cha.toMap());
            }
        }
        
        if (modSpecs != null && modSpecs.size() > 0)
        {
            for (ChaModData cha : modSpecs)
            {
                sheet.add(cha.toMap());
            }
        }
        
        if (delSpecs != null && delSpecs.size() > 0)
        {
            for (ChaDelData cha : delSpecs)
            {
                sheet.add(cha.toMap());
            }
        }
        
        IData part = new DataMap();

        part.put("PRICEPLAN_CHA_SPECS", sheet);
        
        part.put("PRICE_PLAN_ID", this.pricePlanId);
        part.put("PRICE_PLAN_INS_ID", this.pricePlanInsId);
        part.put("OPER_CODE", TRADE_MODIFY_TAG.MODI.getValue());
        part.put("EXPIRE_DATE", this.expireDate);
        part.put("OFFER_INS_ID",this.offerInsId);
        return part;

    }

}
