package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class RelationBankTradeData extends BaseTradeData{

	private String userId;
	private String partitionId;
	private String serialNumber;
	private String subId;
	private String bankId;
	private String userAccount;
	private String payType;
	private String accountCat;
	private String subTime;
	private String rechAmount;
	private String rechThreshold;
	private String modifyTag;
	private String instId;
	private String startDate;
	private String endDate;
	private String rsrvStr1;
	private String rsrvStr2;
	private String rsrvStr3;
	private String rsrvStr4;
	private String rsrvStr5;
	private String payMode;
	private String userType;
	
	public void setUserId(String userId){
		this.userId=userId;
	}
	
	public String getUserId(){
		return this.userId;
	}
	public void setPartitionId(String partitionId){
		this.partitionId=partitionId;
	}
	
	public String getPartitionId(){
		return this.partitionId;
	}
	public void setSerialNumber(String serialNumber){
		this.serialNumber=serialNumber;
	}
	
	public String getSerialNumber(){
		return this.serialNumber;
	}
	public void setSubId(String subId){
		this.subId=subId;
	}
	
	public String getSubId(){
		return this.subId;
	}
	public void setBankId(String bankId){
		this.bankId=bankId;
	}
	
	public String getBankId(){
		return this.bankId;
	}
	public void setUserAccount(String userAccount){
		this.userAccount=userAccount;
	}
	
	public String getUserAccount(){
		return this.userAccount;
	}
	public void setPayType(String payType){
		this.payType=payType;
	}
	
	public String getPayType(){
		return this.payType;
	}
	public void setAccountCat(String accountCat){
		this.accountCat=accountCat;
	}
	
	public String getAccountCat(){
		return this.accountCat;
	}
	public void setSubTime(String subTime){
		this.subTime=subTime;
	}
	
	public String getSubTime(){
		return this.subTime;
	}
	public void setRechAmount(String rechAmount){
		this.rechAmount=rechAmount;
	}
	
	public String getRechAmount(){
		return this.rechAmount;
	}
	public void setRechThreshold(String rechThreshold){
		this.rechThreshold=rechThreshold;
	}
	
	public String getRechThreshold(){
		return this.rechThreshold;
	}
	public void setModifyTag(String modifyTag){
		this.modifyTag=modifyTag;
	}
	
	public String getModifyTag(){
		return this.modifyTag;
	}
	public void setInstId(String instId){
		this.instId=instId;
	}
	
	public String getInstId(){
		return this.instId;
	}
	public void setStartDate(String startDate){
		this.startDate=startDate;
	}
	
	public String getStartDate(){
		return this.startDate;
	}
	public void setEndDate(String endDate){
		this.endDate=endDate;
	}
	
	public String getEndDate(){
		return this.endDate;
	}
	public void setRsrvStr1(String rsrvStr1){
		this.rsrvStr1=rsrvStr1;
	}
	
	public String getRsrvStr1(){
		return this.rsrvStr1;
	}
	public void setRsrvStr2(String rsrvStr2){
		this.rsrvStr2=rsrvStr2;
	}
	
	public String getRsrvStr2(){
		return this.rsrvStr2;
	}
	public void setRsrvStr3(String rsrvStr3){
		this.rsrvStr3=rsrvStr3;
	}
	
	public String getRsrvStr3(){
		return this.rsrvStr3;
	}
	public void setRsrvStr4(String rsrvStr4){
		this.rsrvStr4=rsrvStr4;
	}
	
	public String getRsrvStr4(){
		return this.rsrvStr4;
	}
	public void setRsrvStr5(String rsrvStr5){
		this.rsrvStr5=rsrvStr5;
	}
	
	public String getRsrvStr5(){
		return this.rsrvStr5;
	}
	public void setPayMode(String payMode){
		this.payMode=payMode;
	}
	
	public String getPayMode(){
		return this.payMode;
	}
	public void setUserType(String userType){
		this.userType=userType;
	}
	
	public String getUserType(){
		return this.userType;
	}

	public RelationBankTradeData()
	{
	
	}

	public RelationBankTradeData(IData data)
	{
		this.userId = data.getString("USER_ID");
		this.partitionId = data.getString("PARTITION_ID");
		this.serialNumber = data.getString("SERIAL_NUMBER");
		this.subId = data.getString("SUB_ID");
		this.bankId = data.getString("BANK_ID");
		this.userAccount = data.getString("USER_ACCOUNT");
		this.payType = data.getString("PAY_TYPE");
		this.accountCat = data.getString("ACCOUNT_CAT");
		this.subTime = data.getString("SUB_TIME");
		this.rechAmount = data.getString("RECH_AMOUNT");
		this.rechThreshold = data.getString("RECH_THRESHOLD");
		this.modifyTag = data.getString("MODIFY_TAG");
		this.instId = data.getString("INST_ID");
		this.startDate = data.getString("START_DATE");
		this.endDate = data.getString("END_DATE");
		this.rsrvStr1 = data.getString("RSRV_STR1");
		this.rsrvStr2 = data.getString("RSRV_STR2");
		this.rsrvStr3 = data.getString("RSRV_STR3");
		this.rsrvStr4 = data.getString("RSRV_STR4");
		this.rsrvStr5 = data.getString("RSRV_STR5");
		this.payMode = data.getString("PAY_MODE");
		this.userType = data.getString("USER_TYPE");
	}
	
	public RelationBankTradeData clone()
	{
		RelationBankTradeData relationBankTradeData = new RelationBankTradeData();
		relationBankTradeData.setUserId(this.getUserId());
		relationBankTradeData.setPartitionId(this.getPartitionId());
		relationBankTradeData.setSerialNumber(this.getSerialNumber());
		relationBankTradeData.setSubId(this.getSubId());
		relationBankTradeData.setBankId(this.getBankId());
		relationBankTradeData.setUserAccount(this.getUserAccount());
		relationBankTradeData.setPayType(this.getPayType());
		relationBankTradeData.setAccountCat(this.getAccountCat());
		relationBankTradeData.setSubTime(this.getSubTime());
		relationBankTradeData.setRechAmount(this.getRechAmount());
		relationBankTradeData.setRechThreshold(this.getRechThreshold());
		relationBankTradeData.setModifyTag(this.getModifyTag());
		relationBankTradeData.setInstId(this.getInstId());
		relationBankTradeData.setStartDate(this.getStartDate());
		relationBankTradeData.setEndDate(this.getEndDate());
		relationBankTradeData.setRsrvStr1(this.getRsrvStr1());
		relationBankTradeData.setRsrvStr2(this.getRsrvStr2());
		relationBankTradeData.setRsrvStr3(this.getRsrvStr3());
		relationBankTradeData.setRsrvStr4(this.getRsrvStr4());
		relationBankTradeData.setRsrvStr5(this.getRsrvStr5());
		relationBankTradeData.setPayMode(this.getPayMode());
		relationBankTradeData.setUserType(this.getUserType());
		
		return relationBankTradeData;
	}
	
	public IData toData()
	{
		IData data = new DataMap();
		data.put("USER_ID", this.userId);
		data.put("PARTITION_ID", this.partitionId);
		data.put("SERIAL_NUMBER", this.serialNumber);
		data.put("SUB_ID", this.subId);
		data.put("BANK_ID", this.bankId);
		data.put("USER_ACCOUNT", this.userAccount);
		data.put("PAY_TYPE", this.payType);
		data.put("ACCOUNT_CAT", this.accountCat);
		data.put("SUB_TIME", this.subTime);
		data.put("RECH_AMOUNT", this.rechAmount);
		data.put("RECH_THRESHOLD", this.rechThreshold);
		data.put("MODIFY_TAG", this.modifyTag);
		data.put("INST_ID", this.instId);
		data.put("START_DATE", this.startDate);
		data.put("END_DATE", this.endDate);
		data.put("RSRV_STR1", this.rsrvStr1);
		data.put("RSRV_STR2", this.rsrvStr2);
		data.put("RSRV_STR3", this.rsrvStr3);
		data.put("RSRV_STR4", this.rsrvStr4);
		data.put("RSRV_STR5", this.rsrvStr5);
		data.put("PAY_MODE", this.payMode);
		data.put("USER_TYPE", this.userType);
		
		return data;
	}
	
	public String getTableName()
	{
		return "TF_B_TRADE_RELATION_BANK";
	}
	
	public String toString()
	{
		IData data = new DataMap();
		data.put(getTableName(), this.toData());
		return data.toString();
	}
}