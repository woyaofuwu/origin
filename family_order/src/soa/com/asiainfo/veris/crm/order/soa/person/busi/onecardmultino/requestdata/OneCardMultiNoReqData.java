package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata;


import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class OneCardMultiNoReqData extends BaseReqData {
	private String serialNumber;// 主号码
	private String serial_number_b;// 副号码
	private String orderno;// 副号码序号
	private String flag;// 操作标志
	private String inherit;// 改号 过户时候 是否继承
	private String new_serial_number;//改号时候的新号码
	private String category;//副号码类型
	private String force_object;//短信发送的号码
	
	private String biz_code;
	private String sp_code;
	private String service_id;
	private String serial_type;
	private String effective_time;
	
	private String pictureT;
	private String pictureZ;
	private String pictureF;
	private String address;
	private String synTime;
	private String seqId;
	private String msOpCode;
	
	private String msisdn;
	private String oprCode;
	
	 public String getOprCode() {
		return oprCode;
	}
	public void setOprCode(String oprCode) {
		this.oprCode = oprCode;
	}
	public String getMsisdn() {
			return msisdn;
		}
	 public void setMsisdn(String msisdn) {
			this.msisdn = msisdn;
		}
	
	//和多好需求改造
	private String channel_id;
	
	
	
	public String getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}
	public String getForce_object() {
		return force_object;
	}
	public void setForce_object(String force_object) {
		this.force_object = force_object;
	}
	public String getNew_serial_number() {
		return new_serial_number;
	}
	public void setNew_serial_number(String new_serial_number) {
		this.new_serial_number = new_serial_number;
	}
	public String getSerial_number_b() {
		return serial_number_b;
	}
	public void setSerial_number_b(String serial_number_b) {
		this.serial_number_b = serial_number_b;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getInherit() {
		return inherit;
	}
	public void setInherit(String inherit) {
		this.inherit = inherit;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getService_id() {
		return service_id;
	}
	public void setService_id(String service_id) {
		this.service_id = service_id;
	}
	public String getBiz_Code() {
		return biz_code;
	}
	public void setBIZ_CODE(String biz_code) {
		this.biz_code = biz_code;
	}	
	public String getSp_code() {
		return sp_code;
	}
	public void setSp_code(String sp_code) {
		this.sp_code = sp_code;
	}
	public String getSerial_type() {
		return serial_type;
	}
	public void setSerial_type(String serial_type) {
		this.serial_type = serial_type;
	}
	public String getEffective_time() {
		return effective_time;
	}
	public void setEffective_time(String effective_time) {
		this.effective_time = effective_time;
	}
	public String getPictureT()
	{
		return pictureT;
	}
	public void setPictureT(String pictureT)
	{
		this.pictureT = pictureT;
	}
	public String getPictureZ()
	{
		return pictureZ;
	}
	public void setPictureZ(String pictureZ)
	{
		this.pictureZ = pictureZ;
	}
	public String getPictureF()
	{
		return pictureF;
	}
	public void setPictureF(String pictureF)
	{
		this.pictureF = pictureF;
	}
	public String getAddress()
	{
		return address;
	}
	public void setAddress(String address)
	{
		this.address = address;
	}
	public String getSynTime()
	{
		return synTime;
	}
	public void setSynTime(String synTime)
	{
		this.synTime = synTime;
	}
	public String getSeqId()
	{
		return seqId;
	}
	public void setSeqId(String seqId)
	{
		this.seqId = seqId;
	}
	public String getMsOpCode()
	{
		return msOpCode;
	}
	public void setMsOpCode(String msOpCode)
	{
		this.msOpCode = msOpCode;
	}
	
}