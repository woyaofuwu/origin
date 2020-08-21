package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class GOfferRelAddData
{

    private String relType;

    private String relOfferInsId;

    private String relOfferCode;
    
    private String relOfferType;
    
    public String getRelOfferType()
    {
        return relOfferType;
    }

    public void setRelOfferType(String relOfferType)
    {
        this.relOfferType = relOfferType;
    }

    public String getRelType()
    {
        return relType;
    }

    public void setRelType(String relType)
    {
        this.relType = relType;
    }

    public String getRelOfferInsId()
    {
        return this.relOfferInsId;
    }

    public void setRelOfferInsId(String relOfferInsId)
    {
        this.relOfferInsId = relOfferInsId;
    }

    public String getRelOfferCode()
    {
        return relOfferCode;
    }

    public void setRelOfferCode(String relOfferCode)
    {
        this.relOfferCode = relOfferCode;
    }

    public Map<String, String> toMap()
    {
        Map<String, String> chaSpecMap = new HashMap<String, String>();
        chaSpecMap.put("REL_TYPE", this.relType);
        chaSpecMap.put("REL_OFFER_INS_ID", this.relOfferInsId);
        chaSpecMap.put("REL_OFFER_CODE", this.relOfferCode);

        return chaSpecMap;
    }

    public IData toIData()
    {
        IData chaSpecMap = new DataMap();
        chaSpecMap.put("REL_TYPE", this.relType);
        chaSpecMap.put("REL_OFFER_INS_ID", this.relOfferInsId);
        chaSpecMap.put("REL_OFFER_CODE", this.relOfferCode);

        return chaSpecMap;
    }

}
