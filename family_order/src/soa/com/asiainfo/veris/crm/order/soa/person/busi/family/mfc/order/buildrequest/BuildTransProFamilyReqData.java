package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.requestdata.TransProFamilyReqData;

public class BuildTransProFamilyReqData extends BaseBuilder implements IBuilder 
{

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
	{
		TransProFamilyReqData reqData = (TransProFamilyReqData) brd;
		reqData.setPoOrderNumber(param.getString("PO_ORDER_NUMBER"));
		reqData.setProductCode(param.getString("PRODUCT_CODE"));
		reqData.setProductOfferingID(param.getString("PRODUCT_OFFERING_ID"));
		reqData.setCustomerPhone(param.getString("CUSTOMER_PHONE"));
		reqData.setCompanyID(param.getString("COMPANY_ID"));
		reqData.setOrderType(param.getString("ORDER_TYPE"));
		reqData.setBizVersion(param.getString("BIZ_VERSION"));
		reqData.setOperType(param.getString("OPER_TYPE"));
		//虚拟家庭信息
		reqData.setUserIdA(param.getString("USER_ID_A",""));
		reqData.setSerialNumberA(param.getString("SERIAL_NUMBER_A",""));
		reqData.setIsFull(param.getString("IS_FULL","1"));
		//3.1必填入参
		reqData.setOrderSourceID(param.getString("ORDER_SOURCE_ID",""));
		reqData.setOperationSubTypeID(param.getString("OPERATION_SUBTYPE_ID",""));
		reqData.setCustomerType(param.getString("CUSTOMER_TYPE",""));
		reqData.setFinishTime(param.getString("FINISH_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS()));
		reqData.setEffTime(param.getString("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS()));
		reqData.setExpTime(param.getString("EXP_TIME","20991231235959"));
		//3.3必填入参
		reqData.setBusinessType(param.getString("BUSINESS_TYPE","1"));
		reqData.setOrderSource(param.getString("ORDER_SOURCE",""));
		reqData.setAction(param.getString("ACTION",""));
		reqData.setIsSendType(param.getString("IS_SEND_TYPE","0"));//0-发送，1-不发送

		IDataset productmember = param.getDataset("PRODUCT_ORDER_MEMBER");
		if(DataUtils.isNotEmpty(productmember))
		{
			reqData.setProductOrderMember(productmember);
		}
	}

	@Override
	public BaseReqData getBlankRequestDataInstance()
	{
		// TODO Auto-generated method stub
		return new TransProFamilyReqData();
	}

}
