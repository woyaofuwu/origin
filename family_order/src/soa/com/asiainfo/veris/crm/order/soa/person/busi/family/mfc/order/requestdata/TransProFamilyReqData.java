package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.requestdata;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class TransProFamilyReqData extends BaseReqData 
{
	//公用参数
	private String poOrderNumber;		//业务订单号
	private String productCode;			//产品编码
	private String productOfferingID;	//业务订购实例ID
	private String customerPhone;		//主号号码
	private String companyID;			//订单来源省编码
	private String orderType;			//订单类型
	private String bizVersion;			//主号号码
	private String operType;			//主号号码
	//建家后存在
	private String userIdA;				//虚拟家庭ID
	private String serialNumberA;		//虚拟家庭号码
	private String isFull;				//删除所有成员信息,0:是,1:否
	
	//3.1接口其他入参
	private String orderSourceID;		//订单来源
	private String operationSubTypeID;	//操作类型
	private String customerType;		//主号类型
	private String finishTime;			//归档时间
	private String effTime;				//生效时间
	private String expTime;				//失效时间	
	//3.3接口其他入参
	private String businessType;		//业务类型
	private String orderSource;			//订单来源
	private String action;				//操作类型
	private IDataset productOrderMember;//本次交易的成员列表
	private String isSendType;			//是否发送短信标记
	
	public String getIsSendType() {
		return isSendType;
	}
	public void setIsSendType(String isSendType) {
		this.isSendType = isSendType;
	}
	public String getPoOrderNumber()
	{
		return poOrderNumber;
	}
	public void setPoOrderNumber(String poOrderNumber)
	{
		this.poOrderNumber = poOrderNumber;
	}
	public String getProductCode()
	{
		return productCode;
	}
	public void setProductCode(String productCode)
	{
		this.productCode = productCode;
	}
	public String getProductOfferingID() 
	{
		return productOfferingID;
	}
	public void setProductOfferingID(String productOfferingID) 
	{
		this.productOfferingID = productOfferingID;
	}
	public String getCustomerPhone() 
	{
		return customerPhone;
	}
	public void setCustomerPhone(String customerPhone) 
	{
		this.customerPhone = customerPhone;
	}
	public String getCompanyID() 
	{
		return companyID;
	}
	public void setCompanyID(String companyID) 
	{
		this.companyID = companyID;
	}
	public String getOrderType() 
	{
		return orderType;
	}
	public void setOrderType(String orderType) 
	{
		this.orderType = orderType;
	}
	public String getBizVersion() 
	{
		return bizVersion;
	}
	public void setBizVersion(String bizVersion)
	{
		this.bizVersion = bizVersion;
	}
	public String getOperType() 
	{
		return operType;
	}
	public void setOperType(String operType) 
	{
		this.operType = operType;
	}
	public String getUserIdA()
	{
		return userIdA;
	}
	public void setUserIdA(String userIdA) 
	{
		this.userIdA = userIdA;
	}
	public String getSerialNumberA() 
	{
		return serialNumberA;
	}
	public void setSerialNumberA(String serialNumberA) 
	{
		this.serialNumberA = serialNumberA;
	}
	public String getIsFull() 
	{
		return isFull;
	}
	public void setIsFull(String isFull)
	{
		this.isFull = isFull;
	}
	public String getOrderSourceID() 
	{
		return orderSourceID;
	}
	public void setOrderSourceID(String orderSourceID) 
	{
		this.orderSourceID = orderSourceID;
	}
	public String getOperationSubTypeID() 
	{
		return operationSubTypeID;
	}
	public void setOperationSubTypeID(String operationSubTypeID)
	{
		this.operationSubTypeID = operationSubTypeID;
	}
	public String getCustomerType() 
	{
		return customerType;
	}
	public void setCustomerType(String customerType) 
	{
		this.customerType = customerType;
	}
	public String getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(String finishTime) 
	{
		this.finishTime = finishTime;
	}
	public String getEffTime() 
	{
		return effTime;
	}
	public void setEffTime(String effTime) 
	{
		this.effTime = effTime;
	}
	public String getExpTime()
	{
		return expTime;
	}
	public void setExpTime(String expTime)
	{
		this.expTime = expTime;
	}
	public String getBusinessType() 
	{
		return businessType;
	}
	public void setBusinessType(String businessType) 
	{
		this.businessType = businessType;
	}
	public String getOrderSource()
	{
		return orderSource;
	}
	public void setOrderSource(String orderSource)
	{
		this.orderSource = orderSource;
	}
	public String getAction()
	{
		return action;
	}
	public void setAction(String action)
	{
		this.action = action;
	}
	public IDataset getProductOrderMember()
	{
		return productOrderMember;
	}
	public void setProductOrderMember(IDataset productOrderMember)
	{
		this.productOrderMember = productOrderMember;
	}
	
	
}
