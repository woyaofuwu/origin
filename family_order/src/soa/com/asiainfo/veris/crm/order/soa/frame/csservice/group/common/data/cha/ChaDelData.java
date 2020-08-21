package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class ChaDelData
{

    private String chaInsId;

    private String expireDate;

    public String getChaInsId()
    {
        return chaInsId;
    }

    public void setChaInsId(String chaInsId)
    {
        this.chaInsId = chaInsId;
    }

    public String getExpireData()
    {
        return this.expireDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

    public Map<String, String> toMap()
    {
        Map<String, String> chaSpecMap = new HashMap<String, String>();
        chaSpecMap.put("INS_ID", this.chaInsId);
        chaSpecMap.put("CHA_INS_ID", this.chaInsId);
        chaSpecMap.put("EXPIRE_DATE", this.expireDate);

        return chaSpecMap;
    }
    
    public IData toIData()
    {
        IData chaSpecMap = new DataMap();
        chaSpecMap.put("INS_ID", this.chaInsId);
        chaSpecMap.put("CHA_INS_ID", this.chaInsId);
        chaSpecMap.put("EXPIRE_DATE", this.expireDate);

        return chaSpecMap;
    }

    public static ChaDelData getInstance(Map<String, String> chaSpecMap)
    {
        ChaDelData chaSpec = new ChaDelData();
        if (chaSpecMap == null)
            return chaSpec;
        chaSpec.setChaInsId(chaSpecMap.get("CHA_INS_ID"));
        chaSpec.setExpireDate(chaSpecMap.get("EXPIRE_DATE"));
        return chaSpec;
    }

    public static ChaDelData getInstance(IData chaSpecMap)
    {
        ChaDelData chaSpec = new ChaDelData();
        if (chaSpecMap == null)
            return chaSpec;
        chaSpec.setChaInsId(chaSpecMap.getString("CHA_INS_ID"));
        chaSpec.setExpireDate(chaSpecMap.getString("EXPIRE_DATE"));
        return chaSpec;
    }

}
