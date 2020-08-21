/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardBaseReqData;

public class OneCardNCodesChangeCardReqData extends BaseReqData
{

    private String serialNum;

    private String imsi;

    private String otherSN;

    private String simCardNoM;

    private String simCardNoO;

    private String serialNumO;

    private String imsiO;

    private String ki_a;

    private String ki_b;
    
    private boolean tag;

    private SimCardBaseReqData newSimCardInfo;

    private SimCardBaseReqData oldSimCardInfo;

    private SimCardBaseReqData newSimCardInfoOther;

    
    
    public boolean isTag() {
		return tag;
	}

	public void setTag(boolean tag) {
		this.tag = tag;
	}

	public String getImsi()
    {
        return imsi;
    }

    public String getImsiO()
    {
        return imsiO;
    }

    public String getKi_a()
    {
        return ki_a;
    }

    public String getKi_b()
    {
        return ki_b;
    }

    public SimCardBaseReqData getNewSimCardInfo()
    {
        return newSimCardInfo;
    }

    public SimCardBaseReqData getNewSimCardInfoOther()
    {
        return newSimCardInfoOther;
    }

    public SimCardBaseReqData getOldSimCardInfo()
    {
        return oldSimCardInfo;
    }

    public String getOtherSN()
    {
        return otherSN;
    }

    public String getSerialNum()
    {
        return serialNum;
    }

    public String getSerialNumO()
    {
        return serialNumO;
    }

    public String getSimCardNoM()
    {
        return simCardNoM;
    }

    public String getSimCardNoO()
    {
        return simCardNoO;
    }

    public void setImsi(String imsi)
    {
        this.imsi = imsi;
    }

    public void setImsiO(String imsiO)
    {
        this.imsiO = imsiO;
    }

    public void setKi_a(String ki_a)
    {
        this.ki_a = ki_a;
    }

    public void setKi_b(String ki_b)
    {
        this.ki_b = ki_b;
    }

    public void setNewSimCardInfo(SimCardBaseReqData newSimCardInfo)
    {
        this.newSimCardInfo = newSimCardInfo;
    }

    public void setNewSimCardInfoOther(SimCardBaseReqData newSimCardInfoOther)
    {
        this.newSimCardInfoOther = newSimCardInfoOther;
    }

    public void setOldSimCardInfo(SimCardBaseReqData oldSimCardInfo)
    {
        this.oldSimCardInfo = oldSimCardInfo;
    }

    public void setOtherSN(String otherSN)
    {
        this.otherSN = otherSN;
    }

    public void setSerialNum(String serialNum)
    {
        this.serialNum = serialNum;
    }

    public void setSerialNumO(String serialNumO)
    {
        this.serialNumO = serialNumO;
    }

    public void setSimCardNoM(String simCardNoM)
    {
        this.simCardNoM = simCardNoM;
    }

    public void setSimCardNoO(String simCardNoO)
    {
        this.simCardNoO = simCardNoO;
    }

}
