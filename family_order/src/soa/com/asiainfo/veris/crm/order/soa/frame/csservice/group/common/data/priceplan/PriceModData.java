package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.priceplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.ArrayUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaDelData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaModData;

public class PriceModData
{

    private String priceInsId;

    private String priceId;

    private String isBundle;

    private String cycle;

    private String priceType;

    private String calType;

    private String priceVal;

    private String ruleId;

    private String validDate;

    private String expireDate;

    private List<ChaAddData> addSpecs;

    private List<ChaModData> modSpecs;

    private List<ChaDelData> delSpecs;
    
    private String operCode;

    public String getOperCode()
    {
        return operCode;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public void setPriceInsId(String priceInsId)
    {
        this.priceInsId = priceInsId;
    }

    public String getPriceInsId()
    {
        return this.priceInsId;
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

    public static PriceModData getInstance(Map priceData)
    {
        PriceModData pd = new PriceModData();
        if (pd == null)
            return pd;
        pd.setPriceInsId((String) priceData.get("PRICE_INS_ID"));
        pd.setPriceId((String) priceData.get("PRICE_ID"));
        pd.setPriceVal((String) priceData.get("PRICE_VAL"));
        pd.setOperCode((String) priceData.get("OPER_CODE"));
        pd.setExpireDate((String) priceData.get("EXPIRE_DATE"));
        List priceSpecs = (List) priceData.get("PRICE_CHA_SPECS");
        if (priceSpecs != null && priceSpecs.size() > 0)
        {
            List<ChaData> specs = new ArrayList<ChaData>();
            for (Object priceSpec : priceSpecs)
            {
                String operCode = (String) ((Map) priceSpec).get("OPER_CODE");
                if (StringUtils.isBlank(operCode))
                {
                    // todo
                }
                else if (operCode.equals(TRADE_MODIFY_TAG.Add.getValue()))
                {
                    ChaAddData chaAddData = ChaAddData.getInstance((Map) priceSpec);
                    pd.addChaAddSpec(chaAddData);
                }
                else if (operCode.equals(TRADE_MODIFY_TAG.DEL.getValue()))
                {
                    ChaDelData chaDelData = ChaDelData.getInstance((Map) priceSpec);
                    pd.addChaDelSpec(chaDelData);
                }
                else if (operCode.equals(TRADE_MODIFY_TAG.MODI.getValue()))
                {
                    ChaModData chaModData = ChaModData.getInstance((Map) priceSpec);
                    pd.addChaModSpec(chaModData);
                }

            }
        }
        return pd;

    }

    public static PriceModData getInstance(IData priceData)
    {
        PriceModData pd = new PriceModData();
        if (pd == null)
            return pd;
        pd.setPriceInsId(priceData.getString("PRICE_INS_ID"));
        pd.setPriceId(priceData.getString("PRICE_ID"));
        pd.setPriceVal(priceData.getString("PRICE_VAL"));
        pd.setOperCode(priceData.getString("OPER_CODE", TRADE_MODIFY_TAG.MODI.getValue()));
        pd.setExpireDate(priceData.getString("EXPIRE_DATE"));
        IDataset priceSpecs = priceData.getDataset("PRICE_CHA_SPECS");
        if (IDataUtil.isNotEmpty(priceSpecs))
        {
            for (Object priceSpec : priceSpecs)
            {
                ChaModData chaModData = ChaModData.getInstance((IData)priceSpec);
                pd.addChaModSpec(chaModData);
            }
        }
        return pd;

    }

    public IData toMap()
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

        IData map = new DataMap();

        map.put("OPER_CODE", this.operCode);
        map.put("PRICE_INS_ID", this.priceInsId);
        map.put("PRICE_ID", this.priceId);
        map.put("PRICE_TYPE", this.priceType);
        map.put("PRICE_VAL", this.priceVal);
        map.put("CALL_TYPE", this.calType);
        map.put("CYCLE", this.cycle);
        map.put("EXPIRE_DATE", this.expireDate);
        map.put("IS_BUNDLE", this.isBundle);
        map.put("RULE_ID", this.ruleId);
        map.put("VALID_DATE", this.validDate);
        map.put("PRICE_CHA_SPECS", sheet);

        return map;
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
