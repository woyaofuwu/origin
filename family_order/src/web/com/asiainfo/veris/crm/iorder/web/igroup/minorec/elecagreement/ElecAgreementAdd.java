package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import org.apache.tapestry.IRequestCycle;


/**
 * 电子合同新增
 *
 * @author ckh
 * @date 2018/10/22.
 */
public abstract class ElecAgreementAdd extends GroupBasePage
{
	public static final String AGREEMENT_TYPE_BODY = "AGCONTENT";


	/**
	 * 新增初始化
	 * @param cycle 请求对象
	 * @throws Exception 异常
	 */
	public void init(IRequestCycle cycle) throws Exception
	{
		IData info = getData();
		//产品ID
		if(StringUtils.isNotEmpty(info.getString("PRODUCT_ID"))){
			//根据产品ID查询合同模板信息
			changeProduct(cycle);
		}
		IDataset products = pageutil.getStaticList("ELECPRODUCTINFO");
		info.put("PRODUCTS",products);
		info.put("SHOWBUTTON","true");
		setInfo(info);
	}

	/**
	 * 查询产品合同配置信息
	 * @param cycle
	 * @throws Exception 查询异常
	 */
	public void queryAgreementInfos(IRequestCycle cycle) throws Exception
	{
		IData param = new DataMap();
		String productId = getData().getString("PRODUCT_ID","");
		String contractCode = getData().getString("CONTRACT_CODE","");
		if(StringUtils.isNotEmpty(productId) && StringUtils.isNotEmpty(contractCode)){
			param.put("PRODUCT_ID", productId);
			param.put("CONTRACT_CODE", contractCode);
			IDataset agreementInfos = CSViewCall.call( this,"SS.AgreementInfoSVC.queryAgreementDefInfo", param);
			if (IDataUtil.isEmpty(agreementInfos))
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103,"根据PRODUCT_ID:" + productId + "未找到合同配置！");

			}
			setAgreementInfos(agreementInfos);
		}
		IData info = new DataMap();
		info.put("SHOWBUTTON","true");
		setInfo(info);
	}
	/**
	 * 查询产品合同配置信息
	 * @param cycle
	 * @throws Exception 查询异常
	 */
	public void changeProduct(IRequestCycle cycle) throws Exception
	{
		IData param = new DataMap();
		String productId = getData().getString("PRODUCT_ID","");
		if(StringUtils.isNotEmpty(productId)){
			param.put("PRODUCT_ID", productId);
			param.put("AGREEMENT_TYPE", AGREEMENT_TYPE_BODY);
			IDataset contracts = CSViewCall.call( this,"SS.AgreementInfoSVC.queryAgreementDefInfo", param);
			if (IDataUtil.isEmpty(contracts))
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据PRODUCT_ID:" + productId + "未找到合同配置！");
			}
			setContracts(contracts);
		}
	}

	/**
	 * 预占合同ID
	 * @param cycle {@linkplain IRequestCycle}
	 * @throws Exception 取值异常
	 */
	public void getContractId(IRequestCycle cycle) throws Exception
	{

		ServiceResponse response = BizServiceFactory.call("CC.groupcontract.IGroupContractQuerySV.queryNewContractId", new DataMap());
		IData ret = response.getBody();

		if (IDataUtil.isEmpty(ret))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "合同编码获取失败，请检查合同编码是否用尽！");
		}
		IData retData = new DataMap();
		retData.put("CONTRACT_NUMBER", ret.getString("DATAS"));
		setAjax(retData);
	}


	public void checkAgreementID(IRequestCycle cycle) throws Exception{
		IData retData = new DataMap();
		String agreeementID = getData().getString("AGREEMENT_ID");
		if(StringUtils.isEmpty(agreeementID)){
			retData.put("USED", "Y");
		}else{
			IData param = new DataMap();
			param.put("AGREEMENT_ID", agreeementID);
			IDataset contextField = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElectronicAgreement", param);
			if (IDataUtil.isEmpty(contextField)){
				retData.put("USED", "N");
			}else{
				retData.put("USED", "Y");
			}
		}
		setAjax(retData);

	}

	/**
	 * 查询合同数据
	 * @param cycle {@link IRequestCycle 页面请求对象}
	 * @throws Exception 查询异常
	 */
	public void queryContractInfo(IRequestCycle cycle) throws Exception
	{
		String productId = getData().getString("PRODUCT_ID");
		String contractId = getData().getString("CONTRACT_ID");

		IData data = new DataMap();
		data.put("CUST_ID", getData().getString("CUST_ID"));
		data.put("PRODUCT_ID", productId);
		IDataset contractLists = CSViewCall.call(this, "SS.AgreementInfoSVC.queryAgreementInfosByCIDAndPID", data);
		setContractInfos(contractLists);

		// 1- 无合同编码则初始化基础信息
		if (StringUtils.isNotEmpty(contractId) && StringUtils.isNotEmpty(productId))
		{
			// 2- 根据合同编码初始化数据
			IData param = new DataMap();
			param.put("PRODUCT_ID", productId);
			param.put("AGREEMENT_ID", contractId);
			IDataset agreementInfos = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElectronicAgreement", param);
			if (IDataUtil.isEmpty(agreementInfos))
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据合同编码" + contractId + "未找到合同信息！");
			}
			IData agreementInfo = agreementInfos.first();
			param.put("CONTRACT_CODE",agreementInfo.getString("CONTRACT_CODE"));
			getData().put("CONTRACT_CODE",agreementInfo.getString("CONTRACT_CODE"));

			IDataset contractInfos = getArchiveAllInfos(param,this);
			setAgreementInfo(param);
			setAgreementInfos(contractInfos);

			IData info = new DataMap();
			if("3".equals(agreementInfo.getString("ARCHIVES_STATE"))){
				info.put("SHOWBUTTON","false");
			}else {
				info.put("SHOWBUTTON","true");
			}
			info.put("CONTRACT_ID",contractId);
			setInfo(info);
			setAjax(agreementInfo);
		}else{
			queryAgreementInfos(cycle);
		}

	}

	public static IDataset getArchiveAllInfos(IData inparam , IBizCommon biz) throws Exception
	{
		// 1- 查询产品电子协议定义信息
		IDataset results = CSViewCall.call(biz, "SS.AgreementInfoSVC.queryAgreementDefInfo", inparam);


		// 2- 查询合同下对应的实例数据
		for (int i = 0; i < results.size(); i++)
		{
			IDataset instanceInfo = new DatasetList();
			IData agreementDefInfo = results.getData(i);
			// 2.1- 判断电子协议类型
			String agreementType = agreementDefInfo.getString("AGREEMENT_TYPE");
			// 合同正文
			if (AGREEMENT_TYPE_BODY.equals(agreementType))
			{
				IDataset agreementInfos = CSViewCall.call(biz, "SS.AgreementInfoSVC.queryElectronicAgreement", inparam);
				if (IDataUtil.isEmpty(agreementInfos))
				{
					continue;
				}
				IData agreementInfo = agreementInfos.first();

				IData param = new DataMap();
				param.put("ARCHIVES_ID", agreementInfo.getString("ARCHIVES_ID"));
				IDataset archiveInfos = CSViewCall.call(biz, "SS.AgreementInfoSVC.queryElectronicArchives", param);

				if (IDataUtil.isEmpty(archiveInfos))
				{
					continue;
				}
				IData archiveInfo = archiveInfos.first();
				// 处理签约附件文件列表
				StringBuilder fileList = dealArchiveAttachInfo(archiveInfo);
				if (fileList.length() > 0)
				{
					archiveInfo.put("ARCHIVE_FILE_ID", fileList.substring(1));
				}
				archiveInfo.put("AGREEMENT_INFO", agreementInfo);
				instanceInfo.add(archiveInfo);
			}
			// 合同附件
			else
			{
				IDataset attachInfos = CSViewCall.call(biz,"SS.AgreementInfoSVC.queryElectronicAgreAttach", inparam);

				if (IDataUtil.isEmpty(attachInfos))
				{
					continue;
				}
				for (int j = 0; j < attachInfos.size(); j++)
				{
					IData attachInfo = attachInfos.getData(j);

					IData param = new DataMap();
					param.put("ARCHIVES_ID", attachInfo.getString("ARCHIVES_ID"));
					IDataset archiveInfos = CSViewCall.call(biz,"SS.AgreementInfoSVC.queryElectronicArchives", param);

					if (IDataUtil.isEmpty(archiveInfos))
					{
						continue;
					}
					if (!StringUtils.equals(archiveInfos.first().getString("SUB_ARCHIVES_TYPE"), agreementType))
					{
						continue;
					}
					IData archiveInfo = archiveInfos.first();
					// 处理签约附件文件列表
					StringBuilder fileList = dealArchiveAttachInfo(archiveInfo);
					if (fileList.length() > 0)
					{
						archiveInfo.put("ARCHIVE_FILE_ID", fileList.substring(1));
					}
					archiveInfo.put("AGREEMENT_INFO", attachInfo);
					instanceInfo.add(archiveInfo);
				}
			}
			if (IDataUtil.isNotEmpty(instanceInfo))
			{
				agreementDefInfo.put("INSTANCE_INFO", instanceInfo);
			}
		}
		return results;
	}

	private static StringBuilder dealArchiveAttachInfo(IData archiveInfo)
	{
		StringBuilder fileList = new StringBuilder();
		String archivesAttach = archiveInfo.getString("ARCHIVES_ATTACH");
		IDataset fileInfos = new DatasetList();
		if (StringUtils.isNotEmpty(archivesAttach))
		{
			fileInfos = new DatasetList(archivesAttach);
		}
		for (int j = 0; j < fileInfos.size(); j++)
		{
			IData fileInfo = fileInfos.getData(j);
			fileList.append(",").append(fileInfo.getString("FILE_ID"));
		}
		return fileList;
	}

	public void onsubmit(IRequestCycle cycle) throws Exception
	{
		IData contractInfo = new DataMap(getData().getString("SUBMIT_DATA"));

		IDataset ret = CSViewCall.call(this,"SS.AgreementInfoSVC.addElecAgreementAttachInfo", contractInfo);

	}

	public abstract void setInfo(IData info) throws Exception;

	public abstract void setAgreementInfos(IDataset agreementInfos) throws Exception;

	public abstract void setContracts(IDataset contracts) throws Exception;

	public abstract void setAgreementInfo(IData agreementInfo) throws Exception;

	public abstract void setContractInfos(IDataset contractInfos) throws Exception;
}
