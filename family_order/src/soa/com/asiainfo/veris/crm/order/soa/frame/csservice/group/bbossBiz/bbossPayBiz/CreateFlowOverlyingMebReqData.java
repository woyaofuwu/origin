
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossPayBiz;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

/**
 * @description
 * @author chengjian
 * @data 2015-7-14下午02:58:33
 */
public class CreateFlowOverlyingMebReqData extends MemberReqData
{
	private IData bbossProductInfo;

	

	public IData getBbossProductInfo() {
		return bbossProductInfo;
	}

	
	public void setBbossProductInfo(IData bbossProductInfo) {
		this.bbossProductInfo = bbossProductInfo;
	} 
	
}
