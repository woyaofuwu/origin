package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.ArrayUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;

public class PriceAddData
{

    private String priceId;

    private String isBundle;

    private String cycle;

    private String priceType;

    private String calType;

    private String priceVal;

    private String ruleId;

    private String validDate;

    private String expireDate;

    private List<ChaAddData> priceChaSpecs;
    
    private String operCode;

    public String getOperCode()
    {
        return operCode;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public void setPriceId(String priceId)
    {
        this.priceId = priceId;
    }

    public String getPriceId()
    {
        return this.priceId;
    }

    public void setIsBundle(String isBundle)
    {
        this.isBundle = isBundle;
    }

    public String getIsBundle()
    {
        return this.isBundle;
    }

    public void setCycle(String cycle)
    {
        this.cycle = cycle;
    }

    public String getCycle()
    {
        return this.cycle;
    }

    public void setPriceType(String priceType)
    {
        this.priceType = priceType;
    }

    public String getPriceType()
    {
        return this.priceType;
    }

    public void setCalType(String calType)
    {
        this.calType = calType;
    }

    public String getCalType()
    {
        return this.calType;
    }

    public void setPriceVal(String priceVal)
    {
        this.priceVal = priceVal;
    }

    public String getPriceVal()
    {
        return this.priceVal;
    }

    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }

    public String getRuleId()
    {
        return this.ruleId;
    }

    public void setValidDate(String validDate)
    {
        this.validDate = validDate;
    }

    public String getValidDate()
    {
        return this.validDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

    public String getExpireDate()
    {
        return this.expireDate;
    }

    public void setPriceChaSpecs(List<ChaAddData> priceChaSpecs)
    {
        this.priceChaSpecs = priceChaSpecs;
    }

    public List<ChaAddData> getPriceChaSpecs()
    {
        return this.priceChaSpecs;
    }

    public static PriceAddData getInstance(Map priceData)
    {
        PriceAddData pd = new PriceAddData();
        if (pd == null)
            return pd;
        pd.setPriceId((String) priceData.get("PRICE_ID"));
        pd.setPriceVal((String) priceData.get("PRICE_VAL"));
        pd.setOperCode((String) priceData.get("OPER_CODE"));
        pd.setValidDate((String) priceData.get("VALID_DATE"));
        pd.setExpireDate((String) priceData.get("EXPIRE_DATE"));
        List priceSpecs = (List) priceData.get("PRICE_CHA_SPECS");
        if (priceSpecs != null && priceSpecs.size() > 0)
        {
            List<ChaAddData> specs = new ArrayList<ChaAddData>();
            for (Object priceSpec : priceSpecs)
            {
                ChaAddData chaData = ChaAddData.getInstance((Map) priceSpec);
                specs.add(chaData);
            }
            pd.setPriceChaSpecs(specs);
        }
        return pd;

    }

    public static PriceAddData getInstance(IData priceData)
    {
        PriceAddData pd = new PriceAddData();

        pd.setPriceId(priceData.getString("PRICE_ID"));
        pd.setPriceVal(priceData.getString("PRICE_VAL"));
        pd.setOperCode(priceData.getString("OPER_CODE", TRADE_MODIFY_TAG.Add.getValue()));
        pd.setValidDate(priceData.getString("VALID_DATE"));
        pd.setExpireDate(priceData.getString("EXPIRE_DATE"));
        IDataset priceSpecs = priceData.getDataset("PRICE_CHA_SPECS");
        if (IDataUtil.isNotEmpty(priceSpecs))
        {
            List<ChaAddData> specs = new ArrayList<ChaAddData>();
            for (Object priceSpec : priceSpecs)
            {
                specs.add(ChaAddData.getInstance((IData)priceSpec));
            }
            pd.setPriceChaSpecs(specs);
        }
        return pd;

    }
}
