
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat;

import java.util.ArrayList;
import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;

public class PlatInfoQry
{

    public static void deleteFetionInfo(String serialNumber, String sysfeinnoId) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("SYSFEINNOID", sysfeinnoId);
        SQLParser parser = new SQLParser(data);
        parser.addSQL("DELETE FROM TF_F_USER_FETIONINFO");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND MOBILE = :SERIAL_NUMBER ");
        parser.addSQL(" and OPTTYPE = '1' and SYSFEINNOID =  :SYSFEINNOID ");
        Dao.executeUpdates(parser);
    }

    /**
     * 获取手机支付属性信息
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getAutoPayContractInfo(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_SVC_ID", param);
    }

    /**
     * 判断用户是否签约了自动交费代扣
     * 
     * @param pd
     *            pageData
     * @param param
     *            user_id service_id
     * @return 已经签约： true ； 未签约：false
     * @throws Exception
     */
    public static boolean getAutoPayContractState(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        IDataset list = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_ID_SVC", param);
        if (list != null && list.size() > 0)
        {
            return true;
        }
        else
            return false;
    }

    public static IDataset getCurrentWlanDiscnt(String userId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        //data.put("EPARCHY_CODE", eparchyCode);

        //return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WLAN_BY_USERID_NOW", data);
        //1. 查询出用户discnt，并反查出product_id
        IDataset userDiscntList = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WLAN_BY_USERID_NOW", data);
        
        //2. 查找commpara表155配置
        IDataset comminfoList = ParamInfoQry.getCommparaByCode("CSM", "155", "", eparchyCode);
        //3. PRODUCT_ID 与para_code1比较
        
        IDataset result = new DatasetList();
        if(ArrayUtil.isNotEmpty(userDiscntList) && ArrayUtil.isNotEmpty(comminfoList))
        {
        	FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscntList, null, null);// 填充productId和packageId
        	for(int i = 0 ; i < userDiscntList.size() ; i++)
        	{
        		IData userDiscnt = userDiscntList.getData(i);
        		String productId = userDiscnt.getString("PRODUCT_ID");
        		for(int k = 0 ; k < comminfoList.size() ; k++)
        		{
        			IData comminfo = comminfoList.getData(k);
        			String paraCode1 = comminfo.getString("PARA_CODE1");
        			if(StringUtils.equals(productId, paraCode1))
        			{
        				IData temp = new DataMap();
        				temp.put("PARTITION_ID", userDiscnt.getString("PARTITION_ID"));
        				temp.put("USER_ID", userDiscnt.getString("USER_ID"));
        				temp.put("USER_ID_A", userDiscnt.getString("USER_ID_A"));
        				temp.put("DISCNT_CODE", userDiscnt.getString("DISCNT_CODE"));
        				temp.put("SPEC_TAG", userDiscnt.getString("SPEC_TAG"));
        				temp.put("RELATION_TYPE_CODE", userDiscnt.getString("RELATION_TYPE_CODE"));
        				temp.put("START_DATE", userDiscnt.getString("START_DATE"));
        				temp.put("END_DATE", userDiscnt.getString("END_DATE"));
        				result.add(temp);
        			}
        		}
        	}
        }
        return result;
    }

    /**
     * @param packageCode
     * @return
     * @throws Exception
     */
    public static String getEntityCardLimitCount(String packageCode) throws Exception
    {
        String limitCount = "1";
        IData param = new DataMap();
        if("".equals(packageCode) || packageCode == null){
        	return limitCount;
        }
        param.put("PACKAGE_CODE", packageCode);
        IDataset list = Dao.qryByCode("TD_B_PLATSVC", "SEL_ENTITYCARD_LIMITCOUNT", param, Route.CONN_CRM_CEN);
        if (list != null && !list.isEmpty())
        {
            limitCount = list.getData(0).getString("LIMIT_COUNT");
        }

        return limitCount;
    }

    public static IDataset getExcludeEntityCardService(String serviceId) throws Exception
    {
    	IDataset result = new DatasetList();
        IData param = new DataMap();
        /*param.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_ENTITYCARD_EXCLUDE", param, Route.CONN_CRM_CEN);*/
        
        //1. 根据serviceId查询td_B_platsvc表，然后取出sp_code，biz_code
        IDataset dataset = new DatasetList();
        try{
        	dataset = UpcCall.querySpServiceAndProdByCond(null, null, null, serviceId);
        }catch(Exception e){
        	
        }
        
        for(int i = 0 ; i < dataset.size() ; i ++)
        {
        	IData data = dataset.getData(i);
        	String spCode = data.getString("SP_CODE");
        	String bizCode = data.getString("BIZ_CODE");
        	param.clear();
        	param.put("SP_CODE", spCode);
        	param.put("BIZ_CODE", bizCode);
        	
        	result = Dao.qryByCodeParser("TD_B_PLATSVC", "SEL_BY_ENTITYCARD_EXCLUDE", param, Route.CONN_CRM_CEN);
        	
        	for(int j = i ; j < result.size() ; j ++)
        	{
        		IData temp = result.getData(j);
        		
        		temp.put("EX_SERVICE_ID", serviceId);
        		temp.put("EX_SP_CODE", spCode);
        		temp.put("EX_BIZ_CODE", bizCode);
        		temp.put("SERVICE_NAME", data.getString("OFFER_NAME"));
        	}
        }
        return result;
    }

    /**
     * 查询平台业务的绑定优惠配置
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getPlatDiscntConfig(String eparchyCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCodeParser("TD_B_PLATSVC_PARAM", "SEL_BY_PLATDISCNT_CONFIG", param, page);
    }

    /**
     * 查询平台业务的绑定优惠配置 根据属性和属性值查
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getPlatDiscntConfigByServiceId(String eparchyCode, String serviceId, String attrCode, String attrValue) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("PARAM_CODE", serviceId);
        param.put("PARA_CODE1", attrValue);
        param.put("PARA_CODE3", attrCode);
        return Dao.qryByCodeParser("TD_B_PLATSVC_PARAM", "SEL_BY_PLATDISCNT_CONFIG", param);
    }
    
    public static IDataset getPlatDiscntConfigByRelateValue(String eparchyCode,String relateValue) throws Exception
    {
    	  IData param = new DataMap();
          param.put("EPARCHY_CODE", eparchyCode);
          param.put("PARA_CODE4", relateValue);
          return Dao.qryByCodeParser("TD_B_PLATSVC_PARAM", "SEL_BY_PLATDISCNT_CONFIG", param);
    }

    public static IDataset getPlatsvcCustSvc(String serviceId) throws Exception
    {
        IData param = new DataMap();
//        param.put("SERVICE_ID", serviceId);
//        return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SERVICEID_CUSTSVC", param);
        IDataset dataset = new DatasetList();
        try{
        	 dataset = UpcCall.getPlatsvcCustSvc(serviceId);
        }catch(Exception e){
        	
        }
        return dataset;
    }
    /**
	 * 根据tab_name,sql_ref,eparchy_code查询用户服务信息
	 */
	public static IDataset getUserSvc(IData inparams) throws Exception {

		String eparchyCode = inparams.getString("EPARCHY_CODE");
		String sqlref = inparams.getString("SQL_REF");

		return Dao.qryByCode("TF_F_USER_SVC", sqlref, inparams);
	}
	
	/**
	 * 根据tab_name,sql_ref,eparchy_code查询用户服务信息
	 */
	public static IDataset getUserOcsInfo(IData inparams) throws Exception {
		String eparchyCode = inparams.getString("EPARCHY_CODE");
		String sqlref = inparams.getString("SQL_REF");

		return Dao.qryByCode("TF_F_USER_OCS", sqlref, inparams);
	}


	/**
	 * 根据tab_name,sql_ref,eparchy_code查询用户UU关系
	 */
	public static IDataset getUserUURelation(IData inparams) throws Exception {

		String eparchyCode = inparams.getString("EPARCHY_CODE");

		String sqlref = inparams.getString("SQL_REF");

		return Dao.qryByCode("TF_F_RELATION_UU", sqlref, inparams);
	}
	
    /**
     * 查询用户的依赖互斥配置
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getPlatsvcLimit(String bizTypeCode, String serviceId, String operCode) throws Exception
    {
        /*IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("OPER_CODE", operCode);
        return Dao.qryByCode("TD_B_PLATSVC_LIMIT", "SEL_LIMITSVC_BY_SID", param, Route.CONN_CRM_CEN);*/
    	IDataset relaConfigs = new DatasetList();
    	try{
    		relaConfigs = UpcCall.qrySpRelByAnyOfferIdBizTypeCodeOperCode(serviceId, "Z", bizTypeCode, operCode);
    	}catch(Exception e){
    		
    	}
        
        for(int i = 0 ; i < relaConfigs.size() ; i ++)
        {
        	IData relaConfig = relaConfigs.getData(i);
        	
        	String limitType = relaConfig.getString("REL_TYPE");

    		String relaState = relaConfig.getString("REL_FUNC_STATUS", "");
            
            String flag = relaConfig.getString("UNSUBSCRIBE_RELATED", ""); // TRUE时，不管是PACKAGE_ID为多少，都连带退订，如果非TURE，则PACKAGE_ID为50000000才退订
            String pfTag = relaConfig.getString("INTF_MODE", "0"); // 是否发指令，此处可能需要修改配置表TD_B_PLATSVC_LIMIT，表中原配置为01，发指令
            String infoCode = relaConfig.getString("MEMBER_ATTR", ""); // 需要匹配属性名
            String infoValue = relaConfig.getString("MEMBER_TYPE", "");// 需要匹配属性值
            String attrCode = relaConfig.getString("SUBSCRIBE_RELATED", ""); // 属性名 连带开服务或者平台服务，需要加的属性名
            String attrValue = relaConfig.getString("SUBSCRIBE_RELATED_VAL", ""); // 属性值 连带开服务或者平台服务，需要加的属性值
           
            
            relaConfig.put("SERVICE_ID_L", relaConfig.getString("REL_OFFER_CODE"));
            relaConfig.put("SVC_TYPE", relaConfig.getString("REL_OFFER_TYPE"));
            
            relaConfig.put("SVC_STATE", relaState);
            relaConfig.put("LIMIT_TYPE", limitType);
            relaConfig.put("RSRV_STR1", flag);
            relaConfig.put("RSRV_STR2", pfTag);
            relaConfig.put("RSRV_STR3", infoCode);
            relaConfig.put("RSRV_STR4", infoValue);
            relaConfig.put("RSRV_STR5", attrCode);
            relaConfig.put("RSRV_STR6", attrValue);
            
        }
        return relaConfigs;
    }

    /**
     * 查询是否走服务开通的配置
     * 
     * @param pd
     * @param serviceId
     * @param orgDomain
     * @param bizTypeCode
     * @param operCode
     * @param oprSource
     * @return
     * @throws Exception
     */
    public static IDataset getPlatSvcPfRule(String serviceId, String orgDomain, String bizTypeCode, String operCode, String oprSource, String inModeCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("ORG_DOMAIN", orgDomain);
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("OPER_CODE", operCode);
        param.put("OPR_SOURCE", oprSource);
        return Dao.qryByCode("TD_B_PLATSVC_PFRULE", "SEL_PF_RULE", param, page);
    }

    /**
     * @Function: getPlatSvcPintInfos
     * @Description: /数据平台业务打印 hank
     */
    public static IDataset getPlatSvcPintInfos(String tradeId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SVCID_PRINT", inParam);
    }

    /**
     * 查询平台业务统一付费服务配置
     * 
     * @param eparchyCode
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getPlatUinifiedFeeDiscntConfig(String eparchyCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_PLATSVC_PARAM", "SEL_BY_PLAT_UINIFIEDFEE_CONFIG", param, page, Route.CONN_CRM_CEN);
    }

    public static List getPrivList(String param)
    {
        List RSRV_STR3_List = new ArrayList();
        if (param.indexOf("PLAT_DATARIGHT_A") > 0)
        {// 是否有　平台＿业务权限A：10位的串的第一位为1
            RSRV_STR3_List.add("1_________");
        }
        if (param.indexOf("PLAT_DATARIGHT_B") > 0)
        {// 是否有　平台＿业务权限B：10位的串的第二位为1,
            RSRV_STR3_List.add("_1________");
        }
        if (param.indexOf("PLAT_DATARIGHT_C") > 0)
        {// 是否有　平台＿业务权限C：10位的串的第三位为1，
            RSRV_STR3_List.add("__1_______");
        }
        if (param.indexOf("PLAT_DATARIGHT_D") > 0)
        {// 是否有　平台＿业务权限D：10位的串的第四二位为1,
            RSRV_STR3_List.add("___1______");
        }
        if (param.indexOf("PLAT_DATARIGHT_E") > 0)
        {// 是否有　平台＿业务权限E：10位的串的第五位为1，
            RSRV_STR3_List.add("____1_____");
        }
        if (param.indexOf("PLAT_DATARIGHT_F") > 0)
        {// 是否有　平台＿业务权限F：10位的串的第六位为1,
            RSRV_STR3_List.add("_____1____");
        }
        if (param.indexOf("PLAT_DATARIGHT_G") > 0)
        {// 是否有　平台＿业务权限G：10位的串的第七位为1，
            RSRV_STR3_List.add("______1___");
        }
        if (param.indexOf("PLAT_DATARIGHT_H") > 0)
        {// 是否有　平台＿业务权限H：10位的串的第八位为1,
            RSRV_STR3_List.add("_______1__");
        }
        if (param.indexOf("PLAT_DATARIGHT_I") > 0)
        {// 是否有　平台＿业务权限I：10位的串的第九位为1，
            RSRV_STR3_List.add("________1_");
        }
        if (param.indexOf("PLAT_DATARIGHT_J") > 0)
        {// 是否有　平台＿业务权限J：10位的串的第十位为1
            RSRV_STR3_List.add("_________1");
        }
        return RSRV_STR3_List;
    }

    public static String getRoleIdbOneCN(String userIdB, String roleCodeB) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_B", userIdB);
        param.put("ROLE_CODE_B", roleCodeB);
        IDataset list = Dao.qryByCode("TF_F_RELATION_UU", "SEL_ROLE_IDB_OneCN", param);
        if (list != null && list.size() > 0)
        {
            return list.getData(0).getString("ROLE_CODE_B");
        }
        else
            return "1";
    }

    /**
     * @author luoz
     * @date 2013-08-20
     * @description 查询业务信息
     * @param pd
     * @param param
     * @param isCancel
     * @return
     * @throws Exception
     */
    public static IDataset getSpBizInfo(String serviceId, String bizTypeCode, String bizCode, String spCode, boolean isCancel) throws Exception
    {
        IData param = new DataMap();
        if (serviceId != null || !"".equals(serviceId))
        {
            param.put("SERVICE_ID", serviceId);
        }
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("BIZ_CODE", bizCode);
        param.put("SP_CODE", spCode);

        if (!isCancel)
        {
            return Dao.qryByCodeParser("TD_B_PLATSVC", "SEL_SP_BIZ_INFO_A", param);
        }
        else
        {
            return Dao.qryByCodeParser("TD_B_PLATSVC", "SEL_SP_BIZ_INFO", param);
        }
    }

    /**
     * 平台服务配置参数表
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getSPInfo0(IData inparams) throws Exception
    {
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_SPBIZCODE_0", inparams);
    }

    /**
     * 平台服务配置参数表
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getSPInfo1(IData inparams) throws Exception
    {
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_SPBIZCODE_1", inparams);
    }

    /**
     * 平台服务配置参数表
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getSPInfo2(IData inparams) throws Exception
    {
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_SPBIZCODE_2", inparams);
    }

    /**
     * 平台服务配置参数表
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getSPInfo3(IData inparams) throws Exception
    {
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_SPBIZCODE_3", inparams);
    }

    /**
     * 查询用户飞信支付情况
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getUserFetionInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_FETIONINFO", "SEL_FETIONINFO_BYSN", param);
    }

    /**
     * 根据手机号码查询最近开户的纪录
     * 
     * @return
     * @throws Exception
     */
    public static IDataset getUserInfoBySNOpenDate(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_SNOPENDATE", param);
    }

    /**
     * 查询用户主体服务状态
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getUserMainState(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_USER_VALID_MAINSVCSTATE", param);
    }

    /**
     * 根据tab_name,sql_ref,eparchy_code查询用户平台服务信息 by lusw
     */
    public static IDataset getUserPlatSvc(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_BIZCODE", inparams);
    }

    /**
     * 根据tab_name,sql_ref,eparchy_code查询用户平台服务信息 by huanghui
     */
    public static IDataset getUserPlatSvc(IData params, String sqlRef) throws Exception
    {
    	String bizTypeCode = params.getString("BIZ_TYPE_CODE");
    	String userId = params.getString("USER_ID");
    	
    	IData input = new DataMap();
    	input.put("USER_ID", userId);
    	
    	IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", sqlRef, input);
    	
    	return userPlatSvcs;
    }
    
    /**
     * 根据tab_name,sql_ref,eparchy_code查询用户平台服务信息 add by guyan
     */
    public static IDataset getUserPlatSvcAndBizTypeCode(IData params, String sqlRef) throws Exception
    {
    	String bizTypeCode = params.getString("BIZ_TYPE_CODE");
    	String userId = params.getString("USER_ID");
    	
    	IData input = new DataMap();
    	input.put("USER_ID", userId);
    	
    	IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC", sqlRef, input);
    	
    	IDataset result = new DatasetList();
    	for(int i = 0 ; i < userPlatSvcs.size() ; i ++)
    	{
    		IData userPlatSvc = userPlatSvcs.getData(i);
    		String serviceId = userPlatSvc.getString("SERVICE_ID");
    		IDataset upcDatas = new DatasetList();
    		try{
    			upcDatas = UpcCall.querySpServiceAndProdByCond(null ,null ,bizTypeCode, serviceId);
    		}catch(Exception e)
    		{
    			
    		}
    		
    		if(null != upcDatas && upcDatas.size() > 0)
    		{
    			result.add(userPlatSvc);
    		}
    	}
    	return result;
    }

	 /**
    * 查询用户是否存在某产品
    */
	public static IDataset getUserChangeInfo(IData params, String sqlRef) throws Exception {
	   return Dao.qryByCode("TF_F_USER_INFOCHANGE",sqlRef, params);
	}
	
    /**
     * 根据tab_name,sql_ref,eparchy_code查询用户平台服务信息 by lusw
     */
    public static IDataset getUserPlatSvc1(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_RESERVEORDER_E", inparams);

    }

    public static IDataset queryElecCardSaleList(String serialNumber, String operType, String state, String wlanCardSeq) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("OPR_TYPE", operType);
        param.put("STATE", state);
        param.put("WLAN_CARD_SEQ", wlanCardSeq);
        IDataset feeCardLogList = Dao.qryByCode("TF_B_WLAN_FEE_CARD_LOG", "SEL_WLAN_EFFECT_CARD", param, Route.CONN_CRM_CEN);
        return feeCardLogList;
    }

    public static IDataset queryFetionInfo(String serialNumber, String sysfeinnoId) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("SYSFEINNOID", sysfeinnoId);
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT * FROM TF_F_USER_FETIONINFO");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND MOBILE = :SERIAL_NUMBER ");
        parser.addSQL(" and OPTTYPE = '1' and SYSFEINNOID =  :SYSFEINNOID ");
        return Dao.qryByParse(parser);
    }

    public static IDataset queryFetionLimit(String serviceIdl, String operCode) throws Exception
    {
        //return Dao.qryByCode("TD_B_PLATSVC_LIMIT", "SEL_CHILD_SERVICE_BYSID", param, Route.CONN_CRM_CEN);
        /*AND T.BIZ_TYPE_CODE='23'
        	AND T.LIMIT_TYPE='0'*/
        return UpcCall.qryOfferByRelOfferIdAndBizTypeCodeAndAndRelTypeAndOperCode(serviceIdl, BofConst.ELEMENT_TYPE_CODE_PLATSVC, operCode,
        		"23", "0");
    }

    public static IDataset queryMobilePayLog(String serialNumber, String exSeq) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("BOSS_SEQ", exSeq);
        SQLParser parser = new SQLParser(data);
        parser.addSQL("select * from tf_b_mobilepaylog　");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND BOSS_SEQ = :BOSS_SEQ ");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据SP_CODE查询平台服务的信息
     * 
     * @param pd
     * @param staff_id
     * @return
     * @throws Exception
     */
    public static IDataset queryPlatSVCInfoBySPCode(String spCode) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("SP_CODE", spCode);
        return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_SPSERVICEINFO", inParam);
    }

    public static IDataset queryPlatSVCInfoBySPCode(String spCode, String bizCode) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("SP_CODE", spCode);
        inParam.put("BIZ_CODE", bizCode);
        //return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_SPSERVICEINFO", inParam, Route.CONN_CRM_CEN);
        IDataset upcDatas = UpcCall.querySpServiceBySpCodeAndBizCodeAndBizStateCode(spCode, bizCode);
        IDataset resultList = new DatasetList();
		if(ArrayUtil.isNotEmpty(upcDatas))
		{
			for(int k = 0 ; k < upcDatas.size() ; k++)
			{
				IData upcData = upcDatas.getData(k);
				String offerCode = upcData.getString("OFFER_CODE");
    			String bizTypeCode = upcData.getString("BIZ_TYPE_CODE");
    			IDataset platSvcs = new DatasetList();
    			try{
    				platSvcs = UpcCall.queryPlatSvc(offerCode, BofConst.ELEMENT_TYPE_CODE_PLATSVC, bizTypeCode, "A");	
    			}catch(Exception e){
    				
    			}
    			
    			if(ArrayUtil.isNotEmpty(platSvcs))
    			{
    				IData platSvcConfig = platSvcs.getData(0);
    				
    				IData temp = new DataMap();
    				temp.put("SERVICE_ID", offerCode);
    				temp.put("SP_ID", spCode);
    				temp.put("SP_NAME", upcData.getString("OFFER_NAME"));
    				temp.put("SP_NAME_EN", "");
    				temp.put("SP_SVC_ID", platSvcConfig.getString("BIZ_CODE"));
    				temp.put("BIZ_TYPE", platSvcConfig.getString("BIZ_NAME"));
    				temp.put("BIZ_TYPE_CODE", platSvcConfig.getString("BIZ_TYPE_CODE"));
    				temp.put("BIZ_DESC", platSvcConfig.getString("BIZ_DESC"));
    				temp.put("ACCESS_MODE", platSvcConfig.getString("ACCESS_MODE")); 
    				temp.put("PRICE", platSvcConfig.getString("PRICE"));
    				temp.put("BILLING_TYPE", platSvcConfig.getString("BILL_TYPE"));
    				temp.put("BIZ_STATUS", platSvcConfig.getString("BIZ_STATUS"));
    				temp.put("PROV_ADDR", platSvcConfig.getString("PROV_ADDR"));
    				temp.put("PROV_PORT", platSvcConfig.getString("PROV_PORT"));
    				temp.put("USAGE_DESC", platSvcConfig.getString("USAGE_DESC"));
    				temp.put("INTRO_URL", platSvcConfig.getString("INTRO_URL"));
    				temp.put("FOREGIFT_TYPE", platSvcConfig.getString("FOREGIFT_TYPE"));
    				temp.put("FOREGIFT", platSvcConfig.getString("FOREGIFT"));
    				temp.put("RSRV_STR1", platSvcConfig.getString("RSRV_STR1"));
    				temp.put("RSRV_STR2", platSvcConfig.getString("RSRV_STR2"));
    				temp.put("RSRV_STR3", platSvcConfig.getString("RSRV_STR3"));
    				temp.put("RSRV_STR4", platSvcConfig.getString("RSRV_STR4"));
    				temp.put("RSRV_STR5", platSvcConfig.getString("RSRV_STR5"));
    				temp.put("REMARK", platSvcConfig.getString("REMARK"));
    				temp.put("UPDATE_STAFF_ID", platSvcConfig.getString("OP_ID"));
    				temp.put("UPDATE_DEPART_ID", platSvcConfig.getString("ORG_ID"));
    				temp.put("UPDATE_TIME", platSvcConfig.getString("DONE_DATE")); 
    				
    				resultList.add(temp);
    			}
			}
		}
		return resultList;
    }

    public static IDataset queryPlatSVCInfoByStaffSPServiceInfo(String spCode, String staffId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("SP_CODE", spCode);
        inParam.put("STAFF_ID", staffId);
        IDataset staffPlats = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_STAFFSPSERVICEINFO_NEW", inParam, Route.CONN_CRM_CEN);
        IDataset resultList = new DatasetList();
        if(ArrayUtil.isNotEmpty(staffPlats))
        {
        	for(int i = 0 ; i < staffPlats.size() ; i++)
        	{
        		IData staffPlat = staffPlats.getData(i);
        		String bizCode = staffPlat.getString("BIZ_CODE");
        		IDataset upcDatas = UpcCall.querySpServiceBySpCodeAndBizCodeAndBizStateCode(spCode, bizCode);
        		if(ArrayUtil.isNotEmpty(upcDatas))
        		{
        			for(int k = 0 ; k < upcDatas.size() ; k++)
        			{
        				IData upcData = upcDatas.getData(k);
        				String offerCode = upcData.getString("OFFER_CODE");
            			String bizTypeCode = upcData.getString("BIZ_TYPE_CODE");
            			IDataset platSvcs = new DatasetList();
            			try{
            				platSvcs = UpcCall.queryPlatSvc(offerCode, BofConst.ELEMENT_TYPE_CODE_PLATSVC, bizTypeCode, "A");
            			}catch(Exception e){
            				
            			}
            			
            			if(ArrayUtil.isNotEmpty(platSvcs))
            			{
            				IData platSvcConfig = platSvcs.getData(0);
            				
            				IData temp = new DataMap();
            				temp.put("SERVICE_ID", offerCode);
            				temp.put("SP_ID", spCode);
            				temp.put("SP_NAME", upcData.getString("OFFER_NAME"));
            				temp.put("SP_NAME_EN", "");
            				temp.put("SP_SVC_ID", platSvcConfig.getString("BIZ_CODE"));
            				temp.put("BIZ_TYPE", platSvcConfig.getString("BIZ_NAME"));
            				temp.put("BIZ_TYPE_CODE", platSvcConfig.getString("BIZ_TYPE_CODE"));
            				temp.put("BIZ_DESC", platSvcConfig.getString("BIZ_DESC"));
            				temp.put("ACCESS_MODE", platSvcConfig.getString("ACCESS_MODE")); 
            				temp.put("PRICE", platSvcConfig.getString("PRICE"));
            				temp.put("BILLING_TYPE", platSvcConfig.getString("BILL_TYPE"));
            				temp.put("BIZ_STATUS", platSvcConfig.getString("BIZ_STATUS"));
            				temp.put("PROV_ADDR", platSvcConfig.getString("PROV_ADDR"));
            				temp.put("PROV_PORT", platSvcConfig.getString("PROV_PORT"));
            				temp.put("USAGE_DESC", platSvcConfig.getString("USAGE_DESC"));
            				temp.put("INTRO_URL", platSvcConfig.getString("INTRO_URL"));
            				temp.put("FOREGIFT_TYPE", platSvcConfig.getString("FOREGIFT_TYPE"));
            				temp.put("FOREGIFT", platSvcConfig.getString("FOREGIFT"));
            				temp.put("RSRV_STR1", platSvcConfig.getString("RSRV_STR1"));
            				temp.put("RSRV_STR2", platSvcConfig.getString("RSRV_STR2"));
            				temp.put("RSRV_STR3", platSvcConfig.getString("RSRV_STR3"));
            				temp.put("RSRV_STR4", platSvcConfig.getString("RSRV_STR4"));
            				temp.put("RSRV_STR5", platSvcConfig.getString("RSRV_STR5"));
            				temp.put("REMARK", platSvcConfig.getString("REMARK"));
            				temp.put("UPDATE_STAFF_ID", platSvcConfig.getString("OP_ID"));
            				temp.put("UPDATE_DEPART_ID", platSvcConfig.getString("ORG_ID"));
            				temp.put("UPDATE_TIME", platSvcConfig.getString("DONE_DATE")); 
            				
            				resultList.add(temp);
            			}
        			}
        		}
        	}
        }
        return resultList;
    }

    public static IDataset queryRelaLimitPlatSvc_L(String serviceId_l, String operCode, String bizTypeCode, String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID_L", serviceId_l);
        param.put("OPER_CODE", operCode);
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TD_B_PLATSVC_LIMIT", "SEL_RELALIMIT_BYS_L_ID", param);
    }
    
    public static IDataset queryRelaLimitPlatSvcs(String serviceId, String operCode, String bizTypeCode) throws Exception
    {
        //return Dao.qryByCode("TD_B_PLATSVC_LIMIT", "SEL_RELALIMIT_BYSID", param, Route.CONN_CRM_CEN);
    	IDataset relaConfigs = new DatasetList();
    	try{
    		relaConfigs = UpcCall.qrySpRelByAnyOfferIdBizTypeCodeOperCode(serviceId, "Z", bizTypeCode, operCode);
    	}catch(Exception e){
    		
    	}
        IDataset resultList = new DatasetList();
        for(int i = 0 ; i < relaConfigs.size() ; i ++)
        {
        	IData relaConfig = relaConfigs.getData(i);
        	
        	String limitType = relaConfig.getString("REL_TYPE");
        	if(StringUtils.equals("3", limitType) || StringUtils.equals("4", limitType))
        	{
        		String relaState = relaConfig.getString("REL_FUNC_STATUS", "");
                
                String flag = relaConfig.getString("UNSUBSCRIBE_RELATED", ""); // TRUE时，不管是PACKAGE_ID为多少，都连带退订，如果非TURE，则PACKAGE_ID为50000000才退订
                String pfTag = relaConfig.getString("INTF_MODE", "0"); // 是否发指令，此处可能需要修改配置表TD_B_PLATSVC_LIMIT，表中原配置为01，发指令
                String infoCode = relaConfig.getString("MEMBER_ATTR", ""); // 需要匹配属性名
                String infoValue = relaConfig.getString("MEMBER_TYPE", "");// 需要匹配属性值
                String attrCode = relaConfig.getString("SUBSCRIBE_RELATED", ""); // 属性名 连带开服务或者平台服务，需要加的属性名
                String attrValue = relaConfig.getString("SUBSCRIBE_RELATED_VAL", ""); // 属性值 连带开服务或者平台服务，需要加的属性值
               
                
                relaConfig.put("SERVICE_ID_L", relaConfig.getString("REL_OFFER_CODE"));
                relaConfig.put("SVC_TYPE", relaConfig.getString("REL_OFFER_TYPE"));
                
                relaConfig.put("SVC_STATE", relaState);
                relaConfig.put("LIMIT_TYPE", limitType);
                relaConfig.put("RSRV_STR1", flag);
                relaConfig.put("RSRV_STR2", pfTag);
                relaConfig.put("RSRV_STR3", infoCode);
                relaConfig.put("RSRV_STR4", infoValue);
                relaConfig.put("RSRV_STR5", attrCode);
                relaConfig.put("RSRV_STR6", attrValue);
                
                resultList.add(relaConfig);
        	}
        	
        }
        return resultList;
        
    }

    public static IDataset queryScoreExchangePlatLimit(String serviceId, String bizTypeCode, String operCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("OPER_CODE", operCode);

        return Dao.qryByCode("TD_B_PLATSVC_LIMIT", "SEL_BY_SCORE_SVC_L", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询手机支付账户对账记录
     * 
     * @param startDate
     * @param endDate
     * @param reconState
     * @param cancelFlag
     * @return
     * @throws Exception
     */
    public static IDataset querySmallPaymentCheckUpResult(String startDate, String endDate, String reconState, String cancelFlag, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("START_TIME", startDate);
        param.put("END_TIME", endDate);
        param.put("RECON_STATE", reconState);
        param.put("CANCEL_FLAG", cancelFlag);
        IDataset resultList = Dao.qryByCodeParser("TI_B_SMALL_PAYMENT_LOG_RESULT", "SEL_SMALL_PAYMENT_LOG_RESULT", param, page);

        return resultList;
    }

    public static IDataset querySmallPaymentLog(String startDate, String endDate, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("START_TIME", startDate);
        param.put("END_TIME", endDate);
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset resultList = Dao.qryByCodeParser("TF_B_SMALL_PAYMENT_LOG", "SEL_SMALL_PAYMENT_LOG", param, Route.CONN_CRM_CEN);

        return resultList;
    }

    /**
     * 根据操作员ID查询角色
     * 
     * @param pd
     * @param staff_id
     * @return
     * @throws Exception
     */
    public static IDataset queryStaffRole(String staff_id) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staff_id);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select data_code from tf_m_roledataright where role_code in (select data_code from tf_m_staffdataright where staff_id = :STAFF_ID)");

        return Dao.qryByParse(parser, Route.CONN_SYS);
    }

    public static IDataset selWlanFeeCardByOperDate(String serialNumber, String state) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("STATE", state);
        IDataset infos = Dao.qryByCodeParser("TF_B_WLAN_FEE_CARD_LOG", "SEL_WLAN_EFFECT_CARD_BY_OPR_DATE", param, Route.CONN_CRM_CEN);
        return infos;
    }

    public static IDataset selWlanFeeCardByPK(String seqId) throws Exception
    {
        IData param = new DataMap();
        param.put("SEQ_ID", seqId);
        IDataset infos = Dao.qryByCode("TF_B_WLAN_FEE_CARD_LOG", "SEL_WLAN_FEECARD_BY_PK", param, Route.CONN_CRM_CEN);
        return infos;
    }

    public static void updateMobilePayLog(String serialNumber, String recvTime, String recvDepartId, String recvStaffId, String recvCityCode, String recvEparchyCode, String exSeq) throws Exception
    {
        IData param = new DataMap();
        param.put("RECV_TIME", recvTime);
        param.put("RECV_DEPART_ID", recvDepartId);
        param.put("RECV_STAFF_ID", recvStaffId);
        param.put("RECV_CITY_CODE", recvCityCode);
        param.put("RECV_EPARCHY_CODE", recvEparchyCode);
        param.put("BOSS_SEQ", exSeq);
        param.put("SERIAL_NUMBER", serialNumber);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("UPDATE tf_b_mobilepaylog SET cancel_tag = '1'　");
        parser.addSQL(" , CANCEL_TIME = to_date( :RECV_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL(" , CANCEL_DEPART_ID= :RECV_DEPART_ID ");
        parser.addSQL(" , CANCEL_STAFF_ID = :RECV_STAFF_ID ");
        parser.addSQL(" , CANCEL_CITY_CODE = :RECV_CITY_CODE ");
        parser.addSQL(" , CANCEL_EPARCHY_CODE = :RECV_EPARCHY_CODE ");
        parser.addSQL("  WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("  AND BOSS_SEQ = :BOSS_SEQ ");

        Dao.executeUpdate(parser);

    }
    
	/**
	 * 根据tab_name,sql_ref,eparchy_code查询平台服务信息
	 */
	public static IDataset getBizInfo(IData inparams) throws Exception {
//	    IDataset infos = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SP_BIZ_CODE", inparams);
		IDataset infos = UpcCall.querySpServiceBySpCodeAndBizCodeAndBizStateCode(inparams.getString("SP_CODE",""), inparams.getString("BIZ_CODE",""));
        return infos;
	}
	
	/**
	 * 根据serviceid查询平台服务信息
	 */
	public static IDataset getServiceInfo(IData inparams) throws Exception {
//	    IDataset infos = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SERVICE_ID_NEW", inparams);
		IDataset infos = new DatasetList();
		IData data = UPlatSvcInfoQry.qryServInfoBySvcId(inparams.getString("SERVICE_ID",""));
		infos.add(data);
        return infos;
	}
	
	/**
	 * 订购名校资源时查询是否订购校讯通套餐
	 */
	 public static IDataset queryEliteResourcesLimit(String userId) throws Exception
	    {
	        IData param = new DataMap();
	        param.put("USER_ID", userId);

	        return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_ELITE_RESOURCES", param);
	    }
	 
	 public static IDataset queryMiguCampInfoByServiceId(String memberType, String offerId, String startDate) throws Exception 
	 {
			return UpcCall.queryMiguCampInfoByServiceId(memberType, offerId, startDate);
	 }
	 
	 /**
	     * 获取 本月失效的平台服务数据
	     * @param userId
	     * @param firstDateThisMonth
	     * @param lastDateThisMonth
	     * @param routeEparchyCode
	     * @return
	     * @throws Exception
	     */
	    public static IDataset queryPlatSvcEstateThisMonth(String userId,String serviceId, String firstDateThisMonth , String lastDateThisMonth,String routeEparchyCode)throws Exception {
	    	IData param = new DataMap();
			param.put("USER_ID", userId);
			param.put("SERVICE_ID", serviceId);
			param.put("FIRST_DATE_THIS_MONTH", firstDateThisMonth);
			param.put("LAST_DATE_THIS_MONTH", lastDateThisMonth);
			return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATSVC_ESTATE_THIS_MONTH", param, routeEparchyCode);
	    }

	    /**
	     * 根据营销活动ID查询营销活动
	     * @param campaignId
	     * @param startDate
	     * @return
	     * @throws Exception
	     */
	    public static IDataset queryCampaignInfoByCampaignId(String campaignId,String startDate) throws Exception
		 {
	    	return UpcCall.queryCampaignInfoByCampaignId(campaignId, startDate);
		 }
		 /**
		 * 根据sp_code查询表
		 * @param bindServiceId
		 * @return
		 * @throws Exception
		 */
		public static  IDataset getPlatSvcBySpCode(String service_id) throws Exception
		{
			
			IData iData = new DataMap();
			iData.put("OFFER_CODE", service_id);
			return Dao.qryByCode("TL_B_PLATSVC_STOP_MNG", "SELECT_BY_OFFER_CODE", iData);
		}
}
