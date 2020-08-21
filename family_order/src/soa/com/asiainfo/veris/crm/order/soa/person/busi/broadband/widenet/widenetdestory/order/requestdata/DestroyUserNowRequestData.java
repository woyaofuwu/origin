
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @author think
 */
public class DestroyUserNowRequestData extends BaseReqData
{

    private String remove_reason_code = "";// 销户原因

    private String remove_reason = "";

    private String score = "";

    private boolean isYHFHUser = false;// 是否影号副号

    private boolean isNetNpUser = false;// 是否携转号码
    
    private String serialNumberA = ""; //宽带对应的手机号码
    
    private String modemFee = "";//光猫押金金额
    
    private String modermReturn = "0"; //是否退光猫  0 不退  1退

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
