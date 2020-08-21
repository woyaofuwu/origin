package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import com.asiainfo.veris.crm.iorder.pub.consts.GroupStandardConstans;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustContractInfoQry;

public class ContractItemInfoQueryBean extends CSBizBean {
	protected static final Logger logger = Logger.getLogger(ContractItemInfoQueryBean.class);

	public IData contractItemInfoQuery(IData params ,Boolean isDetail) throws Exception{

		String contractNumber = IDataUtil.chkParam(params, "contractNumber");
		String custName = params.getString( "CUST_NAME");
		if( logger.isDebugEnabled()) {
			logger.debug("=============contractNumber========="+contractNumber);
		}
		IData infos = new DataMap();
		IDataset oldConSet = CustContractInfoQry.qryContractInfoByContractId(contractNumber);//登记在客管那边的信息
		//产品信息非必须 则 暂时不传
		//IDataset conProSet = CustContractProductInfoQry.qryContractProductByContId(contractNumber);//登记在客管那边的信息
		if( CollectionUtils.isEmpty( oldConSet )) {
			IData param = new DataMap();
			param.put("AGREEMENT_ID", contractNumber);
			IDataset conSet = Dao.qryByCode("TF_F_ELECTRONIC_AGREEMENT", "SEL_BY_AGREEMENT_ID", param ,Route.CONN_CRM_CG );
			if(  CollectionUtils.isEmpty( conSet ) ) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据合同编号:" + contractNumber + ",未找到合同信息！");
			}
			IData contractInfo = conSet.first();
			infos.put("contractID",contractNumber);
			infos.put("contractNumber",contractNumber);
			infos.put("contractNmae",contractInfo.getString("CONTRACT_NAME"));
			infos.put("firstParty",contractInfo.getString("A_NAME"));
			infos.put("secondParty",contractInfo.getString("B_NAME"));
			infos.put("customerID",contractInfo.getString("CUST_ID"));
			infos.put("contractDate", contractInfo.getString("A_SIGN_DATE") );
			infos.put("validDate", contractInfo.getString("START_DATE") );
			infos.put("expireDate", contractInfo.getString("END_DATE") );
			if( isDetail ) {
				IData contractOherInfo = new DataMap();
				infos.put("textOfClause",contractInfo.getString("ARCHIVES_NAME")); //条款正文
				String contractFileStr= contractInfo.getString("PDF_FILE");
//					Map contractFile= (Map) ( (List)JSON.parse( contractFileStr ) ).get(0);
				IData contractFile= new DatasetList( contractFileStr ).first();
				contractOherInfo.put("contractFileID",contractFile.get("FILE_ID") ); //文件file_id
				String[] fileName = contractFile.get("FILE_NAME").toString().split("[.]");
				contractOherInfo.put("fileType",dealFileType( fileName[fileName.length-1]) ); //文件后缀文
				contractOherInfo.put("fileName", fileName[0] ); //附件名称

				String filePath = getFilePathByFileId( contractNumber ,contractFile ) ;
				contractOherInfo.put("filePath",filePath ); //附件路径
				infos.put("contractFileList",new DatasetList( contractOherInfo ) );//合同附件列表
			}
		}else {
			IData contractInfo = oldConSet.first();
			infos.put("contractID",contractNumber);
			infos.put("contractNumber",contractNumber);
			infos.put("contractNmae",contractInfo.getString("CONTRACT_NAME"));
			infos.put("firstParty", custName );
			infos.put("secondParty", GroupStandardConstans.SECOND_PARTY);
			infos.put("customerID",contractInfo.getString("CUST_ID"));
			infos.put("contractDate", dealStandardTimeToYYYYMMDDHHMI24SS (
					contractInfo.getString("CONTRACT_WRITE_DATE") ));
			infos.put("validDate", dealStandardTimeToYYYYMMDDHHMI24SS (
					contractInfo.getString("CONTRACT_START_DATE") ));
			infos.put("expireDate", dealStandardTimeToYYYYMMDDHHMI24SS (
					contractInfo.getString("CONTRACT_END_DATE") ));
			if( isDetail ) {
				IData contractOherInfo = new DataMap();
				infos.put("textOfClause",contractInfo.getString("REMARK")); //条款正文
				String[] contractFileID = contractInfo.getString("CONTRACT_FILE_ID").split(":");
				if( contractFileID.length >0 ) {
					contractOherInfo.put("contractFileID",contractFileID[0] ); //文件file_id
					String[] fileName = contractFileID[1].split("[.]");
					contractOherInfo.put("fileType",dealFileType( fileName[fileName.length-1] ) ); //文件后缀文
					contractOherInfo.put("fileName", fileName[0] ); //附件名称
					IData file = new DataMap();
					file.put("FILE_ID",contractFileID[0]);
					String filePath = getFilePathByFileId( contractNumber , file );
					contractOherInfo.put("filePath", filePath ); //附件路径
				}
				infos.put("contractFileList",new DatasetList( contractOherInfo ) );//合同附件列表
			}
		}


		return infos;
	}

	/**
	 * 处理时间转成纯数字
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private String dealStandardTimeToYYYYMMDDHHMI24SS( String date) throws Exception {
		return date.replaceAll("-","").replaceAll(":","").replaceAll(" ","");
	}

	/**
	 * 文件后缀名
	 * @param dealType
	 * @return
	 * @throws Exception
	 */
	private String dealFileType( String dealType) throws Exception {
		String fileType ="3";
		if("Jpeg".equalsIgnoreCase(dealType)) {
			fileType ="0";
		}else if("Img".equalsIgnoreCase(dealType)) {
			fileType ="1";
		} else if("Pdf".equalsIgnoreCase(dealType)){
			fileType ="2";
		}
		return fileType;
	}

	/**
	 * 获取文件上传路径
	 * @param contractNumber
	 * @param contractFile
	 * @return
	 */
	private String getFilePathByFileId( String contractNumber,IData contractFile) throws Exception {

		IDataset files = Dao.qryByCode("WD_F_FTPFILE", "SEL_BY_FILE_ID",contractFile, Route.CONN_CRM_CEN);
		if(CollectionUtils.isEmpty( files )){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据合同编号:" + contractNumber + ",未找到合同上传路径信息！");
		}
		String filePath =  files.first().getString("FILE_PATH");
		return filePath ;
	}

}
