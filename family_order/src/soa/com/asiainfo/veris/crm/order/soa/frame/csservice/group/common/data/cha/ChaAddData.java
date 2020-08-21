package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class ChaAddData
{

    private String chaSpecId;

    private String chaValueId;

    private String value;
    
    private String chaSpecCode;

    private String chaSpecValCode;

    private String validDate;
    
    private String groupAttr;//bboss业务属性组
    
    private String groupAttrName;//bboss业务属性组

    public String getGroupAttr()
    {
        return groupAttr;
    }

    public void setGroupAttr(String groupAttr)
    {
        this.groupAttr = groupAttr;
    }
    
    public String getGroupAttrName()
    {
        return groupAttrName;
    }

    public void setGroupAttrName(String groupAttrName)
    {
        this.groupAttrName = groupAttrName;
    }
    
    public String getChaSpecValCode()
    {
        return chaSpecValCode;
    }

    public void setChaSpecValCode(String chaSpecValCode)
    {
        this.chaSpecValCode = chaSpecValCode;
    }

    public String getChaSpecId()
    {
        return chaSpecId;
    }

    public void setChaSpecId(String chaSpecId)
    {
        this.chaSpecId = chaSpecId;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getChaValueId()
    {
        return this.chaValueId;
    }

    public void setChaValueId(String chaValueId)
    {
        this.chaValueId = chaValueId;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"CHA_SPEC_ID\":\"" + this.chaSpecId + "\",");
        sb.append("\"CHA_SPEC_CODE\":\"" + this.chaSpecCode + "\",");
        sb.append("\"CHA_VAL_ID\":\"" + this.chaValueId + "\",");
        sb.append("\"CHA_VALUE\":\"" + this.value + "\",");
        sb.append("\"CHA_SPEC_VAL_CODE\":\"" + this.chaSpecValCode + "\"}");
        sb.append("\"VALID_DATE\":\"" + this.validDate + "\"}");
        return sb.toString();
    }

    public Map<String, String> toMap()
    {
        Map<String, String> chaSpecMap = new HashMap<String, String>();
        chaSpecMap.put("CHA_SPEC_ID", this.chaSpecId);
        chaSpecMap.put("CHA_SPEC_VAL_ID", this.chaValueId);
        chaSpecMap.put("CHA_SPEC_VAL", this.value);
        chaSpecMap.put("CHA_VAL_ID", this.chaValueId);
        chaSpecMap.put("CHA_VALUE", this.value);
        chaSpecMap.put("CHA_SPEC_CODE", this.chaSpecCode);
        chaSpecMap.put("CHA_SPEC_VAL_CODE", this.chaSpecValCode);
        chaSpecMap.put("VALID_DATE", this.validDate	);
        chaSpecMap.put("GROUP_ATTR", this.groupAttr	);

        return chaSpecMap;
    }

    public static ChaAddData getInstance(Map<String, String> chaSpecMap)
    {
        ChaAddData chaSpec = new ChaAddData();
        if (chaSpecMap == null)
            return chaSpec;
        chaSpec.setValue(chaSpecMap.get("CHA_VALUE"));
        chaSpec.setChaValueId(chaSpecMap.get("CHA_VAL_ID"));
        chaSpec.setChaSpecId(chaSpecMap.get("CHA_SPEC_ID"));
        chaSpec.setChaSpecCode(chaSpecMap.get("CHA_SPEC_CODE"));
        chaSpec.setChaSpecValCode(chaSpecMap.get("CHA_SPEC_VAL_CODE"));
        chaSpec.setValidDate(chaSpecMap.get("VALID_DATE"));
        chaSpec.setGroupAttr(chaSpecMap.get("GROUP_ATTR"));
        chaSpec.setGroupAttrName(chaSpecMap.get("GROUP_ATTR_NAME"));
        return chaSpec;
    }

    public static ChaAddData getInstance(IData chaSpecMap)
    {
        ChaAddData chaSpec = new ChaAddData();
        if (chaSpecMap == null)
            return chaSpec;
        chaSpec.setValue(chaSpecMap.getString("CHA_VALUE"));
        chaSpec.setChaValueId(chaSpecMap.getString("CHA_VAL_ID"));
        chaSpec.setChaSpecId(chaSpecMap.getString("CHA_SPEC_ID"));
        chaSpec.setChaSpecCode(chaSpecMap.getString("CHA_SPEC_CODE"));
        chaSpec.setChaSpecValCode(chaSpecMap.getString("CHA_SPEC_VAL_CODE"));
        chaSpec.setValidDate(chaSpecMap.getString("VALID_DATE"));
        chaSpec.setGroupAttr(chaSpecMap.getString("GROUP_ATTR"));
        return chaSpec;
    }
    
    public IData toIData()
    {
        IData chaSpecMap = new DataMap();
        chaSpecMap.put("CHA_SPEC_ID", this.chaSpecId);
        chaSpecMap.put("CHA_VAL_ID", this.chaValueId);
        chaSpecMap.put("CHA_VALUE", this.value);
        chaSpecMap.put("CHA_SPEC_CODE", this.chaSpecCode);
        chaSpecMap.put("CHA_SPEC_VAL_CODE", this.chaSpecValCode);
        chaSpecMap.put("VALID_DATE", this.validDate);
        chaSpecMap.put("GROUP_ATTR", this.groupAttr );

        return chaSpecMap;
    }
    
    public String getChaSpecCode()
    {
        return chaSpecCode;
    }

    public void setChaSpecCode(String chaSpecCode)
    {
        this.chaSpecCode = chaSpecCode;
    }

	public String getValidDate() {
		return validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

}
