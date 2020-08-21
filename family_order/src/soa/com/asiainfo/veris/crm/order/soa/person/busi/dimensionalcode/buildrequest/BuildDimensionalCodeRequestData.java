package com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode.DimensionalCodeBean;
import com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode.requestdata.DimensionalCodeReqData;

public class BuildDimensionalCodeRequestData extends BaseBuilder implements  IBuilder {
	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {
		DimensionalCodeReqData dimensionalCodeReqData = (DimensionalCodeReqData) brd;
		// 获取所传的参数
		String remark=param.getString("REMARK");
		if(null==remark||"".equals(remark)) {
			remark="二维码变更操作：操作码："+param.getString("OPR_CODE");;
		}
		dimensionalCodeReqData.setRemark(remark);
		dimensionalCodeReqData.setId_type(param.getString("ID_TYPE","01"));
		String id_value = param.getString("ID_VALUE");
		if(null==id_value||"".equals(id_value)){
			id_value=param.getString("SERIAL_NUMBER");
		}
		dimensionalCodeReqData.setId_value(id_value);
		dimensionalCodeReqData.setOpr_code(param.getString("OPR_CODE"));
		dimensionalCodeReqData.setOpr_time(param.getString("OPR_TIME",SysDateMgr.getSysDate("yyyyMMddHHmmss")));
		dimensionalCodeReqData.setChannel(param.getString("CHANNEL","07")); // 02-网上营业厅， 07-10086语音，08-营业厅
		DimensionalCodeBean bean = (DimensionalCodeBean) BeanManager.createBean(DimensionalCodeBean.class);
		dimensionalCodeReqData.setSeq(param.getString("SEQ",bean.getSeqID("BIP2B334")));
		dimensionalCodeReqData.setStatus(param.getString("STATUS"));
		dimensionalCodeReqData.setPkg_seq(param.getString("PKG_SEQ",bean.getSeqID("BIP2B334")));
	}
	@Override
	public BaseReqData getBlankRequestDataInstance() {
		return new DimensionalCodeReqData();
	}
}