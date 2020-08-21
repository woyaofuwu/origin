package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class ViceRealInfoReRegSVC extends CSBizService {

	private static final long serialVersionUID = -963591590107053592L;
	
	public IData loadViceRealInfo(IData input)throws Exception
	{
		String sn = input.getString("SERIAL_NUMBER");
    	UcaData uca = UcaDataFactory.getNormalUca(sn);
    	if(!"MOSP".equals(uca.getBrandCode()))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该业务只允许和多号虚拟副号码办理！");// 副卡的短号不能为空
    	}
    	ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);
    	
    	IDataset datas = bean.qryHdhSynInfo(input);
    	if(IDataUtil.isEmpty(datas))
    	{
    		 CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到该号码的同步信息，业务无法继续！");
    	}		
		return datas.getData(0);
	}
	
	public IDataset tradeReg(IData input)throws Exception
	{
		IDataset results = new DatasetList();
		String sn = input.getString("SERIAL_NUMBER");
		UcaData uca = UcaDataFactory.getNormalUca(sn);
		
		ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);
		IDataset datas = bean.qryHdhSynInfo(input);
    	if(IDataUtil.isEmpty(datas))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到该号码的同步信息，业务无法继续！");
    	}
    	
    	String asynCustName = datas.getData(0).getString("CUST_NAME");
    	String asynPsptType = datas.getData(0).getString("PSPT_TYPE_CODE");
    	String asynPsptId = datas.getData(0).getString("PSPT_ID");
    	String asynSnA = datas.getData(0).getString("SERIAL_NUMBER");
    	
    	String chgPsptAddr = input.getString("PSPT_ADDR");
    	String chgPsptTypeCode = input.getString("PSPT_TYPE_CODE");
    	String chgCustName = input.getString("CUST_NAME");
    	String chgPsptId = input.getString("PSPT_ID");
		
		if(!StringUtils.equals(asynCustName, chgCustName)
				||!StringUtils.equals(asynPsptType, chgPsptTypeCode)
				||!StringUtils.equals(asynPsptId, chgPsptId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"要补录号码的客户名称，证件类型，证件号码与同步的信息不一致！");
		}
		IData result = new DataMap();
		result.put("SERIAL_NUMBER", sn);
		results.add(result);
		//目前貌似也只能改证件地址
		if(!StringUtils.equals(chgPsptAddr, uca.getCustPerson().getPsptAddr()))
		{
			IData custInfoCharge = new DataMap();
			custInfoCharge.put("SERIAL_NUMBER", sn);
				
			custInfoCharge.putAll(uca.getCustPerson().toData());
			custInfoCharge.putAll(uca.getCustomer().toData());
			custInfoCharge.put("PSPT_ADDR", chgPsptAddr);
			custInfoCharge.put("PHONE", input.getString("PHONE"));
			IDataset chgResults = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", custInfoCharge);
			results.addAll(chgResults);
		}
		
		String picNameT = "undefined";
		String picNameZ = "undefined";
		String picNameF = "undefined";
		
		IData chgAsynInfo = new DataMap();
		chgAsynInfo.put("ADDRESS", chgPsptAddr);
		chgAsynInfo.put("PIC_NAMET", picNameT);
		chgAsynInfo.put("PIC_NAMEZ", picNameZ);
		chgAsynInfo.put("PIC_NAMEF", picNameF);
		chgAsynInfo.put("SERIAL_NUMBER", asynSnA);
		chgAsynInfo.put("SERIAL_NUMBER_B", sn);
		chgAsynInfo.put("UPDATE_STAFF_ID",getVisit().getStaffId());
		chgAsynInfo.put("UPDATE_DEPART_ID", getVisit().getDepartId());
		
		bean.updateViceAsynInfo(chgAsynInfo);
		
		return results;
	}
}
