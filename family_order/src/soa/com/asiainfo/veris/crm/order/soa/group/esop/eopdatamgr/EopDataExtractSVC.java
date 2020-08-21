package com.asiainfo.veris.crm.order.soa.group.esop.eopdatamgr;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.eopdatatrans.EopDataTransBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EopEomsInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeTraQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformNodeBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

/**
 * order
 * eop系统数据提取接口
 *
 * @author ckh
 * @date 2018/2/27.
 */
public class EopDataExtractSVC extends GroupOrderService
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IDataset extractEomsDataForEwe(IData param) throws Exception
    {
        ExtractEomsDataForEweBean bean = new ExtractEomsDataForEweBean();
        return bean.extractData(param);
    }
	public IDataset extractIdcOpenDataForEwe(IData param) throws Exception
    {
		ExtractIdcDataForEweBean bean = new ExtractIdcDataForEweBean();
        return bean.makeMainData(param);
    }
	public IDataset extractIdccheckInDataForEwe(IData param) throws Exception
    {
		ExtractIdcDataForEweBean bean = new ExtractIdcDataForEweBean();
        return bean.checkInData(param);
    }
	public IDataset extractIdcReturnDataForEwe(IData param) throws Exception
    {
		ExtractIdcDataForEweBean bean = new ExtractIdcDataForEweBean();
        return bean.returnCommon(param);
    }
	public IDataset extractSubEomsDataForEwe(IData param) throws Exception
    {
        ExtractEomsDataForEweBean bean = new ExtractEomsDataForEweBean();
        return bean.extractSubData(param);
    }
	
	public IDataset renewEomsDataForEwe(IData param) throws Exception
    {
        ExtractEomsDataForEweBean bean = new ExtractEomsDataForEweBean();
        return bean.renewEomsDataForEwe(param);
    }

	public IDataset getEomsAddrInfo(IData param) throws Exception
	{
		IDataset eomsAddrInfo = EopEomsInfoQry.qryEomsAddrInfo(param, this.getPagination());
		if (DataUtils.isEmpty(eomsAddrInfo))
		{
			return new DatasetList();
		}
		return eomsAddrInfo;

	}
    
    public IDataset createGroupUser(IData param) throws Exception
    {
    	IData inparam = new DataMap();
    	String busiformId = param.getString("BUSIFORM_ID");
		String nodeId = "eApply";
		IDataset nodeDataset = EweNodeTraQry.qryEweNodeTraByBusiformIdAndNodeId(busiformId, nodeId);
		String eparchCode = nodeDataset.first().getString("DEAL_EPARCHY_CODE");
		String departId = nodeDataset.first().getString("UPDATE_DEPART_ID");
		String staffId = nodeDataset.first().getString("DEAL_STAFF_ID");
		
    	String subIbsysId = param.getString("SUB_BI_SN");
    	String ibsysId = "";
    	
    	IDataset attrDataset = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, "0");
    	
    	IData data = new DataMap();
    	
    	if (IDataUtil.isNotEmpty(attrDataset)) {
			for (int i = 0; i < attrDataset.size(); i++) {
				
				data.put(attrDataset.getData(i).getString("ATTR_CODE"), attrDataset.getData(i).getString("ATTR_VALUE"));
				ibsysId = attrDataset.getData(i).getString("IBSYSID");
			}
		}
    	
		String custId = data.getString("CUST_ID", "");
		String custName = data.getString("CUST_NAME","");
		inparam.put("CUST_ID",custId);
		
		inparam.put("CUST_NAME",custName);

		inparam.put("IF_CENTRETYPE","0");

		String serialNumber = data.getString("SERIAL_NUMBER", "");
		inparam.put("SERIAL_NUMBER",serialNumber);

		String userDiffCode = data.getString("USER_DIFF_CODE", "");
		String contractId = data.getString("GRP_CONTRACT_ID",""); // 合同

		IData userInfo = new DataMap();
		userInfo.put("USER_DIFF_CODE", userDiffCode);
		userInfo.put("CONTRACT_ID", contractId);
		userInfo.put("CITY_CODE", eparchCode);
		userInfo.put("IN_STAFF_ID", staffId);
		userInfo.put("IN_DEPART_ID", departId);
		inparam.put("USER_INFO",userInfo);

		IDataset postInfos = new DatasetList();
		IData postInfo = new DataMap();
		postInfo.put("POST_CONTENT", "0");
		postInfo.put("POST_CYC", "0");
		postInfo.put("POST_TYPESET", "0");
		postInfo.put("POST_TAG", "0");
		postInfos.add(postInfo);
		inparam.put("POST_INFO",postInfos);

		IData acctInfo = new DataMap();
		acctInfo.put("CONTRACT_ID", data.getString("CONTRACT_ID",""));
		acctInfo.put("SUPER_BANK_CODE", data.getString("SUPER_BANK_CODE",""));
		acctInfo.put("PAY_NAME", data.getString("ACCT_NAME",""));
		acctInfo.put("BANK_ACCT_NO", data.getString("BANK_ACCT_NO",""));
		acctInfo.put("RSRV_STR8", data.getString("RSRV_STR8",""));
		acctInfo.put("BANK_CODE", data.getString("BANK_CODE",""));
		acctInfo.put("RSRV_STR9", data.getString("RSRV_STR9",""));
		acctInfo.put("PAY_MODE_CODE", data.getString("ACCT_TYPE",""));
		
		inparam.put("ACCT_INFO", acctInfo);

		IData payPlan = new DataMap();
		String grpProductId = data.getString("GRP_OFFER_CODE","");
		if ("6085".equals(grpProductId)) {
			payPlan.put("PAY_FEE_MODE_CODE", "G");
			payPlan.put("USER_NAME", "集团专线用户");
			payPlan.put("PLAN_MODE_CODE", "G");
			payPlan.put("PLAN_TYPE", "G");
		}
		inparam.put("PLAN_INFO",payPlan);

		IDataset resinfos = new DatasetList();
		inparam.put("RES_INFO",resinfos);

		IDataset productElements = new DatasetList();
    	
    	IDataset svcDataset = EopDataTransBean.buildWorkformSvc(subIbsysId, "0");
    	setElementInfo(svcDataset, productElements, data.getString("GRP_OFFER_CODE",""));
    	
    	IDataset disDataset = EopDataTransBean.buildWorkformDis(subIbsysId, "0");
    	setElementInfo(disDataset, productElements, data.getString("GRP_OFFER_CODE",""));
    	
    	inparam.put("ELEMENT_INFO",productElements);

		IDataset productParam = new DatasetList();
		inparam.put("PRODUCT_PARAM_INFO",productParam);

		IDataset userGrpPackaeInfos = new DatasetList();//saveUserGrpPackageInfoFrontData(data);
		inparam.put("GRP_PACKAGE_INFO",userGrpPackaeInfos);

		inparam.put("PRODUCT_ID",data.getString("GRP_OFFER_CODE",""));
		inparam.put("ACCT_IS_ADD","true");
		//生效方式
		inparam.put("EFFECT_NOW", "true");
		inparam.put(Route.USER_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());
		inparam.put("IBSYSID", ibsysId);
		IDataset eosdDataset = new DatasetList();
		
		IDataset eopSubscribe = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
		if (IDataUtil.isEmpty(eopSubscribe)) {
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID"+ibsysId+"查询TF_B_EOP_SUBSCRIBE信息不存在！");
		}
		
		IData eosData = new DataMap();
		eosData.put("IBSYSID", ibsysId);
		eosData.put("ATTR_CODE", "ESOP");
		eosData.put("ATTR_VALUE", ibsysId);
		eosData.put("FLOW_MAIN_ID", eopSubscribe.getData(0).getString("BPM_TEMPLET_ID"));
		eosData.put("BPM_TEMPLET_ID", eopSubscribe.getData(0).getString("BPM_TEMPLET_ID"));
		eosData.put("RECORD_NUM", "0" );
		eosData.put("RSRV_STR9", "0" );
		eosData.put("RSRV_STR1", "creatGrpUser" );
		eosData.put("NODE_ID", "creatGrpUser" );
        eosData.put("RSRV_STR2", "01");
        eosData.put("SUB_IBSYSID", subIbsysId);
		eosdDataset.add(eosData);
		
		inparam.put("EOS", eosdDataset);
        
        IDataset result = CSAppCall.call("CS.CreateGroupUserSvc.createGroupUser", inparam);
        
        return result;
    }
    
    
    public IDataset createBatOpenMeb(IData param) throws Exception
     {
    	IDataset result = new DatasetList();
    	IData inparam = new DataMap();
    	String recordNum = param.getString("RECORD_NUM"); 
    	String ibsysid = param.getString("IBSYSID");
    	IDataset subscribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if (IDataUtil.isEmpty(subscribeDataset)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID【"+ibsysid+"】查询eop订单不存在！");
		}
        
        String epachyCode = subscribeDataset.first().getString("EPARCHY_CODE");
        
        IDataset infoData = WorkformNodeBean.qryNodeByIbsysidNodeDesc(ibsysid, "eOpenNewWorkSheet");
        if (IDataUtil.isEmpty(infoData)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID【"+ibsysid+"】,NODE_ID【eOpenNewWorkSheet】查询TF_B_EOP_NODE资料不存在");
		}
        
        String subIbsysId = infoData.getData(0).getString("SUB_IBSYSID");
    	
    	IDataset attrDataset = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, "0");
    	
    	IData data = new DataMap();
    	
    	if (IDataUtil.isNotEmpty(attrDataset)) {
			for (int i = 0; i < attrDataset.size(); i++) {
				
				data.put(attrDataset.getData(i).getString("ATTR_CODE"), attrDataset.getData(i).getString("ATTR_VALUE"));
			}
		}
    	
		String custId = data.getString("CUST_ID", "");
		inparam.put("CUST_ID", custId);
		inparam.put("CUST_NAME",custId);
		
		// 集团三户信息(专线产品需要)
		String grpserialNumber = data.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(grpserialNumber);
		if (IDataUtil.isEmpty(userInfo)) {
			CSAppException.apperr(CrmUserException.CRM_USER_434, grpserialNumber);
		}
		IData userInfoData = new DataMap();
		userInfoData.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
		userInfoData.put("USER_ID", userInfo.getString("USER_ID"));
		inparam.put("USER_INFO",userInfoData);
		
		IData acctInfoData = UcaInfoQry.qryAcctInfoByUserId(userInfo.getString("USER_ID"));
		inparam.put("ACCT_INFO",acctInfoData);

		String custInfoTeltype = data.getString("CUST_INFO_TELTYPE", "1"); // IMS客户端类型
		inparam.put("CUST_INFO_TELTYPE",custInfoTeltype);
		
		//合同单号
		String CONTRACT_ID = data.getString("GRP_CONTRACT_ID", "");
		inparam.put("CONTRACT_ID", CONTRACT_ID);
		
		// 资源信息
		inparam.put("RES_INFO",new DatasetList());
		
		// 成员三户信息
		inparam.put("MEM_CUST_INFO", new DatasetList());

		inparam.put("MEM_USER_INFO",new DatasetList());

		inparam.put("MEM_ACCT_INFO",new DatasetList());
		
    	IData resultData = new DataMap();
    	
    	IData paramInfo = new DataMap();
    	IDataset paramInfos = new DatasetList();
    	IDataset proParamDataset = new DatasetList();
    	//公用参数信息
    	IDataset attrCommonList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, "0");
    	if (IDataUtil.isNotEmpty(attrCommonList)) {
    		
    		trancAttrDataset(attrCommonList,proParamDataset);
		}
    	//专线成员参数信息
    	IDataset attrList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, recordNum);
    	if (IDataUtil.isNotEmpty(attrList)) {
    		trancAttrDataset(attrList,proParamDataset);
		}
    	paramInfo.put("PRODUCT_PARAM", proParamDataset);
    	IData productInfoData  = WorkformProductBean.qryProductByPk(ibsysid, recordNum);
    	paramInfo.put("PRODUCT_ID", productInfoData.getString("PRODUCT_ID"));
    	paramInfos.add(paramInfo);
    	
    	inparam.put("PRODUCT_PARAM_INFO", paramInfos);
    	inparam.put("PRODUCT_ID", productInfoData.getString("PRODUCT_ID"));
    	inparam.put("SERIAL_NUMBER", productInfoData.getString("SERIAL_NUMBER"));
    	inparam.put("IS_EXPER_DATALINE",productInfoData.getString("RSRV_STR2","1"));//默认不为体验客户
    	
    	//资费服务信息
    	IDataset productElements = new DatasetList();
    	
    	IDataset svcDataset = EopDataTransBean.buildWorkformSvc(subIbsysId, recordNum);
    	setElementInfo(svcDataset, productElements, productInfoData.getString("PRODUCT_ID"));
    	
    	IDataset disDataset = EopDataTransBean.buildWorkformDis(subIbsysId, recordNum);
    	setElementInfo(disDataset, productElements, productInfoData.getString("PRODUCT_ID"));
    	
    	inparam.put("ELEMENT_INFO",productElements);
    	inparam.put(Route.USER_EPARCHY_CODE, epachyCode);
    	inparam.put("EPARCHY_CODE", epachyCode);
    	
		inparam.put("IBSYSID", ibsysid);
		
		IDataset eosdDataset = new DatasetList();
		IData eosData = new DataMap();
		eosData.put("IBSYSID", ibsysid);
		eosData.put("ATTR_CODE", "ESOP");
		eosData.put("FLOW_MAIN_ID", "EDIRECTLINEOPENNEW");
		eosData.put("BPM_TEMPLET_ID", "EDIRECTLINEOPENNEW");
		eosData.put("RECORD_NUM", recordNum );
		eosData.put("ATTR_VALUE", ibsysid);
		eosData.put("RSRV_STR9", recordNum );
		eosData.put("RSRV_STR1", "batOpenMeb" );
		eosData.put("NODE_ID", "batOpenMeb" );
		
		IDataset eweNodeInfo = CSAppCall.call("SS.EweNodeQrySVC.qryEweNodeByIbsysid", inparam);
    	
    	if (IDataUtil.isNotEmpty(eweNodeInfo)) {
    		eosData.put("BUSIFORM_NODE_ID", eweNodeInfo.getData(0).getString("BUSIFORM_NODE_ID"));
    		eosData.put("SUB_IBSYSID", eweNodeInfo.getData(0).getString("SUB_BI_SN","-1"));
		}
		eosdDataset.add(eosData);
		inparam.put("EOS", eosdDataset);
    	
    	IDataset dataset  = CSAppCall.call("SS.BatOpenGrpMemSVC.opengroupmember", inparam);
    	resultData.put("BATCH_ID", dataset.first().getString("BATCH_ID"));
    	resultData.put("RECORD_NUM", recordNum);
    	result.add(resultData);
        
        return result;
    }
    
    public void setElementInfo(IDataset elementInfos, IDataset productElements, String productId) throws Exception {
    	if (IDataUtil.isNotEmpty(elementInfos)) {
    		for (int a = 0; a < elementInfos.size(); a++) {}
    		if (IDataUtil.isNotEmpty(elementInfos)) {
    			for (int b = 0; b < elementInfos.size(); b++) {
					IData data3 = new DataMap();
					data3.put("ELEMENT_ID", elementInfos.getData(b).getString("OFFER_CODE"));
					data3.put("MODIFY_TAG", elementInfos.getData(b).getString("OPER_CODE"));
					String offercha = elementInfos.getData(b).getString("OFFER_CHA");
					if (StringUtils.isNotEmpty(offercha)) {
						data3.put("ATTR_PARAM", setOfferCha(new DatasetList(elementInfos.getData(b).getString("OFFER_CHA"))));
					}else {
						data3.put("ATTR_PARAM", new DatasetList());
					}
					data3.put("END_DATE", elementInfos.getData(b).getString("END_DATE"));
					data3.put("START_DATE", elementInfos.getData(b).getString("START_DATE"));
					data3.put("ELEMENT_TYPE_CODE", elementInfos.getData(b).getString("OFFER_TYPE"));
					data3.put("ELEMENT_NAME", elementInfos.getData(b).getString("OFFER_NAME"));
					data3.put("PRODUCT_ID", productId);
					data3.put("PACKAGE_ID", elementInfos.getData(b).getString("GROUP_ID","-1"));
					productElements.add(data3);
				}
			}
		}
    	
	}

    public IDataset setOfferCha(DatasetList datasetList) throws Exception{
		IDataset paramDataset = new DatasetList();
		if (IDataUtil.isNotEmpty(datasetList)) {
			for (int i = 0; i < datasetList.size(); i++) {
				IData param = new DataMap();
				param.put("ATTR_CODE", datasetList.getData(i).getString("CHA_SPEC_CODE"));
				param.put("ATTR_VALUE", datasetList.getData(i).getString("CHA_VALUE"));
				param.put("RSRV_STR1", datasetList.getData(i).getString("CHA_SPEC_ID"));
				paramDataset.add(param);
			}
		}
		return paramDataset;
	}
    
    
    public IDataset changeMemElement(IData param) throws Exception{
    	IData inparam = new DataMap();
    	IDataset result = new DatasetList();
    	String recordNum = param.getString("RECORD_NUM"); 
    	String ibsysId = param.getString("IBSYSID"); 
        
        IDataset infoData = WorkformNodeBean.qryNodeByIbsysidNodeDesc(ibsysId, "eOpenNewWorkSheet");
        if (IDataUtil.isEmpty(infoData)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID【"+ibsysId+"】,NODE_ID【eOpenNewWorkSheet】查询TF_B_EOP_NODE资料不存在");
		}
        
        String subIbsysId = infoData.getData(0).getString("SUB_IBSYSID");
    	
        IData grpProductDataset =  WorkformProductBean.qryProductByPk(ibsysId, "0");
    	
    	if (IDataUtil.isEmpty(grpProductDataset)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID"+ibsysId+"查询TF_B_EOP_PRODUCT信息不存在！");
		}
    	inparam.put("USER_ID", grpProductDataset.getString("USER_ID", ""));
    	inparam.put("PRODUCT_ID", grpProductDataset.getString("PRODUCT_ID", ""));
    	
    	IData mebProductDataset =  WorkformProductBean.qryProductByPk(ibsysId, recordNum);
    	
    	if (IDataUtil.isEmpty(mebProductDataset)) 
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID"+ibsysId+"查询TF_B_EOP_PRODUCT成员专线信息不存在！");
		}
    	
		IData resultData = new DataMap();
		String productId = mebProductDataset.getString("PRODUCT_ID");
		
		
		IData paramInfo = new DataMap();
    	IDataset paramInfos = new DatasetList();
    	IDataset proParamDataset = new DatasetList();
    	//公用参数信息
    	IDataset attrCommonList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, "0");
    	if (IDataUtil.isNotEmpty(attrCommonList)) {
    		
    		trancAttrDataset(attrCommonList,proParamDataset);
		}
    	//专线成员参数信息
    	IDataset attrList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, recordNum);
    	if (IDataUtil.isNotEmpty(attrList)) {
    		trancAttrDataset(attrList,proParamDataset);
		}
    	paramInfo.put("PRODUCT_PARAM", proParamDataset);
    	paramInfo.put("PRODUCT_ID", productId);
    	paramInfos.add(paramInfo);
    	
    	inparam.put("PRODUCT_PARAM_INFO", paramInfos);
    	
    	//资费服务信息
    	IDataset productElements = new DatasetList();
    	
    	IDataset svcDataset = EopDataTransBean.buildWorkformSvc(subIbsysId, recordNum);
    	setElementInfo(svcDataset, productElements, productId);
    	
    	IDataset disDataset = EopDataTransBean.buildWorkformDis(subIbsysId, recordNum);
    	setElementInfo(disDataset, productElements, productId);
    	
    	inparam.put("ELEMENT_INFO",productElements);
    	inparam.put(Route.USER_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());
		inparam.put("IBSYSID", ibsysId);
		inparam.put("SERIAL_NUMBER", mebProductDataset.getString("SERIAL_NUMBER"));
		
		IDataset eopSubscribe = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
		if (IDataUtil.isEmpty(eopSubscribe)) {
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID"+ibsysId+"查询TF_B_EOP_SUBSCRIBE信息不存在！");
		}
		
		IDataset eosdDataset = new DatasetList();
		IData eosData = new DataMap();
		eosData.put("IBSYSID", ibsysId);
		eosData.put("ATTR_CODE", "ESOP");
		eosData.put("ATTR_VALUE", ibsysId);
		eosData.put("FLOW_MAIN_ID", eopSubscribe.getData(0).getString("BPM_TEMPLET_ID"));
		eosData.put("BPM_TEMPLET_ID", eopSubscribe.getData(0).getString("BPM_TEMPLET_ID"));
		eosData.put("RECORD_NUM", recordNum );
		eosData.put("RSRV_STR9", recordNum );
		eosData.put("RSRV_STR1", "creatCRMOrder" );
		eosData.put("NODE_ID", "creatCRMOrder" );
		
		IData data = new DataMap();
		data.put("BI_SN", ibsysId);
		
		IDataset eweNodeInfo = CSAppCall.call("SS.EweNodeQrySVC.qryEweNodeByIbsysid", data);
    	
    	if (IDataUtil.isNotEmpty(eweNodeInfo)) {
    		eosData.put("BUSIFORM_NODE_ID", eweNodeInfo.getData(0).getString("BUSIFORM_NODE_ID"));
    		eosData.put("SUB_IBSYSID", eweNodeInfo.getData(0).getString("SUB_BI_SN","-1"));
		}
		eosdDataset.add(eosData);
		inparam.put("EOS", eosdDataset);
		IDataset dataset  = CSAppCall.call("SS.BatMemChgSVC.crtGrpMemBat", inparam);
		
		resultData.put("BATCH_ID", dataset.first().getString("BATCH_ID"));
    	resultData.put("RECORD_NUM", recordNum);
    	result.add(resultData);
    	
    	return result;
    }
    
    public void trancAttrDataset(IDataset attrCommonList, IDataset proParamDataset)throws Exception
    {
    	for (int t = 0; t < attrCommonList.size(); t++) {
			IData attrData = attrCommonList.getData(t);
			IData proCommonParamData = new DataMap();
			String attrCode = attrData.getString("ATTR_CODE");
			
			//从配置变里面取数据，进行转换  
			String configkeyValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EsopParamToCrmParamKey", attrCode});
			if(StringUtils.isNotEmpty(configkeyValue))
			{
				String value = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ configkeyValue, attrData.getString("ATTR_VALUE")});
				if (StringUtils.isNotEmpty(value)) {
					attrData.put("ATTR_VALUE", value);
				}else {
					attrData.put("ATTR_VALUE", attrData.getString("ATTR_VALUE"));	
				}
			}
			//将ESOP，超过长度的key、与报文不相符的key,进行转换
			String paramKey = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EsopParamToCrmParam", attrCode});
			if(StringUtils.isNotEmpty(paramKey))
			{
				proCommonParamData.put("ATTR_CODE", paramKey);
				proCommonParamData.put("ATTR_VALUE", attrData.getString("ATTR_VALUE"));
			}else
			{
				proCommonParamData.put("ATTR_CODE", attrData.getString("ATTR_CODE"));
				proCommonParamData.put("ATTR_VALUE", attrData.getString("ATTR_VALUE"));
			}
			proParamDataset.add(proCommonParamData);
		}
    	
    }
    
    public IDataset removeGroupMember(IData param) throws Exception
    {
    	IData inparam = new DataMap();
    	IDataset result = new DatasetList();
    	String recordNum = param.getString("RECORD_NUM"); 
    	String ibsysId = param.getString("IBSYSID"); 
        
        IDataset infoData = WorkformNodeBean.qryNodeByIbsysidNodeDesc(ibsysId, "eApply");
        if (IDataUtil.isEmpty(infoData)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID【"+ibsysId+"】,NODE_ID【eApply】查询TF_B_EOP_NODE资料不存在");
		}
        String subIbsysId = infoData.getData(0).getString("SUB_IBSYSID");
        
        IData mebProductData = WorkformProductBean.qryProductByPk(ibsysId, recordNum);
    	
    	if (IDataUtil.isEmpty(mebProductData)) {
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID"+ibsysId+"查询TF_B_EOP_PRODUCT成员专线信息不存在！");
		}
		IData resultData = new DataMap();
		inparam.put("SERIAL_NUMBER", mebProductData.getString("SERIAL_NUMBER"));
		inparam.put("PRODUCT_ID", mebProductData.getString("PRODUCT_ID"));
		//注销成员信息存放在  RECORD_NUM=0
		IDataset grpAttrDataset = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, "0");
		
		IData data = new DataMap();
    	
    	if (IDataUtil.isNotEmpty(grpAttrDataset)) {
			for (int j = 0; j < grpAttrDataset.size(); j++) {
				
				data.put(grpAttrDataset.getData(j).getString("ATTR_CODE"), grpAttrDataset.getData(j).getString("ATTR_VALUE"));
			}
			
			String reasonCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]{ "cancelReasonCrm", data.getString("backoutCause")});
		
    		inparam.put("REMOVE_REASON", data.getString("backoutCause"));
        	inparam.put("REMOVE_REASON_CODE", reasonCode);
        	inparam.put("REMARK", data.getString("REMARK"));
        	inparam.put(Route.USER_EPARCHY_CODE, CSBizBean.getVisit().getLoginEparchyCode());
        	inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getLoginEparchyCode());
        	
        	IDataset eopSubscribe = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
    		if (IDataUtil.isEmpty(eopSubscribe)) {
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID"+ibsysId+"查询TF_B_EOP_SUBSCRIBE信息不存在！");
    		}
        	
        	IDataset eosdDataset = new DatasetList();
    		IData eosData = new DataMap();
    		eosData.put("IBSYSID", ibsysId);
    		eosData.put("ATTR_CODE", "ESOP");
    		eosData.put("ATTR_VALUE", ibsysId);
    		eosData.put("FLOW_MAIN_ID", eopSubscribe.getData(0).getString("BPM_TEMPLET_ID"));
    		eosData.put("BPM_TEMPLET_ID", eopSubscribe.getData(0).getString("BPM_TEMPLET_ID"));
    		eosData.put("RECORD_NUM", recordNum );
    		eosData.put("RSRV_STR9", recordNum );
    		eosData.put("RSRV_STR1", "creatCRMOrder" );
    		eosData.put("NODE_ID", "creatCRMOrder" );
            eosData.put("RSRV_STR2", "01");
            eosData.put("SUB_IBSYSID", subIbsysId);
    		eosdDataset.add(eosData);
    		
    		inparam.put("EOS", eosdDataset);
        	
        	IDataset dataset  = CSAppCall.call("SS.BatMemCancelSVC.cancleMebBat", inparam);
        	resultData.put("BATCH_ID", dataset.first().getString("BATCH_ID"));
        	resultData.put("RECORD_NUM", recordNum);
    	}
    	result.add(resultData);
    	return result;
    }
}
