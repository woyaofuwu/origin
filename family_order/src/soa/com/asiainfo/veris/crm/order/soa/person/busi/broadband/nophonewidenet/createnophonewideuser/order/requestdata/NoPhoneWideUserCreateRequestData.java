
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.requestdata;

import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

public class NoPhoneWideUserCreateRequestData extends MergeWideUserCreateRequestData
{
    
    
    /**
     * 是否实名制 1:是，2:否
     */
    private String realName;
    
    /**
     * 宽带对应的手机号码
     */
    private String widenetSn;
    
    //魔百和受理时长
    private String topSetBoxTime;
    
    //魔百和受理时长费用
    private String topSetBoxFee;
    
    /**
     * 是否海工商宽带开户
     */
    private String isHGS;


    public String getIsHGS() {
		return isHGS;
	}

	public void setIsHGS(String isHGS) {
		this.isHGS = isHGS;
	}

	public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

	public String getTopSetBoxTime() {
		return topSetBoxTime;
	}

	public void setTopSetBoxTime(String topSetBoxTime) {
		this.topSetBoxTime = topSetBoxTime;
	}

	public String getTopSetBoxFee() {
		return topSetBoxFee;
	}

	public void setTopSetBoxFee(String topSetBoxFee) {
		this.topSetBoxFee = topSetBoxFee;
	}

	public String getWidenetSn() {
		return widenetSn;
	}

	public void setWidenetSn(String widenetSn) {
		this.widenetSn = widenetSn;
	}
    
}
