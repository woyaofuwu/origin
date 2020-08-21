package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.minorec;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;


/**
 * 电子合同新增
 *
 * @author pangs
 * @date 2019/12/20.
 */
public abstract class ElecAgreementAdd extends BizTempComponent
{
	public static final String AGREEMENT_TYPE_BODY = "AGCONTENT";

	public static final int DATE_LENTH = 10;

	public void renderComponent(StringBuilder informalParametersBuilder,IMarkupWriter writer, IRequestCycle cycle) throws Exception{
		boolean isAjax = isAjaxServiceReuqest(cycle);
		String jsFile = "scripts/iorder/icsserv/component/enterprise/ecintegration/elecagreement/ElecAgreementAdd.js";

		if (isAjax)
		{
			includeScript(writer, jsFile, false, false);
		}
		else
		{
			getPage().addResAfterBodyBegin(jsFile, false, false);
		}

		String action = getPage().getData().getString("ACTION");
		if("init".equals(action)){
			init(cycle);
		}else if("queryAgreementInfos".equals(action)){
			queryAgreementInfos(cycle);
		}else if("changeProduct".equals(action)){
			changeProduct(cycle);
		}else if("getContractId".equals(action)){
			getContractId(cycle);
		}else if("checkAgreementID".equals(action)){
			checkAgreementID(cycle);
		}else if("queryContractInfo".equals(action)){
			queryContractInfo(cycle);
		}else if("onsubmit".equals(action)){
			onsubmit(cycle);
		}else if ("getContractImg".equals(action)){
			getContractImg(cycle);
		}else if("checkAgreementAttach".equals(action)){
			checkAgreementAttach(cycle);
		}else if("checkAgreementBpm".equals(action)){
			checkAgreementBpm(cycle);
		}
	}

	/**
	 * 新增初始化
	 * @param cycle 请求对象
	 * @throws Exception 异常
	 */
	public void init(IRequestCycle cycle) throws Exception
	{
		IData info = getPage().getData();
		String contractId = info.getString("CONTRACT_ID");
		String productId = info.getString("PRODUCT_ID","");

		//产品ID
		if(StringUtils.isNotEmpty(productId)) {
			//根据产品ID查询合同模板信息
			changeProduct(cycle);
			IData data = new DataMap();
			IDataset products = new DatasetList();
			String value = StaticUtil.getStaticValue("ELECPRODUCT", productId);
			data.put("DATA_ID", productId);
			data.put("DATA_NAME", value);
			products.add(data);
			info.put("PRODUCTS", products);
		} else {//测试用
			IDataset products = StaticUtil.getStaticList("ELECPRODUCTINFO");
			info.put("PRODUCTS", products);
		}
		info.put("SHOWBUTTON", "true");

		setAgreementInfo(new DataMap());
		setAgreementInfos(new DatasetList());

		if(StringUtils.isNotBlank(contractId)) {

			// 2- 根据合同编码初始化数据
			IData param = new DataMap();
			param.put("PRODUCT_ID", productId);
			param.put("AGREEMENT_ID", contractId);
			IDataset agreementInfos = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElectronicAgreement", param);
			if (IDataUtil.isNotEmpty(agreementInfos))
			{
				IData agreementInfo = agreementInfos.first();
				param.put("CONTRACT_CODE",agreementInfo.getString("CONTRACT_CODE"));

				info.put("CONTRACT_CODE", agreementInfo.getString("CONTRACT_CODE"));

				IDataset contractInfos = getArchiveAllInfos(param,this);
				setAgreementInfo(param);
				setAgreementInfos(contractInfos);

				//设置返回值
				IData returnData = new DataMap();
				returnData.put("AGREEMENT_ID", agreementInfo.getString("AGREEMENT_ID"));
				returnData.put("CONTRACT_WRITE_DATE", agreementInfo.getString("START_DATE"));
				returnData.put("CONTRACT_END_DATE", agreementInfo.getString("END_DATE"));
				returnData.put("ARCHIVES_NAME", agreementInfo.getString("ARCHIVES_NAME"));
				returnData.put("PRODUCT_ID", agreementInfo.getString("PRODUCTS"));
				IData ajaxData = new DataMap();
				ajaxData.put("MINOREC_PRODUCT_INFO", returnData);
				getPage().setAjax(ajaxData);
			}

		}
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
		String productId = getPage().getData().getString("PRODUCT_ID","");
		String contractCode = getPage().getData().getString("CONTRACT_CODE","");
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
		//防止加载出已有协议编码
		setAgreementInfo(info);
	}

	/**
	 * 查询产品合同配置信息
	 * @param cycle
	 * @throws Exception 查询异常
	 */
	public void changeProduct(IRequestCycle cycle) throws Exception
	{
		IData param = new DataMap();
		String productId = getPage().getData().getString("PRODUCT_ID","");
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

		//防止取到相同合同编码
		IData ret = null;
		for(;;){
			ret = CSViewCall.callone(this,"SS.AgreementInfoSVC.getAgreementId",new DataMap());
			if (IDataUtil.isEmpty(ret))
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "合同编码获取失败，请检查合同编码是否用尽！");
			}
			IData input = new DataMap();
			input.put("AGREEMENT_ID",ret.getString("AGREEMENT_ID"));
			IDataset agreementInfos = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElectronicAgreement", input);
			if(DataUtils.isEmpty(agreementInfos)){
				break;
			}
		}

		IData retData = new DataMap();
		retData.put("CONTRACT_NUMBER", ret.getString("AGREEMENT_ID"));
		getPage().setAjax(retData);
	}


	public void checkAgreementID(IRequestCycle cycle) throws Exception{
		IData retData = new DataMap();
		String agreeementID = getPage().getData().getString("AGREEMENT_ID");
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
		getPage().setAjax(retData);

	}

	/**
	 * 查询合同数据
	 * @param cycle {@link IRequestCycle 页面请求对象}
	 * @throws Exception 查询异常
	 */
	public void queryContractInfo(IRequestCycle cycle) throws Exception
	{
		String productId = getPage().getData().getString("PRODUCT_ID");
		String contractId = getPage().getData().getString("CONTRACT_ID");

		IData data = new DataMap();
		data.put("CUST_ID", getPage().getData().getString("CUST_ID"));
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
			getPage().getData().put("CONTRACT_CODE",agreementInfo.getString("CONTRACT_CODE"));

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
			getPage().setAjax(agreementInfo);
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
				String archivesAttach = archiveInfo.getString("ARCHIVES_ATTACH");
				if(StringUtils.isBlank(archivesAttach)){
					archiveInfo.put("ARCHIVES_ATTACH", "");
				}
				dealArchiveDate(archiveInfo);
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
					String archivesAttach = archiveInfo.getString("ARCHIVES_ATTACH");
					if(StringUtils.isBlank(archivesAttach)){
						archiveInfo.put("ARCHIVES_ATTACH", "");
					}
					dealArchiveDate(archiveInfo);
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

	/**
	 * 格式化日期
	 * @param archiveInfo
	 */
	private static void dealArchiveDate(IData archiveInfo)
	{
		String startDate = archiveInfo.getString("START_DATE");
		String endDate = archiveInfo.getString("END_DATE");

		if(StringUtils.isNotBlank(startDate)){
			archiveInfo.put("START_DATE",startDate.substring(0,DATE_LENTH));
		}

		if(StringUtils.isNotBlank(endDate)){
			archiveInfo.put("END_DATE",endDate.substring(0,DATE_LENTH));
		}
	}

	public void checkAgreementAttach(IRequestCycle cycle) throws  Exception{
		IData param = new DataMap();
		param.put("AGREEMENT_ID",getPage().getData().getString("AGREEMENT_ID"));

		IDataset agreementAttchList = new DatasetList();
		IDataset agreementInfos = CSViewCall.call(this,"SS.QryAgreementSVC.qryAgreementInfo",param);
		if(DataUtils.isNotEmpty(agreementInfos)){
			agreementAttchList.addAll(agreementInfos);
		}

		IDataset agreementAttchs = CSViewCall.call(this,"SS.QryAgreementSVC.qryAgreementAttachInfo",param);
		if(DataUtils.isNotEmpty(agreementAttchs)){
			agreementAttchList.addAll(agreementAttchs);
		}

		getPage().setAjax(agreementAttchList);

	}

	public void getContractImg(IRequestCycle cycle) throws Exception{
		IData param = getPage().getData();
		IData input = new DataMap();
		input.put("ARCHIVES_ID",param.getString("ARCHIVE_ID"));
		IDataset agInfos = CSViewCall.call(this,"SS.AgreementInfoSVC.queryElectronicArchives", input);

		if(DataUtils.isEmpty(agInfos)){
			return;
		}
		IDataset archivesAttach = new DatasetList(agInfos.first().getString("ARCHIVES_ATTACH"));

		String imgBase64 = ElecLineUtil.getFileBase64(archivesAttach.first().getString("FILE_ID"),getVisit());

		IData data = new DataMap();
		data.put("imgBase64",imgBase64);
		getPage().setAjax(data);
	}

	public void checkAgreementBpm(IRequestCycle cycle) throws Exception{
		IData param = getPage().getData();
		String archiveType = param.getString("AGREEMENT_DEF_ID");
		String text = StaticUtil.getStaticValue("NEED_CHECK_ARCHIVES",archiveType);
		IData result = null;
		if(StringUtils.isNotBlank(text)){
			result = CSViewCall.callone(this,"SS.AgreementInfoSVC.checkAgreementBpm",param);
		}else{
			result = new DataMap();
			result.put("RESULT","true");
		}

		getPage().setAjax(result);
	}

	public void onsubmit(IRequestCycle cycle) throws Exception
	{
		IData contractInfo = new DataMap(getPage().getData().getString("SUBMIT_DATA"));

		IDataset ret = CSViewCall.call(this,"SS.AgreementInfoSVC.addElecAgreementAttachInfo", contractInfo);

	}

	public abstract void setInfo(IData info) throws Exception;

	public abstract void setAgreementInfos(IDataset agreementInfos) throws Exception;

	public abstract void setContracts(IDataset contracts) throws Exception;

	public abstract void setAgreementInfo(IData agreementInfo) throws Exception;

	public abstract void setContractInfos(IDataset contractInfos) throws Exception;
}
