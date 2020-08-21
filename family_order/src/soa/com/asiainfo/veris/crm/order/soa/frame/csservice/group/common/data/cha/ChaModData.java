package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class ChaModData
{

    private String chaInsId;

    private String value;

    private String chaSpecId;

    private String chaSpecCode;

    private String chaValueId;

    private String chaSpecValCode;

    private String operCode;

    private boolean isFlexDisCha;// 弹性资费属性标记

    private String groupAttr;// bboss业务属性组

    private String groupAttrName;// bboss业务属性组

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

    public boolean isFlexDisCha()
    {
        return isFlexDisCha;
    }

    public void setFlexDisCha(boolean isFlexDisCha)
    {
        this.isFlexDisCha = isFlexDisCha;
    }

    public String getChaSpecId()
    {
        return chaSpecId;
    }

    public void setChaSpecId(String chaSpecId)
    {
        this.chaSpecId = chaSpecId;
    }

    public String getChaInsId()
    {
        return chaInsId;
    }

    public void setChaInsId(String chaInsId)
    {
        this.chaInsId = chaInsId;
    }

    public String getOperCode()
    {
        return operCode;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Map<String, String> toMap()
    {
        Map<String, String> chaSpecMap = new HashMap<String, String>();
        chaSpecMap.put("INS_ID", this.chaInsId);
        chaSpecMap.put("CHA_INS_ID", this.chaInsId);
        chaSpecMap.put("CHA_SPEC_VALUE", this.value);
        chaSpecMap.put("CHA_VALUE", this.value);
        chaSpecMap.put("CHA_SPEC_CODE", this.chaSpecCode);
        chaSpecMap.put("CHA_VAL_ID", this.chaValueId);
        chaSpecMap.put("CHA_SPEC_VAL_CODE", this.chaSpecValCode);
        chaSpecMap.put("CHA_SPEC_ID", this.chaSpecId);

        return chaSpecMap;
    }

    public static ChaModData getInstance(Map<String, String> chaSpecMap)
    {
        ChaModData chaSpec = new ChaModData();
        if (chaSpecMap == null)
            return chaSpec;
        chaSpec.setValue(chaSpecMap.get("CHA_VALUE"));
        chaSpec.setChaInsId(chaSpecMap.get("CHA_INS_ID"));
        chaSpec.setChaSpecId(chaSpecMap.get("CHA_SPEC_ID"));
        chaSpec.setChaValueId(chaSpecMap.get("CHA_VAL_ID"));
        chaSpec.setChaSpecCode(chaSpecMap.get("CHA_SPEC_CODE"));
        chaSpec.setChaSpecValCode(chaSpecMap.get("CHA_SPEC_VAL_CODE"));
        return chaSpec;
    }

    public static ChaModData getInstance(IData chaSpecMap)
    {
        ChaModData chaSpec = new ChaModData();
        if (chaSpecMap == null)
            return chaSpec;
        chaSpec.setValue(chaSpecMap.getString("CHA_VALUE"));
        chaSpec.setChaInsId(chaSpecMap.getString("CHA_INS_ID"));
        chaSpec.setChaSpecId(chaSpecMap.getString("CHA_SPEC_ID"));
        chaSpec.setChaValueId(chaSpecMap.getString("CHA_VAL_ID"));
        chaSpec.setChaSpecCode(chaSpecMap.getString("CHA_SPEC_CODE"));
        chaSpec.setChaSpecValCode(chaSpecMap.getString("CHA_SPEC_VAL_CODE"));
        chaSpec.setGroupAttr(chaSpecMap.getString("GROUP_ATTR"));
        chaSpec.setGroupAttrName(chaSpecMap.getString("GROUP_ATTR_NAME"));
        chaSpec.setOperCode(chaSpecMap.getString("OPER_CODE"));
        return chaSpec;
    }

    public static List<ChaModData> getInstance(IDataset chaSpecMapset)
    {

        if (chaSpecMapset == null)
            return null;

        List<ChaModData> chaList = new ArrayList<ChaModData>();
        for (int i = 0, size = chaSpecMapset.size(); i < size; i++)
        {
            chaList.add(getInstance(chaSpecMapset.getData(i)));
        }
        return chaList;

    }

    public static ChaAddData getChaAddData(ChaModData chaModData)
    {
        ChaAddData chaSpec = new ChaAddData();
        if (chaModData == null)
            return chaSpec;
        chaSpec.setChaSpecValCode(chaModData.getChaSpecValCode());
        chaSpec.setChaSpecCode(chaModData.getChaSpecCode());
        chaSpec.setValue(chaModData.getValue());
        chaSpec.setChaSpecId(chaModData.getChaSpecId());
        chaSpec.setChaValueId(chaModData.getChaValueId());
        chaSpec.setGroupAttr(chaModData.getGroupAttr());
        chaSpec.setGroupAttrName(chaModData.getGroupAttrName());
        return chaSpec;
    }

    public String getChaValueId()
    {
        return chaValueId;
    }

    public void setChaValueId(String chaValueId)
    {
        this.chaValueId = chaValueId;
    }

    public String getChaSpecValCode()
    {
        return chaSpecValCode;
    }

    public void setChaSpecValCode(String chaSpecValCode)
    {
        this.chaSpecValCode = chaSpecValCode;
    }

    public String getChaSpecCode()
    {
        return chaSpecCode;
    }

    public void setChaSpecCode(String chaSpecCode)
    {
        this.chaSpecCode = chaSpecCode;
    }

}
