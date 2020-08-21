package com.asiainfo.veris.crm.iorder.soa.family.busi.destroy.order.requestdata;

import com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata.BaseFamilyBusiReqData;

public class FamilyDestroyReqData extends BaseFamilyBusiReqData
{
    // 注销恢复产品标志
    private String recoverProductTag;

    
	public String getRecoverProductTag() {
		return recoverProductTag;
	}
	
	public void setRecoverProductTag(String recoverProductTag) {
		this.recoverProductTag = recoverProductTag;
	}
    
}
