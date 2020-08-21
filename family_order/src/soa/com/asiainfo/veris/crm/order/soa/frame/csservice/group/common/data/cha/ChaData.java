package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha;

import java.util.Map;

public class ChaData {

	private String chaSpecId;
	
	private String operCode;
	
	private String chaValueId;
	
	private String value;
	
	private String chaRelId;

	public String getChaSpecId() {
		return chaSpecId;
	}

	public void setChaSpecId(String chaSpecId) {
		this.chaSpecId = chaSpecId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getChaRelId() {
        return chaRelId;
    }

    public void setChaRelId(String chaRelId) {
        this.chaRelId = chaRelId;
    }
    
    public String getChaValueId() {
        return this.chaValueId;
    }

    public void setChaValueId(String chaValueId) {
        this.chaValueId = chaValueId;
    }
	
    public static ChaData getInstance(Map<String,String> chaSpecMap){
        ChaData chaSpec = new ChaData();
        if(chaSpecMap == null)
            return chaSpec; 
        chaSpec.setValue(chaSpecMap.get("CHA_VALUE"));
        chaSpec.setChaValueId(chaSpecMap.get("CHA_VAL_ID"));
        chaSpec.setChaSpecId(chaSpecMap.get("CHA_SPEC_ID"));
        chaSpec.setChaRelId(chaSpecMap.get("CHA_REL_ID"));
        chaSpec.setOperCode(chaSpecMap.get("OPER_CODE"));
        return chaSpec;
    }

	public String getOperCode() {
		return operCode;
	}

	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}
    
}
