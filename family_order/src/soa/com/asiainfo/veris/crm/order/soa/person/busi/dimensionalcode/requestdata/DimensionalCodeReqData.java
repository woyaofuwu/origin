package com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class DimensionalCodeReqData extends BaseReqData {
	private String id_type;
	private String id_value;
	private String opr_code;
	private String opr_time;
	private String channel;
	private String seq;
	private String status;
	private String pkg_seq;
	
	public String getId_type() {
		return id_type;
	}
	public void setId_type(String id_type) {
		this.id_type = id_type;
	}
	public String getId_value() {
		return id_value;
	}
	public void setId_value(String id_value) {
		this.id_value = id_value;
	}
	public String getOpr_code() {
		return opr_code;
	}
	public void setOpr_code(String opr_code) {
		this.opr_code = opr_code;
	}
	public String getOpr_time() {
		return opr_time;
	}
	public void setOpr_time(String opr_time) {
		this.opr_time = opr_time;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPkg_seq() {
		return pkg_seq;
	}
	public void setPkg_seq(String pkg_seq) {
		this.pkg_seq = pkg_seq;
	}
}