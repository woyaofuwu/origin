
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: EmergencyOpenRequestData.java
 * @Description: 紧急开机requestData （注：担保开机也是使用的这个）
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-3-19
 */
public class EmergencyOpenReqData extends BaseReqData
{

    private String creditClass = "";// 紧急开机用户信用级别 、担保开机担保用户信用级别

    private String openHours = "";// 紧急开机时间（小时） 、担保开机时长

    private String guaranteeUserId = "";// 担保开机对应的担保用户编码
    
    private String openAmount = "";// 紧急开机额度 、担保开机额度

    public String getCreditClass()
    {
        return creditClass;
    }

    public String getGuaranteeUserId()
    {
        return guaranteeUserId;
    }

    public String getOpenHours()
    {
        return openHours;
    }

    public void setCreditClass(String creditClass)
    {
        this.creditClass = creditClass;
    }

    public void setGuaranteeUserId(String guaranteeUserId)
    {
        this.guaranteeUserId = guaranteeUserId;
    }

    public void setOpenHours(String openHours)
    {
        this.openHours = openHours;
    }

	/**
	 * @return the openAmount
	 */
	public String getOpenAmount() {
		return openAmount;
	}

	/**
	 * @param openAmount the openAmount to set
	 */
	public void setOpenAmount(String openAmount) {
		this.openAmount = openAmount;
	}
    
}
