package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class DredgeSmartNetworkReqData extends BaseReqData{
	
	 private String fiistType = "";// 第一类“和优家”

	 private String secondType = "";// 第二类“新增介入点”

	 private String thirdType = "";// 第三类“宽带装维延伸服务”
	 

	public String getFiistType() {
		return fiistType;
	}

	public void setFiistType(String fiistType) {
		this.fiistType = fiistType;
	}

	public String getSecondType() {
		return secondType;
	}

	public void setSecondType(String secondType) {
		this.secondType = secondType;
	}

	public String getThirdType() {
		return thirdType;
	}

	public void setThirdType(String thirdType) {
		this.thirdType = thirdType;
	}
	
	//guozhao
	private String cust_name;//客户名称

    private String contact_phone;//联系人电话
    
    private String city_code;//城市名称
    
    private String city;//城市名称
    
    private String county_code;//区/县
    
	private String residential_zone;//小区名称
    
    private String reserve_date;//期望上门时间
    
    private String address;//详细地址
    
    private String house_type;//户型描述
    
    private String house_type_code;//户型描述

    
    private String area_size;//房屋面积
    
    private String recommend_num;//安装人员联系方式
    
    private String channal;//操作渠道来源
    
    private String oprNumb;//发起方操作流水

    private String SPID;//SP企业代码
    private String bizCode;//业务代码
    private String campaign_id;//营销案ID
    private String bizVersion;//业务版本号
    private String frameNetType;//组网业务类型 

    private String appOrderId;//和家亲app侧生成的订单编号
    private String bizType;
    
    
	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getAppOrderId() {
		return appOrderId;
	}

	public void setAppOrderId(String appOrderId) {
		this.appOrderId = appOrderId;
	}

	public String getFrameNetType() {
		return frameNetType;
	}

	public void setFrameNetType(String frameNetType) {
		this.frameNetType = frameNetType;
	}

	public String getHouse_type_code() {
		return house_type_code;
	}

	public void setHouse_type_code(String house_type_code) {
		this.house_type_code = house_type_code;
	}

	public String getSPID() {
		return SPID;
	}

	public void setSPID(String sPID) {
		SPID = sPID;
	}

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	public String getCampaign_id() {
		return campaign_id;
	}

	public void setCampaign_id(String campaign_id) {
		this.campaign_id = campaign_id;
	}

	public String getBizVersion() {
		return bizVersion;
	}

	public void setBizVersion(String bizVersion) {
		this.bizVersion = bizVersion;
	}

	public String getOprNumb() {
		return oprNumb;
	}

	public void setOprNumb(String oprNumb) {
		this.oprNumb = oprNumb;
	}

	public String getChannal() {
		return channal;
	}

	public void setChannal(String channal) {
		this.channal = channal;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public String getContact_phone() {
		return contact_phone;
	}

	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty_code() {
		return county_code;
	}

	public void setCounty_code(String county_code) {
		this.county_code = county_code;
	}

	public String getResidential_zone() {
		return residential_zone;
	}

	public void setResidential_zone(String residential_zone) {
		this.residential_zone = residential_zone;
	}

	public String getReserve_date() {
		return reserve_date;
	}

	public void setReserve_date(String reserve_date) {
		this.reserve_date = reserve_date;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHouse_type() {
		return house_type;
	}

	public void setHouse_type(String house_type) {
		this.house_type = house_type;
	}

	public String getArea_size() {
		return area_size;
	}

	public void setArea_size(String area_size) {
		this.area_size = area_size;
	}

	public String getRecommend_num() {
		return recommend_num;
	}

	public void setRecommend_num(String recommend_num) {
		this.recommend_num = recommend_num;
	}

}
