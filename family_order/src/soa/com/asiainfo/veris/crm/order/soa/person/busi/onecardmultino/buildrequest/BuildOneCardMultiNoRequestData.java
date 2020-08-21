package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.buildrequest;



import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata.OneCardMultiNoReqData;


public class BuildOneCardMultiNoRequestData extends BaseBuilder implements IBuilder {
	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) brd;
		// 获取所传的参数
		oneCardMultiNoReqData.setRemark(param.getString("REMARK","一卡多号业务"));
		oneCardMultiNoReqData.setSerialNumber(param.getString("SERIAL_NUMBER"));
		oneCardMultiNoReqData.setSerial_number_b(param.getString("SERIAL_NUMBER_B"));
		oneCardMultiNoReqData.setOrderno(param.getString("ORDERNO",""));
		oneCardMultiNoReqData.setFlag(param.getString("FLAG"));
		oneCardMultiNoReqData.setInherit(param.getString("INHERIT"));
		oneCardMultiNoReqData.setNew_serial_number(param.getString("NEW_SERIAL_NUMBER"));
		oneCardMultiNoReqData.setCategory(param.getString("CATEGORY","0"));
		oneCardMultiNoReqData.setForce_object(param.getString("FORCE_OBJECT"));
		oneCardMultiNoReqData.setBIZ_CODE(param.getString("BIZ_CODE",""));
		oneCardMultiNoReqData.setSp_code(param.getString("SPID",""));
		oneCardMultiNoReqData.setSerial_type(param.getString("SERIAL_TYPE",""));
		oneCardMultiNoReqData.setService_id(param.getString("SERVICE_ID",""));
		oneCardMultiNoReqData.setEffective_time(param.getString("EFFECTIVE_TIME",""));
	
		oneCardMultiNoReqData.setPictureT(param.getString("PIC_NAMET", ""));
		oneCardMultiNoReqData.setPictureZ(param.getString("PIC_NAMEZ", ""));
		oneCardMultiNoReqData.setPictureF(param.getString("PIC_NAMEF", ""));
		oneCardMultiNoReqData.setSeqId(param.getString("SEQ", ""));
		oneCardMultiNoReqData.setAddress(param.getString("ADDRESS", ""));
		oneCardMultiNoReqData.setMsOpCode(param.getString("MS_OPCODE", ""));
		oneCardMultiNoReqData.setChannel_id(param.getString("CHANNEL_ID",""));
		oneCardMultiNoReqData.setMsisdn(param.getString("MSISDN",""));
		oneCardMultiNoReqData.setOprCode(param.getString("OPR_CODE",""));
		
	}
	@Override
	public BaseReqData getBlankRequestDataInstance() {
		return new OneCardMultiNoReqData();
	}
}