
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.common.data.IData;


/**
 * @author Administrator
 */
public class GBlackWhiteData 
{
	private String prodId;//服务ID
	
	private String interfaceMode;// 0-实时接口 1-文件接口 td_b_attr_biz 默认取0
	
	private String operCode;//操作类型
	
	private String platSyncState;
	
	private String pfMode;// 标识是否走服务开通 0 正常走服务开通模式 1 ADC平台 2 行业网关
	
	private String expectTime;//希望生效时间
	
	private String userTypeCode;//用户类型
	
	private String serialNumber;//服务号码
	
	private String bizCode;//业务代码
	
	private String validDate;
	
	private String expireDate;
	
	private String cancleFlag;
	
	private String instId;//mebplatsvc的实例id
	
	private String operState;
	
	public void setProdId(String prodId){
		
		this.prodId = prodId;
	}
	
	public String getProdId(){
		
		return prodId;
	}
	
	public void setInterfaceMode(String interfaceMode){
		
		this.interfaceMode = interfaceMode;
	}
	
	public String getInterfaceMode(){
		
		return interfaceMode;
	}
	
	public void setOperCode(String operCode){
		
		this.operCode = operCode;
	}
	
	public String getOperCode(){
		
		return operCode;
	}
	
	public void setPlatSyncState(String platSyncState){
		
		this.platSyncState = platSyncState;
	}
	
	public String getPlatSyncState(){
		
		return platSyncState;
	}
	
	public void setPfMode(String pfMode){
		
		this.pfMode = pfMode;
	}
	
	public String getPfMode(){
		
		return pfMode;
	}
	
	public void setExpectTime(String expectTime){
		
		this.expectTime = expectTime;
	}
	
	public String getExpectTime(){
		
		return expectTime;
	}
	
	public String getUserTypeCode()
    {
        return userTypeCode;
    }

    public void setUserTypeCode(String userTypeCode)
    {
        this.userTypeCode = userTypeCode;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public String getBizCode()
    {
        return bizCode;
    }

    public void setBizCode(String bizCode)
    {
        this.bizCode = bizCode;
    }

    public String getValidDate() {
		return validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public String getCancleFlag() {
		return cancleFlag;
	}

	public void setCancleFlag(String cancleFlag) {
		this.cancleFlag = cancleFlag;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getOperState() {
		return operState;
	}

	public void setOperState(String operState) {
		this.operState = operState;
	}

	public static GBlackWhiteData getInstance(IData bwData){
		
		if(bwData == null)
			return null;
		GBlackWhiteData bw = new GBlackWhiteData();
		
		bw.setExpectTime(bwData.getString("EXPECT_TIME"));
		bw.setPfMode(bwData.getString("RSRV_TAG3"));
		bw.setPlatSyncState(bwData.getString("PLAT_SYNC_STATE"));
		bw.setInterfaceMode(bwData.getString("RSRV_TAG2"));
		bw.setProdId(bwData.getString("SERVICE_ID"));
		bw.setUserTypeCode(bwData.getString("USER_TYPE_CODE"));
		bw.setBizCode(bwData.getString("BIZ_CODE"));
		bw.setSerialNumber(bwData.getString("SERIAL_NUMBER"));
		String operCode = bwData.getString("OPER_CODE");
		if(StringUtils.isEmpty(operCode))
			operCode = bwData.getString("MODIFY_TAG");
		bw.setOperCode(operCode);
		bw.setValidDate(bwData.getString("START_DATE"));
		bw.setExpireDate(bwData.getString("END_DATE"));
		bw.setCancleFlag(bwData.getString("CANCLE_FLAG"));
		bw.setInstId(bwData.getString("INST_ID"));
		bw.setOperState(bwData.getString("OPER_STATE"));
        return bw;
	}
}
