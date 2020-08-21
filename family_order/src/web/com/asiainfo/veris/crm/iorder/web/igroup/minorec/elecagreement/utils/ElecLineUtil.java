package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils;


import com.ailk.biz.BizVisit;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Set;

public class ElecLineUtil {

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

	public static final String DUFUALT_VALUE = "\\";


	public static void setBackValue(IData resultData, IData pageData) throws Exception {
		resultData.put("CONTRACT_WRITE_DATE",pageData.getString("CONTRACT_START_DATE",SysDateMgr4Web.getSysDate(SysDateMgr4Web.PATTERN_STAND)));
		resultData.put("CONTRACT_END_DATE",pageData.getString("CONTRACT_END_DATE","2050-12-31 23:59:59"));
		resultData.put("CONTRACT_NUMBER",pageData.getString("AGREEMENT_ID"));
		resultData.put("ELECTRONIC_TYPE_SUB",pageData.getString("ELECTRONIC_TYPE_SUB"));
	}

	//建立附件正文参数
	public static IData buildSaveAccessContextInfo(IData pageData) throws Exception {
		// TODO Auto-generated method stub
		IData archivesIfno = new DataMap();
		archivesIfno.put("AGREEMENT_ID", pageData.getString("AGREEMENT_ID"));
		archivesIfno.put("PDF_FILE", pageData.getDataset("PDF_FILE"));
		return archivesIfno;

	}
	//构建正文附件表字段
	public static IDataset buildSaveAccessoryInfo(IData pageData) throws Exception {
		// TODO Auto-generated method stub
		IData archivesAttrs = new DataMap();
		IDataset archivesAttrsSet = new DatasetList();
		archivesAttrsSet.add(archivesAttrs);
		//协议部分字段
		addIfNotEmpty(archivesAttrs, pageData, "AGREEMENT_DEF_ID", "AGREEMENT_DEF_ID","");
		addIfNotEmpty(archivesAttrs, pageData, "CUST_ID", "CUST_ID","");

		return archivesAttrsSet;
	}
	
	public static void addIfNotEmpty(IData archivesAttrs, IData pageData, String key1, String key2, String defvar){
		if(StringUtils.isNotEmpty(pageData.getString(key2))){
			archivesAttrs.put(key1, pageData.getString(key2));
		}else if(StringUtils.isNotEmpty(defvar)){
			archivesAttrs.put(key1, defvar);
		}
	}
	public static void setCommInfoField(IData archives, IData pageData ,BizVisit visit) throws Exception {
		// TODO Auto-generated method stub
        archives.put("ARCHIVES_NAME", pageData.getString("ARCHIVES_NAME",""));
        archives.put("ARCHIVES_TYPE", pageData.getString("ARCHIVES_TYPE",""));
        archives.put("ELECTRONIC_TYPE_SUB", pageData.getString("ELECTRONIC_TYPE_SUB",""));
        archives.put("AGREEMENT_DEF_ID", pageData.getString("AGREEMENT_DEF_ID"));
        archives.put("CUST_ID", pageData.getString("CUST_ID"));
        archives.put("PRODUCT_ID", pageData.getString("PRODUCT_ID"));

		archives.put("UPDATE_STAFF_ID", visit.getStaffId());
		archives.put("UPDATE_DEPART_ID", visit.getDepartId());
		archives.put("UPDATE_TIME", SysDateMgr4Web.getSysDate(SysDateMgr4Web.PATTERN_STAND));
	}
	public static void setPDFField(int index, IDataset archiveslist, String... param) {
		// TODO Auto-generated method stub
		if(index>-1){
			IData data = new DataMap();
			data.put(param[index], "1");
			archiveslist.add(data);
		}
	}

	public static void setPDFDateField(String date, IData archiveInfo, String param1, String param2, String param3) {

	    archiveInfo.put(param1, date.substring(0, 4));
        archiveInfo.put(param2, date.substring(5, 7));
        archiveInfo.put(param3, date.substring(8, 10));

	}

	public static void addMapKeyToList(IData archivesInfo, IDataset archiveslist) {
		for (String key : archivesInfo.getNames()) {
			IData tempData = new DataMap();
			if(StringUtils.isNotEmpty(archivesInfo.getString(key))){
				tempData.put(key, archivesInfo.getString(key));
				archiveslist.add(tempData);
			}
		}
	}


	public static IData buildSaveProtocolInfo(IData pageData) throws Exception {
		// TODO Auto-generated method stub
		IData archivesIfno = new DataMap();
		archivesIfno.put("AGREEMENT_ID", pageData.getString("AGREEMENT_ID"));
		archivesIfno.put("A_NAME", pageData.getString("A_NAME"));
		archivesIfno.put("B_NAME", pageData.getString("B_NAME"));
		archivesIfno.put("A_ADDRESS", pageData.getString("A_ADDRESS"));
		archivesIfno.put("B_ADDRESS", pageData.getString("B_ADDRESS"));
		archivesIfno.put("A_HEADER", pageData.getString("A_HEADER"));
		archivesIfno.put("B_HEADER", pageData.getString("B_HEADER"));
		archivesIfno.put("A_CONTACT_PHONE", pageData.getString("A_CONTACT_PHONE"));
		archivesIfno.put("B_CONTACT_PHONE", pageData.getString("B_CONTACT_PHONE"));
		archivesIfno.put("A_BANK", pageData.getString("A_BANK"));
		archivesIfno.put("B_BANK", pageData.getString("B_BANK"));
		archivesIfno.put("A_SIGN_DATE", pageData.getString("A_SIGN_DATE"));
		archivesIfno.put("B_SIGN_DATE", pageData.getString("B_SIGN_DATE"));
		archivesIfno.put("A_BANK_ACCT_NO", pageData.getString("A_BANK_ACCT_NO"));
		archivesIfno.put("B_BANK_ACCT_NO", pageData.getString("B_BANK_ACCT_NO"));
		archivesIfno.put("PDF_FILE", pageData.getDataset("PDF_FILE"));
		archivesIfno.put("CONTRACT_CODE", pageData.getString("CONTRACT_CODE"));
		return archivesIfno;
	}

	public static String getFileBase64(String fileId,IVisit visit) throws Exception{

		ImpExpUtil.getImpExpManager().getFileAction().setVisit(visit);

		File file = ImpExpUtil.getImpExpManager().getFileAction().download(fileId, false);
		//测试
		//File file = new File("D:\\MyDownloads\\Download\\和商务酒店业务服务协议 (11).jpg");
		FileInputStream inputStream = null;

		String imgsStr = null;
		try {
			inputStream = new FileInputStream(file);

			byte[] data = new byte[inputStream.available()];

			inputStream.read(data);
			BASE64Encoder encoder = new BASE64Encoder();
			imgsStr = encoder.encode(data);
		}catch (Exception e){
			throw new RuntimeException(e);
		}finally {
			if(inputStream != null){
				inputStream.close();
			}
		}

		return imgsStr;
	}

	public static IDataset queryContractInfos(Set<String> contractSet, IBizCommon bc) throws Exception{
		if (DataUtils.isEmpty(contractSet)) {
			return null;
		}

		IDataset contractList = new DatasetList();
		for (String contractId : contractSet) {
			IData data = new DataMap();
			data.put("AGREEMENT_ID", contractId);
			// 正文信息
			IData agreementInfo = CSViewCall.callone(bc, "SS.QryAgreementSVC.qryAgreementInfo", data);

			if (DataUtils.isNotEmpty(agreementInfo)) {
				//标记本流程新增协议
				if(!"3".equals(agreementInfo.getString("ARCHIVES_STATE"))){
					agreementInfo.put("ARCHIVES_NAME",agreementInfo.getString("ARCHIVES_NAME")+"（新增）");
				}

				IData param = new DataMap();
				param.put("PRODUCT_ID", agreementInfo.getString("PRODUCT_ID"));
				param.put("AGREEMENT_TYPE", agreementInfo.getString("SUB_ARCHIVES_TYPE"));
				IDataset agreementDefs = CSViewCall.call(bc, "SS.QryAgreementSVC.qryAgreementDef", param);
				if (DataUtils.isEmpty(agreementDefs)) {
					CSViewException.apperr(CrmCommException.CRM_COMM_103, "未查询到电子协议正文配置！");
				}
				agreementInfo.putAll(agreementDefs.first());

				contractList.add(agreementInfo);
			}
			// 附加协议
			IDataset agreementAttachInfos = CSViewCall.call(bc, "SS.QryAgreementSVC.qryAgreementAttachInfo", data);

			if (DataUtils.isNotEmpty(agreementAttachInfos)) {
				for (int i = 0; i < agreementAttachInfos.size(); i++) {
					IData agreementAttachInfo = agreementAttachInfos.getData(i);

					//标记本流程新增协议
					if(!"3".equals(agreementAttachInfo.getString("ARCHIVES_STATE"))){
						agreementAttachInfo.put("ARCHIVES_NAME",agreementAttachInfo.getString("ARCHIVES_NAME")+"（新增）");
					}

					IData param = new DataMap();
					param.put("PRODUCT_ID", agreementAttachInfo.getString("PRODUCT_ID"));
					param.put("AGREEMENT_TYPE", agreementAttachInfo.getString("SUB_ARCHIVES_TYPE"));
					IDataset agreementDefs = CSViewCall.call(bc, "SS.QryAgreementSVC.qryAgreementDef", param);
					if (DataUtils.isEmpty(agreementDefs)) {
						CSViewException.apperr(CrmCommException.CRM_COMM_103, "未查询到电子协议附加协议配置！");
					}
					agreementAttachInfo.putAll(agreementDefs.first());
					contractList.add(agreementAttachInfo);
				}
			}
		}

		if(DataUtils.isNotEmpty(contractList)){
			for(int i=0;i<contractList.size();i++){
				IData contractData = contractList.getData(i);
				if(StringUtils.isNotBlank(contractData.getString("ARCHIVES_ATTACH"))){
					IDataset fileList = new DatasetList(contractData.getString("ARCHIVES_ATTACH"));
					for(int j = 0;j<fileList.size();j++){
						IData fileData = fileList.getData(j);

						//添加合同ID
						fileData.put("AGREEMENT_ID",contractData.getString("AGREEMENT_ID"));

						String fileName = fileData.getString("FILE_NAME");
						if(StringUtils.isNotBlank(fileName)){
							if(fileName.endsWith(".pdf")){
								fileData.put("TYPE","PDF");
							}else if(fileName.endsWith(".jpg")){
								fileData.put("TYPE","IMG");
								fileData.put("IMG_TYPE","JPG");
							}else if(fileName.endsWith(".jpeg")){
								fileData.put("TYPE","IMG");
								fileData.put("IMG_TYPE","JPEG");
							}else if(fileName.endsWith(".gif")){
								fileData.put("TYPE","IMG");
								fileData.put("IMG_TYPE","GIF");
							}else if(fileName.endsWith(".png")){
								fileData.put("TYPE","IMG");
								fileData.put("IMG_TYPE","PNG");
							}
						}
					}
					contractData.put("ARCHIVES_ATTACH",fileList);
				}
			}
		}

		return contractList;
	}

	public static IDataset queryContractNameInfo(Set<String> contractSet, IBizCommon bc) throws Exception{
		if (DataUtils.isEmpty(contractSet)) {
			return null;
		}

		IDataset contractList = new DatasetList();
		int i = 0;
		for (String contractId : contractSet) {
			IData data = new DataMap();
			data.put("AGREEMENT_ID", contractId);
			// 正文信息
			IData agreementInfo = CSViewCall.callone(bc, "SS.QryAgreementSVC.qryAgreementInfo", data);
			if (DataUtils.isNotEmpty(agreementInfo)) {
				IData contractData = new DataMap();
				contractData.put("ARCHIVES_NAME",agreementInfo.getString("ARCHIVES_NAME"));
				contractData.put("AGREEMENT_ID",contractId);
				contractData.put("INDEX",String.valueOf(i));
				contractList.add(contractData);
				i++;
			}
		}

		return contractList;
	}
}
