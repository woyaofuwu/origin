
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata;
 
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class SimCardReqData extends BaseReqData
{
    private String openMobileTag;

    private String mPayTag;

    private String isScore;
    
    private String simNoOccupyTag;//SIM卡是否需要提前占用标志，默认占用

    private SimCardBaseReqData newSimCardInfo;

    private SimCardBaseReqData oldSimCardInfo; 
    /**REQ201610200008 补换卡业务调整需求 20161102 CHENXY3*/
    private String remotecardType;//补换卡类型 1=换卡 0=补卡
    
    private String oldEid;
    
    private String newEid;
    
    private String ospOrderId;



    private String primarySerialNumber = ""; //补换eSIM时,iphone手机号码

    public String getPrimarySerialNumber() {
        return primarySerialNumber;
    }

    public void setPrimarySerialNumber(String primarySerialNumber) {
        this.primarySerialNumber = primarySerialNumber;
    }

    private String newImei = "";

    public String getNewImei() {
        return newImei;
    }

    public void setNewImei(String newImei) {
        this.newImei = newImei;
    }
    
    public String getOldEid() {
		return oldEid;
	}
	public void setOldEid(String oldEid) {
		this.oldEid = oldEid;
	}
	public String getNewEid() {
		return newEid;
	}
	public void setNewEid(String newEid) {
		this.newEid = newEid;
	}
	public String getOspOrderId() {
		return ospOrderId;
	}
	public void setOspOrderId(String ospOrderId) {
		this.ospOrderId = ospOrderId;
	}
	public String getRemotecardType()
    {
        return remotecardType;
    }
    public void setRemotecardType(String remotecardType)
    {
        this.remotecardType = remotecardType;
    } 
   
    public String getSimNoOccupyTag() {
        return simNoOccupyTag;
    } 

	public void setSimNoOccupyTag(String simNoOccupyTag) {
        this.simNoOccupyTag = simNoOccupyTag;
    }

    public String getIsScore()
    {
        return isScore;
    }

    public String getmPayTag()
    {
        return mPayTag;
    }

    public SimCardBaseReqData getNewSimCardInfo()
    {
        return newSimCardInfo;
    }

    public SimCardBaseReqData getOldSimCardInfo()
    {
        return oldSimCardInfo;
    }

    public String getOpenMobileTag()
    {
        return openMobileTag;
    }

    public void setIsScore(String isScore)
    {
        this.isScore = isScore;
    }

    public void setmPayTag(String mPayTag)
    {
        this.mPayTag = mPayTag;
    }

    public void setNewSimCardInfo(SimCardBaseReqData newSimCardInfo)
    {
        this.newSimCardInfo = newSimCardInfo;
    }

    public void setOldSimCardInfo(SimCardBaseReqData oldSimCardInfo)
    {
        this.oldSimCardInfo = oldSimCardInfo;
    }

    public void setOpenMobileTag(String openMobileTag)
    {
        this.openMobileTag = openMobileTag;
    }
}
