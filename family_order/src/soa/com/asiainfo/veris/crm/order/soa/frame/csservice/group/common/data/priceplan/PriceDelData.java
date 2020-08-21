package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan;

import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaDelData;

public class PriceDelData
{

    private String priceInsId;

    private String priceId;

    private String expireDate;

    private List<ChaDelData> priceChaSpecs;

    private String operCode;

    public void setPriceInsId(String priceInsId)
    {
        this.priceInsId = priceInsId;
    }

    public String getPriceInsId()
    {
        return this.priceInsId;
    }

    public String getExpireDate()
    {
        return this.expireDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

    public static PriceDelData getInstance(Map priceData)
    {
        PriceDelData pd = new PriceDelData();
        if (pd == null)
            return pd;
        pd.setPriceInsId((String) priceData.get("PRICE_INS_ID"));
        pd.setPriceId((String) priceData.get("PRICE_ID"));
        pd.setExpireDate((String) priceData.get("EXPIRE_DATE"));
        pd.setOperCode((String) priceData.get("OPER_CODE"));
        return pd;
    }

    public static PriceDelData getInstance(IData priceData)
    {
        PriceDelData pd = new PriceDelData();
        if (pd == null)
            return pd;
        pd.setPriceInsId(priceData.getString("PRICE_INS_ID"));
        pd.setPriceId(priceData.getString("PRICE_ID"));
        pd.setExpireDate(priceData.getString("EXPIRE_DATE"));
        pd.setOperCode(priceData.getString("OPER_CODE", TRADE_MODIFY_TAG.DEL.getValue()));
        return pd;
    }

    public IData toMap()
    {
        IDataset sheet = new DatasetList();
        if (priceChaSpecs != null && priceChaSpecs.size() > 0)
        {
            for (ChaDelData cha : priceChaSpecs)
            {
                sheet.add(cha.toIData());
            }
        }

        IData map = new DataMap();

        map.put("OPER_CODE", operCode);
        map.put("PRICE_INS_ID", priceInsId);
        map.put("PRICE_ID", priceId);
        map.put("EXPIRE_DATE", this.expireDate);
        map.put("PRICE_CHA_SPECS", sheet);

        return map;
    }

    public List<ChaDelData> getPriceChaSpecs()
    {
        return priceChaSpecs;
    }

    public void setPriceChaSpecs(List<ChaDelData> priceChaSpecs)
    {
        this.priceChaSpecs = priceChaSpecs;
    }

    public String getPriceId()
    {
        return priceId;
    }

    public void setPriceId(String priceId)
    {
        this.priceId = priceId;
    }

    public String getOperCode()
    {
        return operCode;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }
}
