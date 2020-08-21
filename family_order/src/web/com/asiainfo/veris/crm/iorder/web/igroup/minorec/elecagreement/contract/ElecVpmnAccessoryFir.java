package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.contract;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.base.ElecLineProtocolBase;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import org.apache.tapestry.IRequestCycle;


public abstract class ElecVpmnAccessoryFir extends ElecLineProtocolBase {

	private final static String B_NAME = "中国移动通信集团海南有限公司";

	protected void initalPageValue(IData protocolInfo, IData accessoryInfo, IData pageData) throws Exception{
		if("N".equals(pageData.getString("SUBMITTYPE"))){
			IData groupInfo = protocolInfo.getData("CUST_INFO",new DataMap());
			IData managerInfo = protocolInfo.getData("CUST_MRG_INFO",new DataMap());

			String sysDate =SysDateMgr4Web.getSysDate("yyyy年MM月dd日");
			accessoryInfo.put("A_SIGN_DATE", sysDate);
			accessoryInfo.put("B_SIGN_DATE", sysDate);
			accessoryInfo.put("GROUP_NAME",groupInfo.getString("CUST_NAME"));
			accessoryInfo.put("A_NAME",groupInfo.getString("CUST_NAME"));
			accessoryInfo.put("B_NAME",B_NAME);
			accessoryInfo.put("GROUP_ID",groupInfo.getString("GROUP_ID"));
			accessoryInfo.put("ADDRESS",groupInfo.getString("GROUP_ADDR"));
			accessoryInfo.put("POSTAL_CODE",groupInfo.getString("POST_CODE"));

		}
	}

	protected IData buildFDFInfo(IData pageData) throws Exception{
		IData contextData = buildProtocolInfo(pageData);
		return contextData;
	}

	protected IData buildProtocolInfo(IData pageData){
		IData archivesInfo = new DataMap();
		IDataset archiveslist = new DatasetList();

		archivesInfo.put("AGREEMENT_ID",pageData.getString("AGREEMENT_ID"));

		ElecLineUtil.setPDFField(pageData.getInt("BUSI_TYPE",0)-1, archiveslist,"BUSI_TYPE_OPEN","BUSI_TYPE_CHANGE","BUSI_TYPE_CANCEL");

		archivesInfo.put("GROUP_NAME",pageData.getString("GROUP_NAME",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("GROUP_ID",pageData.getString("GROUP_ID",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("ADDRESS",pageData.getString("ADDRESS",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("POSTAL_CODE",pageData.getString("POSTAL_CODE",ElecLineUtil.DUFUALT_VALUE));

		ElecLineUtil.setPDFField(pageData.getInt("CERTIFICATE",0)-1, archiveslist,"CERTIFICATE_BUSINESS","CERTIFICATE_OTHER");

		archivesInfo.put("CERTIFICATE_OTHER_NAME",pageData.getString("CERTIFICATE_OTHER_NAME",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("CERTIFICATE_NO",pageData.getString("CERTIFICATE_NO",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("CONTRACT_PERSON",pageData.getString("CONTRACT_PERSON",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("CONTRACT_PHONE",pageData.getString("CONTRACT_PHONE",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_HEADER",pageData.getString("A_HEADER",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_HEADER_PHONE",pageData.getString("A_HEADER_PHONE",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_AGENT",pageData.getString("A_AGENT",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("AGENT_CERTIFICATE",pageData.getString("AGENT_CERTIFICATE",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("AGENT_CERTIFICATE_NO",pageData.getString("AGENT_CERTIFICATE_NO",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("DISCOUNT_REMARK",pageData.getString("DISCOUNT_REMARK",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("DISCOUNT",pageData.getString("DISCOUNT",ElecLineUtil.DUFUALT_VALUE));

		ElecLineUtil.setPDFField(pageData.getInt("PAY_TYPE",0)-1, archiveslist,"PAY_TYPE_PERSON","PAY_TYPE_GROUP");
		ElecLineUtil.setPDFField(pageData.getInt("PAY_METHOD",0)-1, archiveslist,"PAY_METHOD_CASH","PAY_METHOD_CREDIT","PAY_METHOD_BANK");

		archivesInfo.put("PAY_NO",pageData.getString("PAY_NO",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_BANK_ACCT_NAME",pageData.getString("A_BANK_ACCT_NAME",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_BANK",pageData.getString("A_BANK",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_BANK_ACCT_NO",pageData.getString("A_BANK_ACCT_NO",ElecLineUtil.DUFUALT_VALUE));

		ElecLineUtil.setPDFField(pageData.getInt("REMBURSEMENT",0)-1, archiveslist,"REMBURSEMENT_COMPLETE","REMBURSEMENT_QUOTA");

		archivesInfo.put("PAY_REMARK",pageData.getString("PAY_REMARK",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_SIGN_DATE",pageData.getString("A_SIGN_DATE",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("B_SIGN_DATE",pageData.getString("B_SIGN_DATE",ElecLineUtil.DUFUALT_VALUE));

		ElecLineUtil.addMapKeyToList(archivesInfo, archiveslist);

		IData datas = new DataMap();
		datas.put("DATAS", archiveslist);
		return datas;
	}

	public IData saveProtocolInfo(IData pageData) throws Exception {
		// TODO Auto-generated method stub
		IData archives = new DataMap();

		ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

		IData archivesInfo = ElecLineUtil.buildSaveAccessContextInfo(pageData);
		archives.put("ARCHIVES_INFO", archivesInfo);

		IDataset archivesAttrs = buildSaveAccessoryInfo(pageData);
		archives.put("ARCHIVES_ATTRS", archivesAttrs);

		IDataset products = new DatasetList();
		products.add(pageData.getString("PRODUCT_ID"));
		archives.put("PRODUCTS", products);

		//协议状态
		archives.put("ARCHIVES_STATE", "0");//待审核

		return archives;
	}


	public IData updateProtocolInfo(IData pageData) throws Exception {
		// TODO Auto-generated method stub
		IData archives = new DataMap();
		archives.put("ARCHIVES_ID", pageData.getString("ARCHIVES_ID"));

		ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

		IData archivesInfo = ElecLineUtil.buildSaveAccessContextInfo(pageData);
		archives.put("ARCHIVES_INFO", archivesInfo);

		IDataset archivesAttrs = buildSaveAccessoryInfo(pageData);
		archives.put("ARCHIVES_ATTRS", archivesAttrs);

		IDataset products = new DatasetList();
		products.add(pageData.getString("PRODUCT_ID"));
		archives.put("PRODUCTS", products);

		//协议状态
		archives.put("ARCHIVES_STATE", "0");//待审核

		return archives;
	}

	@Override
	public void onSubmit(IRequestCycle cycle) throws Exception {

		//查询是否有主体协议
		IData pageData = getData();
		String agreementId = pageData.getString("AGREEMENT_ID");

		IData input = new DataMap();
		input.put("AGREEMENT_ID", agreementId);
		IDataset agcontentDatas = CSViewCall.call(this, "SS.AgreementInfoSVC.queryAgArchivesByAgreementId", input);

		if(DataUtils.isEmpty(agcontentDatas)){
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"请先添加主体协议！");
		}

		super.onSubmit(cycle);
	}

	protected void setBackValue(IData resultData, IData pageData) throws Exception {
		resultData.put("ARCHIVES_NAME", pageData.getString("ARCHIVES_NAME"));
		resultData.put("PRODUCT_ID", pageData.getString("PRODUCT_ID"));
		ElecLineUtil.setBackValue(resultData,pageData);
	}

	private IDataset buildSaveAccessoryInfo(IData pageData) throws Exception {
		IData archivesAttrs = new DataMap();
		IDataset archivesAttrsSet = new DatasetList();
		archivesAttrsSet.add(archivesAttrs);
		//协议部分字段
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "AGREEMENT_DEF_ID", "AGREEMENT_DEF_ID","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "CUST_ID", "CUST_ID","");

		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "BUSI_TYPE", "BUSI_TYPE","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "GROUP_NAME", "GROUP_NAME","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "GROUP_ID", "GROUP_ID","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "ADDRESS", "ADDRESS","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "POSTAL_CODE", "POSTAL_CODE","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "CERTIFICATE", "CERTIFICATE","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "CERTIFICATE_OTHER_NAME", "CERTIFICATE_OTHER_NAME","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "CERTIFICATE_NO", "CERTIFICATE_NO","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "CONTRACT_PERSON", "CONTRACT_PERSON","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "CONTRACT_PHONE", "CONTRACT_PHONE","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "A_HEADER", "A_HEADER","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "A_HEADER_PHONE", "A_HEADER_PHONE","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "A_AGENT", "A_AGENT","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "AGENT_CERTIFICATE", "AGENT_CERTIFICATE","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "AGENT_CERTIFICATE_NO", "AGENT_CERTIFICATE_NO","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "DISCOUNT_REMARK", "DISCOUNT_REMARK","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "DISCOUNT", "DISCOUNT","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "PAY_TYPE", "PAY_TYPE","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "PAY_METHOD", "PAY_METHOD","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "PAY_NO", "PAY_NO","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "A_BANK_ACCT_NAME", "A_BANK_ACCT_NAME","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "A_BANK", "A_BANK","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "A_BANK_ACCT_NO", "A_BANK_ACCT_NO","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "REMBURSEMENT", "REMBURSEMENT","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "PAY_REMARK", "PAY_REMARK","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "A_SIGN_DATE", "A_SIGN_DATE","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "B_SIGN_DATE", "B_SIGN_DATE","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "A_NAME", "A_NAME","");
		ElecLineUtil.addIfNotEmpty(archivesAttrs, pageData, "B_NAME", "B_NAME","");

		return archivesAttrsSet;
	}

}
