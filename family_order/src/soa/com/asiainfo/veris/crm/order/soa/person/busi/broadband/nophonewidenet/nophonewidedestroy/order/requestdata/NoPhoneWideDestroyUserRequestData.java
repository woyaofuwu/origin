
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.requestdata;

import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.requestdata.DestroyUserNowRequestData;

/**
 * @author yuyj3
 */
public class NoPhoneWideDestroyUserRequestData extends DestroyUserNowRequestData
{
    /**
     * 
     */
    private String createWideUserTradeId;

    public String getCreateWideUserTradeId()
    {
        return createWideUserTradeId;
    }

    public void setCreateWideUserTradeId(String createWideUserTradeId)
    {
        this.createWideUserTradeId = createWideUserTradeId;
    }
    private String remove_reason_code = "";// 销户原因

    private String remove_reason = "";

    private String score = "";

    private boolean isYHFHUser = false;// 是否影号副号

    private boolean isNetNpUser = false;// 是否携转号码
    
    private String serialNumberA = ""; //宽带对应的手机号码
    
    private String modemFee = "";//光猫押金金额
    
    private String modermReturn = "0"; //是否退光猫  0 不退  1退
    
    private String isWideType="1";//宽带类型，默认GPON宽带
    
    private String modemMode="0";//光猫申领模式，默认 租赁, 申领模式  0租赁，1购买，2赠送，3自备
    
    private String modemfeestate="0";//光猫押金状态，默认正常, 押金状态  0,押金、1,已转移、2已退还、3,已沉淀
    
    private String backFee="";
    public String getBackFee() {
		return backFee;
	}

	public void setBackFee(String backFee) {
		this.backFee = backFee;
	}

    public String getModemFeeState() {
		return modemfeestate;
	}

	public void setModemFeeState(String feestate) {
		this.modemfeestate = feestate;
	}
	
    public String getModemMode() {
		return modemMode;
	}

	public void setModemMode(String modemmode) {
		this.modemMode = modemmode;
	}
    
    public String getWideType() {
		return isWideType;
	}

	public void setWideType(String widetype) {
		this.isWideType = widetype;
	}
	
    public String getModermReturn() {
		return modermReturn;
	}

	public void setModermReturn(String modermReturn) {
		this.modermReturn = modermReturn;
	}

	public String getSerialNumberA() {
		return serialNumberA;
	}

	public void setSerialNumberA(String serialNumberA) {
		this.serialNumberA = serialNumberA;
	}

	public String getModemFee() {
		return modemFee;
	}

	public void setModemFee(String modemFee) {
		this.modemFee = modemFee;
	}

	public String getRemove_reason()
    {
        return remove_reason;
    }

    public String getRemove_reason_code()
    {
        return remove_reason_code;
    }

    public String getScore()
    {
        return score;
    }

    public boolean isNetNpUser()
    {
        return isNetNpUser;
    }

    public boolean isYHFHUser()
    {
        return isYHFHUser;
    }

    public void setNetNpUser(boolean isNetNpUser)
    {
        this.isNetNpUser = isNetNpUser;
    }

    public void setRemove_reason(String remove_reason)
    {
        this.remove_reason = remove_reason;
    }

    public void setRemove_reason_code(String remove_reason_code)
    {
        this.remove_reason_code = remove_reason_code;
    }

    public void setScore(String score)
    {
        this.score = score;
    }

    public void setYHFH(boolean isYHFHUser)
    {
        this.isYHFHUser = isYHFHUser;
    }
}
