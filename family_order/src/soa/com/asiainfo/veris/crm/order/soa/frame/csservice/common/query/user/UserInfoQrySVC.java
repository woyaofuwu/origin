
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.RelationUUQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.DepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.auth.TradeInfoBean;

public class UserInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据CUST_ID查询所有在网用户 chenyi
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getAllNormalUserInfoByCustId(IData inparams) throws Exception
    {
        String cust_id = inparams.getString("CUST_ID");
        return UserInfoQry.getAllNormalUserInfoByCustId(cust_id);
    }

    /**
     * 根据用户USER_ID查询用户信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getGrpUserInfoByUserId(IData idata) throws Exception
    {
        String eparchyCode = BizRoute.getRouteId();
        String user_id = idata.getString("USER_ID");
        String remove_tag = idata.getString("REMOVE_TAG");
        IData data = UserInfoQry.getGrpUserInfoByUserId(user_id, remove_tag, eparchyCode);
        if (IDataUtil.isNotEmpty(data))
        {
            data.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(data.getString("EPARCHY_CODE")));
            data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(data.getString("PRODUCT_ID")));
            data.put("BRAND_NAME", UBankInfoQry.getBankNameByBankCode(data.getString("BRAND_CODE")));
        }
        IDataset dataset = IDataUtil.idToIds(data);
        return dataset;
    }

    public static IDataset getMofficeBySN(IData inparams) throws Exception
    {
        return UserInfoQry.getMofficeBySN(inparams);
    }

    /**
     * 查询正常用户
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getNormalUserInfoBySN(IData inparams) throws Exception
    {
        return TradeInfoBean.getUserForUserCheck(inparams);
    }

    /**
     * @discription 根据tab_name,sql_ref,eparchy_code查询用户信息
     * @author: xunyl
     * @date 2013-03-18
     */
    public static IDataset getUserInfo(IData inparams) throws Exception
    {
        return UserInfoQry.getUserInfo(inparams);
    }

    /**
     * 根据手机号码查询用户信息,无产品信息
     * 
     * @param input
     * @return
     * @throws Exception
     *             wangjx 2013-7-27
     */
    public static IDataset getUserInfoBySnNoProduct(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        String removeTag = input.getString("REMOVE_TAG");
        return UserInfoQry.getUserInfoBySn(sn, removeTag);
    }

    /**
     * 根据用户USER_ID查询用户信息(IP后付费集团成员管理专用)
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getUserInfosByUserId(IData idata) throws Exception
    {
        String eparchyCode = BizRoute.getRouteId();
        String user_id = idata.getString("USER_ID");
        String remove_tag = idata.getString("REMOVE_TAG");
        IDataset userinfos = UserInfoQry.getUserInfoByUserId(user_id, remove_tag, eparchyCode);
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        if (IDataUtil.isNotEmpty(userinfos))
        {
            for (int i = 0; i < userinfos.size(); i++)
            {
                data = userinfos.getData(i);
                data.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(data.getString("EPARCHY_CODE")));
                data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(data.getString("PRODUCT_ID")));
                dataset.add(data);
            }
        }
        return dataset;
    }

    /*
     * 资源查询证件号码下有多少实名制 pspt_id sunxin
     */
    public static IDataset queryRealNameForRes(IData inparams) throws Exception
    {
        IDataUtil.chkParam(inparams, "PSPT_ID");
        String PSPT_ID = inparams.getString("PSPT_ID");
        return UserInfoQry.queryRealNameForRes(PSPT_ID);
    }

    /**
     * 判读号码装态 0 有效用户（排除调本省异地号码） 1 本省号段内的不存在或者无效号码 2 本省异地有效号码 3不存在的网外号码（成员新增需要支持如果是外网号码同时录入外网号码的三户资料）
     * 
     * @param inputData
     * @return
     * @throws Exception
     */
    public IDataset checkMebUserInfoBySn(IData inputData) throws Exception
    {
        String serial_number = inputData.getString("SERIAL_NUMBER");
        return IDataUtil.idToIds(UserInfoQry.checkMebUserInfoBySn(serial_number));
    }

    public IDataset getAllDestroy(IData inparams) throws Exception
    {
        // IDataset dataset = UserInfoQry.getAllDestroy(inparams);
        String serial_number = inparams.getString("SERIAL_NUMBER");
        IDataset dataset = UserInfoQry.getAllDestroyUserInfoBySn(serial_number);
        return dataset;
    }

    public IDataset getGrpUserInfoByUserIdForGrp(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        String removeTag = param.getString("REMOVE_TAG");
        IData data = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, removeTag);
        IDataset ds = new DatasetList();
        ds.add(data);
        return ds;
    }

    /**
     * @author：SongYingli
     * @time：Mar 6, 2013 11:47:47 AM
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getMemInfo(IData input) throws Exception
    {
        String serial_number_a = input.getString("SERIAL_NUMBER_A");
        String user_id_mem = input.getString("USER_ID_MEM");
        IDataset outparams = UserInfoQry.getMemInfo(serial_number_a, user_id_mem, null);
        return outparams;
    }

    /**
     * 根据CustId 查询集团成员信息查询 或者根据集团编号CustId、电话号码 查询集团成员订购关系
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset getRelaUserInfoByCstId(IData inparams) throws Exception
    {
        String cust_id = inparams.getString("CUST_ID");
        String user_id_b = inparams.getString("USER_ID_B");
        IDataset dataset = UserInfoQry.getRelaUserInfoByCstId(cust_id, null, user_id_b, null);
        return dataset;
    }

    /**
     * 查询用户信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getTradeUserInfoByUserIdAndTag(IData inparam) throws Exception
    {

        return UserInfoQry.getTradeUserInfoByUserIdAndTag(inparam);
    }

    /**
     * 根据PRODUCT_ID和CUST_ID获取用户信息:USER_ID可不传
     * 
     * @author fengsl
     * @date 2013-03-26
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getUserID(IData inparams) throws Exception
    {
        String user_id = inparams.getString("USER_ID");
        String cust_id = inparams.getString("CUST_ID");
        String product_id = inparams.getString("PRODUCT_ID");
        IDataset dataset = UserInfoQry.getUserID(user_id, cust_id, product_id);
        return dataset;
    }

    /**
     * 根据CUST_ID,BRAND_CODE查询集团的用户信息
     */
    public IDataset getUserInfoByCandB(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        String brand_code = input.getString("BRAND_CODE");
        IDataset data = UserInfoQry.getUserInfoByCandB(cust_id, brand_code, Route.CONN_CRM_CG);
        return data;
    }

    public IDataset getUserInfoByCstId(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        IDataset data = UserInfoQry.getUserInfoByCstId(cust_id, getPagination());

        return data;
    }

    public IDataset getUserInfoByCstIdForGrp(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        String privForProduct = input.getString("PRIV_FOR_PRODUCT", "false");
        IDataset data = UserInfoQry.getUserInfoByCstIdForGrpHasPriv(cust_id, privForProduct, getPagination());

        return data;
    }

    /**
     * 根据CustId、ProductId查询用户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserInfoByCstIdProId(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        String product_id = input.getString("PRODUCT_ID");
        IDataset data = UserInfoQry.getUserInfoByCstIdProId(cust_id, product_id, null);
        return data;
    }

    /**
     * 根据CustId、ProductId查询用户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserInfoByCstIdProIdForGrp(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        String product_id = input.getString("PRODUCT_ID");
        IDataset data = UserInfoQry.getUserInfoByCstIdProIdForGrp(cust_id, product_id, null);
        return data;
    }

    /**
     * 根据CUST_ID查询集团的用户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserInfoByCustID(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        IDataset data = UserInfoQry.getUserInfoByCustID(cust_id, Route.CONN_CRM_CG);
        return data;
    }

    public IDataset getUserInfoBySerialNumber(IData inparam) throws Exception
    {
        String serial_number = inparam.getString("SERIAL_NUMBER");
        String remove_tag = "0";
        String net_type_code = "00";
        return UserInfoQry.getUserInfoBySerialNumber(serial_number, remove_tag, net_type_code);
    }

    /**
     * @author：SongYingli
     * @time：Mar 5, 2013 8:55:22 PM
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserInfoBySN(IData input) throws Exception
    {
        String serial_number = input.getString("SERIAL_NUMBER");

        IData data = UserInfoQry.getMebUserInfoBySN(serial_number);
        return IDataUtil.idToIds(data);
    }

    public IDataset getUserInfoBySn3(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String removeTag = param.getString("REMOVE_TAG");
        String netTypeCode = param.getString("NET_TYPE_CODE");
        String eparchyCode = param.getString("EPARCHY_CODE");
        IDataset data = UserInfoQry.getUserInfoBySn(serialNumber, removeTag, netTypeCode, eparchyCode);
        return data;
    }

    public IDataset getUserInfoBySnCrmOneDb(IData inparam) throws Exception
    {
        String serialNumber = inparam.getString("SERIAL_NUMBER");
        IDataset dataset = UserInfoQry.getUserInfoBySnCrmOneDb(serialNumber, "0", "00");
        return dataset;
    }

    public IDataset getUserInfoVpmnSnByCPB(IData param) throws Exception
    {
        String custId = param.getString("CUST_ID");
        String productId = param.getString("PRODUCT_ID");
        String brandCode = param.getString("BRAND_CODE");
        IDataset infos = UserInfoQry.getUserInfoVpmnSnByCPB(custId, productId, brandCode);

        return infos;
    }

    /**
     * 获取用户状态，原IBOSS接口ITF_CRM_QryUserState
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getUserStateBySN(IData inparam) throws Exception
    {
        IDataUtil.chkParam(inparam, "SERIAL_NUMBER");

        IDataset returnDataset = new DatasetList();
        IData returnData = new DataMap();

        String serialNumber = inparam.getString("SERIAL_NUMBER");
        IData dataset = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isNotEmpty(dataset))
        {
            returnData.put("CATEGORY", "0"); // 正常
            returnData.put("SERIAL_NUMBER", serialNumber);
            returnData.put("USER_STATE_CODESET", dataset.getString("USER_STATE_CODESET"));
        }
        else
        {
            returnData.put("CATEGORY", "1");// 否
            returnData.put("SERIAL_NUMBER", serialNumber);
            returnData.put("X_RESULTINFO", "该号码无用户资料");
        }

        returnDataset.add(returnData);

        return returnDataset;
    }

    
    public IDataset getUserStateByUserIdDate(IData data) throws Exception
    {
    	
    	String userId = data.getString("USER_ID", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String timePoint = data.getString("TIME_POINT");//YYYYMMDDHH24MISS
        
        String stablesm = data.getString("TABLE_NAME","");
        String stables = stablesm.substring(1, stablesm.length()-1).replaceAll("\"", "");
        
        IData userinfo = null;
        if (StringUtils.isNotBlank(userId))
        {
            userinfo = UcaInfoQry.qryUserInfoByUserId(userId);// 在缓存中根据userId获取用户信息
        }
        else if (StringUtils.isNotBlank(serialNumber))
        {
            userinfo = UcaInfoQry.qryUserInfoBySn(serialNumber);// 在缓存中根据serialNumber获取用户信息
        }

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
        }
        
        userId = userinfo.getString("USER_ID"); 
        		
        IData result = new DataMap();
        result.put("SERIAL_NUMBER", userinfo.getString("SERIAL_NUMBER"));
        result.put("USER_ID", userId);
        result.put("REMOVE_TAG", userinfo.getString("REMOVE_TAG"));
        result.put("USER_STATE_CODESET", userinfo.getString("USER_STATE_CODESET"));
        String imsi = "";
        IDataset resInfo = UserResInfoQry.queryUserSimInfo(userinfo.getString("USER_ID"), "1");
        if (IDataUtil.isNotEmpty(resInfo))
        {
            imsi = resInfo.getData(0).getString("IMSI","");
        }
        result.put("IMSI", imsi);
        
        IDataset tablesinfo = new DatasetList();
        if (StringUtils.isNotBlank(stables))
        {
        	String[] tables = stables.split(",");
        	for(String table : tables)
        	{
        		if("TF_F_RELATION_UU".equalsIgnoreCase(table))
        		{
        			IDataset relationuu = RelationUUQry.getUserRelationByIdBDate(userId, timePoint);
        			result.put("TF_F_RELATION_UU", relationuu);
        		}
        		else if ("TF_F_USER_ACCESS_ACCT".equalsIgnoreCase(table))
        		{
        			IDataset accessact = UserAccessAcctInfoQry.qryInfoByUidTimePoint(userId, timePoint);
        			result.put("TF_F_USER_ACCESS_ACCT", accessact);
        		}
        		else if ("TF_F_USER_ALTSN".equalsIgnoreCase(table))
        		{
        			IDataset altsninfo = UserAltsnInfoQry.queryUserAltsnBySn(serialNumber);
        			result.put("TF_F_USER_ATTR", altsninfo);
        		}
        		else if ("TF_F_USER_ATTR".equalsIgnoreCase(table))
        		{
        			IDataset attrinfo = UserAttrInfoQry.queryUserAttrByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_ATTR", attrinfo);
        		}
        		else if ("TF_F_USER_DISCNT".equalsIgnoreCase(table))
        		{
        			IDataset disinfo = UserDiscntInfoQry.getDiscntByUidDate(userId, timePoint);
        			result.put("TF_F_USER_DISCNT", disinfo);
        		}
        		else if ("TF_F_USER_IMPU".equalsIgnoreCase(table))
        		{
        			IDataset impuinfo = UserImpuInfoQry.queryUserImpuByUidDate(userId, timePoint);
        			result.put("TF_F_USER_IMPU", impuinfo);
        		}
        		else if ("TF_F_USER_INFOCHANGE".equalsIgnoreCase(table))
        		{
        			IDataset infochange = UserInfoChgQry.queryInfoChgByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_INFOCHANGE", infochange);
        		}
        		else if ("TF_F_USER_NP".equalsIgnoreCase(table))
        		{
        			IDataset npinfo = UserNpInfoQry.qryUserNpInfosByUserId(userId);
        			result.put("TF_F_USER_NP", npinfo);
        		}
        		else if ("TF_F_USER_OCS".equalsIgnoreCase(table))
        		{
        			IDataset ocsinfo = UserOcsQry.getUserOcsByUseridDate(userId, timePoint);
        			result.put("TF_F_USER_OCS", ocsinfo);
        		}
        		else if ("TF_F_USER_OTHER".equalsIgnoreCase(table))
        		{
        			IDataset otherInfo = UserOtherInfoQry.queryOtherInfoByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_OTHER", otherInfo);
        		}
        		else if ("TF_F_USER_PRODUCT".equalsIgnoreCase(table))
        		{
        			String userIdA = "-1";
        			IDataset temp = UserSvcInfoQry.qryUserSvcByUserSvcId(userId, "860");
        			if(IDataUtil.isNotEmpty(temp)) {
        				userIdA = temp.getData(0).getString("USER_ID_A","-1");
        			} else {
        				userIdA = "-1";
        			}
        			
        			IDataset productInfo = UserProductInfoQry.queryUserProductByUserIdDate(userIdA, timePoint);
        			result.put("TF_F_USER_PRODUCT", productInfo);
        		}
        		else if ("TF_F_USER_RATE".equalsIgnoreCase(table))
        		{
        			IDataset otherInfo = UserRateInfoQry.getUserRateByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_RATE", otherInfo);
        		}
        		else if ("TF_F_USER_RES".equalsIgnoreCase(table))
        		{
        			IDataset resInfos = UserResInfoQry.getUserResInfosByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_RES", resInfos);
        		}
        		else if ("TF_F_USER_SVC".equalsIgnoreCase(table))
        		{
        			IDataset otherInfo = UserSvcInfoQry.queryUserSvcByUseridDate(userId, timePoint);
        			result.put("TF_F_USER_SVC", otherInfo);
        		}
        		else if ("TF_F_USER_SVCSTATE".equalsIgnoreCase(table))
        		{
        			IDataset svcstateInfo = UserSvcStateInfoQry.queryUserSvcStateInfoByUidDate(userId, timePoint);
        			result.put("TF_F_USER_SVCSTATE", svcstateInfo);
        		}
        		else if ("TF_F_USER_TELEPHONE".equalsIgnoreCase(table))
        		{
        			IDataset telinfo = UserTelephoeInfoQry.qryUserTelephoneByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_TELEPHONE", telinfo);
        		}
        		else if ("TF_F_USER_VPN".equalsIgnoreCase(table))
        		{
        			String userIdA = "-1";
        			IDataset temp = UserSvcInfoQry.qryUserSvcByUserSvcId(userId, "860");
        			if(IDataUtil.isNotEmpty(temp)) {
        				userIdA = temp.getData(0).getString("USER_ID_A","-1");
        			} else {
        				userIdA = "-1";
        			}
 
        			IDataset vpn = UserVpnInfoQry.getVpnInfoByUser(userIdA);
        			result.put("TF_F_USER_VPN", vpn);
        		}
        		else if ("TF_F_USER_VPN_MEB".equalsIgnoreCase(table))
        		{
        			IDataset vpnmeb = UserVpnInfoQry.getMemberVpnByUserId(userId);
        			result.put("TF_F_USER_VPN_MEB", vpnmeb);
        		}
        		else if ("TF_F_USER_WIDENET".equalsIgnoreCase(table))
        		{
        			IDataset widenetinfo = WidenetInfoQry.getWidenetInfoByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_WIDENET", widenetinfo);
        		}
        		else if ("TF_F_USER_WIDENET_ACT".equalsIgnoreCase(table))
        		{
        			IDataset widenetactinfo = WidenetInfoQry.getWidenetActInfoByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_WIDENET_ACT", widenetactinfo);
        		}
        		else if ("TF_F_USER_PLATSVC".equalsIgnoreCase(table))
        		{
        			IDataset platsvs = UserPlatSvcInfoQry.queryUserPlatSvcInfoByUidDate(userId, timePoint);
        			result.put("TF_F_USER_PLATSVC", platsvs);
        		}
        		else if ("TF_F_USER_BLACKWHITE".equalsIgnoreCase(table))
        		{
        			IDataset widenetactinfo = UserBlackWhiteInfoQry.querySvcInfoByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_BLACKWHITE", widenetactinfo);
        		}
        		else if ("TF_F_USER_GRP_PLATSVC".equalsIgnoreCase(table))
        		{
        			IDataset widenetactinfo = UserGrpPlatSvcInfoQry.getUserGrpPlatSvcByUserIdDate(userId, timePoint);
        			result.put("TF_F_USER_GRP_PLATSVC", widenetactinfo);
        		}
        	}
        }
        return IDataUtil.idToIds(result);
    }
    
    
    public IDataset getUserStateByUserId(IData data) throws Exception
    {
        String userId = data.getString("USER_ID", "");// 用户ID
        String serialNumber = data.getString("SERIAL_NUMBER", "");// 用户服务号码
        IDataset svcs = data.getDataset("SVC_LIST");
        // IDataUtil.chkParam(data, CSBizBean.getUserEparchyCode());// 地州编码

        IData userinfo = null;
        if (StringUtils.isNotBlank(userId))
        {
            userinfo = UcaInfoQry.qryUserInfoByUserId(userId);// 在缓存中根据userId获取用户信息
        }
        else if (StringUtils.isNotBlank(serialNumber))
        {
            userinfo = UcaInfoQry.qryUserInfoBySn(serialNumber);// 在缓存中根据serialNumber获取用户信息
        }

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
        }
        IData result = new DataMap();
        result.put("SERIAL_NUMBER", userinfo.getString("SERIAL_NUMBER"));
        result.put("USER_ID", userinfo.getString("USER_ID"));
        result.put("REMOVE_TAG", userinfo.getString("REMOVE_TAG"));
        result.put("USER_STATE", userinfo.getString("USER_STATE_CODESET"));
        result.put("USER_STATE_CODESET", userinfo.getString("USER_STATE_CODESET"));
        String imsi = "";
        IDataset resInfo = UserResInfoQry.queryUserSimInfo(userinfo.getString("USER_ID"), "1");
        if (IDataUtil.isNotEmpty(resInfo))
        {
            imsi = resInfo.getData(0).getString("IMSI");
        }
        result.put("IMSI", imsi);

        IDataset svclist = new DatasetList();
        if (IDataUtil.isNotEmpty(svcs))
        {
            for (int i = 0; i < svcs.size(); i++)
            {
                IData svc = svcs.getData(i);
                String svcId = svc.getString("SVC_ID");
                String tradeTab = svc.getString("FROM_TAB");

                if ("TF_B_TRADE_SVC".equals(tradeTab))
                {
                    IDataset usersvc = UserSvcInfoQry.getSvcUserIdPf(userId, svcId);
                    if (IDataUtil.isNotEmpty(usersvc))
                    {
                        for (int m = 0; m < usersvc.size(); m++)
                        {
                            IData temp = new DataMap();
                            temp.put("SERVICE_ID", svcId);
                            temp.put("START_DATE", usersvc.getData(m).getString("START_DATE"));
                            temp.put("END_DATE", usersvc.getData(m).getString("END_DATE"));
                            temp.put("FROM_TAB", tradeTab);
                            svclist.add(temp);
                        }
                    }
                }
                else if ("TF_B_TRADE_PLATSVC".equals(tradeTab))
                {
                    IDataset platsvc = UserPlatSvcInfoQry.querySvcInfoByUserIdAndSvcIdPf(userId, svcId);
                    if (IDataUtil.isNotEmpty(platsvc))
                    {
                        for (int m = 0; m < platsvc.size(); m++)
                        {
                            IData temp = new DataMap();
                            temp.put("SERVICE_ID", svcId);
                            temp.put("START_DATE", platsvc.getData(m).getString("START_DATE"));
                            temp.put("END_DATE", platsvc.getData(m).getString("END_DATE"));
                            temp.put("FROM_TAB", tradeTab);
                            svclist.add(temp);
                        }
                    }
                }
                else if ("TF_B_TRADE_BLACKWHITE".equals(tradeTab))
                {
                    IDataset blackWhitesvc = UserBlackWhiteInfoQry.querySvcInfoByUserIdAndSvcIdPf(userId, svcId);
                    if (IDataUtil.isNotEmpty(blackWhitesvc))
                    {
                        for (int m = 0; m < blackWhitesvc.size(); m++)
                        {
                            IData temp = new DataMap();
                            temp.put("SERVICE_ID", svcId);
                            temp.put("START_DATE", blackWhitesvc.getData(m).getString("START_DATE"));
                            temp.put("END_DATE", blackWhitesvc.getData(m).getString("END_DATE"));
                            temp.put("FROM_TAB", tradeTab);
                            svclist.add(temp);
                        }
                    }
                }
                else if ("TF_B_TRADE_GRP_PLATSVC".equals(tradeTab))
                {
                    IDataset usersvc = UserGrpInfoQry.querySvcInfoByUserIdAndSvcIdPf(userId, svcId);
                    if (IDataUtil.isNotEmpty(usersvc))
                    {
                        for (int m = 0; m < usersvc.size(); m++)
                        {
                            IData temp = new DataMap();
                            temp.put("SERVICE_ID", svcId);
                            temp.put("START_DATE", usersvc.getData(m).getString("START_DATE"));
                            temp.put("END_DATE", usersvc.getData(m).getString("END_DATE"));
                            temp.put("FROM_TAB", tradeTab);
                            svclist.add(temp);
                        }
                    }
                }
                else if ("TF_B_TRADE_OTHER".equals(tradeTab))
                {
                    IDataset usersvc = UserOtherInfoQry.querySvcInfoByUserIdAndSvcIdPf(userId, svcId);
                    if (IDataUtil.isNotEmpty(usersvc))
                    {
                        for (int m = 0; m < usersvc.size(); m++)
                        {
                            IData temp = new DataMap();
                            temp.put("SERVICE_ID", svcId);
                            temp.put("START_DATE", usersvc.getData(m).getString("START_DATE"));
                            temp.put("END_DATE", usersvc.getData(m).getString("END_DATE"));
                            temp.put("FROM_TAB", tradeTab);
                            svclist.add(temp);
                        }
                    }
                }
                else if ("TF_B_TRADE_SVCSTATE".equals(tradeTab))
                {
                    IDataset usersvc = UserSvcStateInfoQry.getUserLastStateByUserSvcPf(userId, svcId);
                    if (IDataUtil.isNotEmpty(usersvc))
                    {
                        for (int m = 0; m < usersvc.size(); m++)
                        {
                            IData temp = new DataMap();
                            temp.put("SERVICE_ID", svcId);
                            temp.put("START_DATE", usersvc.getData(m).getString("START_DATE"));
                            temp.put("END_DATE", usersvc.getData(m).getString("END_DATE"));
                            temp.put("FROM_TAB", tradeTab);
                            svclist.add(temp);
                        }
                    }
                }
                else if ("TF_B_TRADE_RES".equals(tradeTab))
                {
                    // TODO:暂不处理
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "[FROM_TAB]入参错误!");
                }
            }
        }
        result.put("SVC_LIST", svclist);

        return IDataUtil.idToIds(result);
    }

    /**
     * 是否融合V网
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public boolean isCentrexVpn(IData iData) throws Exception
    {
        String productId = iData.getString("PRODUCT_ID", "");

        if ("8000".equals(productId))
        {
            String userId = iData.getString("USER_ID");
            IDataset userInfos = UserVpnInfoQry.qryUserVpnByUserId(userId);
            IData userInfo = new DataMap();
            // 设置融合V网标识
            if (IDataUtil.isNotEmpty(userInfos))
            {
                userInfo = (IData) userInfos.get(0);
                String vpnUserCode = userInfo.getString("VPN_USER_CODE", "");
                iData.put("IF_CENTRETYPE", vpnUserCode);
                return false;
            }
        }
        return false;
    }

    /**
     * 根据GROUP_ID、PRODUCT_ID获取user_id和product_id
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset qryUserByGroupIdAndProductIdForGrp(IData iData) throws Exception
    {

        IDataUtil.chkParam(iData, "GROUP_ID");
        IDataUtil.chkParam(iData, "PRODUCT_ID");

        String group_id = iData.getString("GROUP_ID");
        String product_id = iData.getString("PRODUCT_ID");

        IDataset userinfos = UserInfoQry.qryUserByGroupIdAndProductIdForGrp(group_id, product_id);

        if (IDataUtil.isNotEmpty(userinfos))
        {
            IData param = userinfos.getData(0);
            isCentrexVpn(param); // 是否融合V网
            return userinfos;
        }

        return null;
    }

    /**
     * 查询成员号码
     * 
     * @author fengsl
     * @date 2013-03-26
     * @param param
     */
    public IDataset queryMaxSerialMumber(IData inparams) throws Exception
    {
        String product_id = inparams.getString("PRODUCT_ID");
        String cust_id = inparams.getString("CUST_ID");
        IDataset dataset = UserInfoQry.queryMaxSerialMumber(product_id, cust_id);
        return dataset;
    }

    public IDataset queryUserEparchyAndBrandCode(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        IData data = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(data))
        {
            data = new DataMap();
            data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        }

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset queryUserInfoBySN(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String removeTag = param.getString("REMOVE_TAG");
        String netTypeCode = param.getString("NET_TYPE_CODE");

        IDataset infos = UserInfoQry.getUserInfoBySN(serialNumber, removeTag, netTypeCode);

        return infos;
    }

    public IDataset queryUserInfoByUserId(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");

        return UserInfoQry.getUserInfoByUserId(userId);
    }
    
    public IDataset queryUserInfoByUserIdAndTag(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        String removeTag = param.getString("REMOVE_TAG");

        return UserInfoQry.getUserInfoByUserIdTag(userId,removeTag);
    }
    
    public IDataset checkUserRight(IData param)throws Exception{
    	IData result=new DataMap();
    	result.put("QUERY_RESULT_CODE", "0");
    	result.put("QUERY_RESULT_RESULT","");
    	
    	String departCode=param.getString("QUERY_DEPART_CODE");
    	String staffDepartId=param.getString("STAFF_DEPART_ID");
    	String staffCityCode=param.getString("STAFF_CITY_CODE","0");
    	
    	//查询是否存在代理商的信息
    	IDataset queryDepertInfo=DepartInfoQry.getAgentDepart(departCode, "0");
    	if(IDataUtil.isNotEmpty(queryDepertInfo)){
    		String queryDepartId=queryDepertInfo.getData(0).getString("DEPART_ID");
    		result.put("QUERY_DEPART_ID", queryDepartId);
    		String AREA_CODE = queryDepertInfo.getData(0).getString("AREA_CODE");
    		
    		if(!(staffCityCode.equals("HNSJ")||staffCityCode.equals("HNHN")||
    				staffCityCode.equals("HNYD"))){		//如果不为省级工号
    			if(!staffCityCode.equals(AREA_CODE)){ //业务区不一样
        			result.put("QUERY_RESULT_CODE", "1");
            		result.put("QUERY_RESULT_RESULT", "您无权查询代理商【"+departCode+"】下的回收号码信息！");
        		}
    		}
    		
    	}else{
    		result.put("QUERY_RESULT_CODE", "1");
    		result.put("QUERY_RESULT_RESULT", "代理商编码在数据库不存在！");
    	}
    	
    	
    	IDataset finalResult=new DatasetList();
    	finalResult.add(result);
    	
    	return finalResult;
    	
    }
    /**
     * 根据起始部门和终止部门查询权限
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkUserRightStartEndDepart(IData param)throws Exception{
        IData result=new DataMap();
        result.put("QUERY_RESULT_CODE", "0");
        result.put("QUERY_RESULT_RESULT","");
        
        String startDepartCode=param.getString("START_AGENT_NO");
        String endDepartCode=param.getString("END_AGENT_NO");
        
        String staffDepartId=param.getString("STAFF_DEPART_ID"); 
        String staffCityCode=param.getString("STAFF_CITY_CODE","0");
        
        //查询是否存在代理商的信息
        IDataset queryDepertInfo=DepartInfoQry.getAgentDepart(startDepartCode,endDepartCode, "0");
        if (IDataUtil.isNotEmpty(queryDepertInfo)) {
            for (int i = 0; i < queryDepertInfo.size(); i++) {

                String queryDepartId = queryDepertInfo.getData(i).getString("DEPART_ID");
                result.put("QUERY_DEPART_ID", queryDepartId);
                String AREA_CODE = queryDepertInfo.getData(i).getString("AREA_CODE");

                if (!(staffCityCode.equals("HNSJ") || staffCityCode.equals("HNHN") || staffCityCode.equals("HNYD"))) { //如果不为省级工号
                    if (!staffCityCode.equals(AREA_CODE)) { //业务区不一样
                        result.put("QUERY_RESULT_CODE", "1");
                        result.put("QUERY_RESULT_RESULT", "您无权查询代理商【" + queryDepertInfo.getData(i).getString("DEPART_CODE") + "】下的回收号码信息！");
                        break;
                    }
                }
            }

        }else{
            result.put("QUERY_RESULT_CODE", "1");
            result.put("QUERY_RESULT_RESULT", "代理商编码在数据库不存在！");
        }
        
        
        IDataset finalResult=new DatasetList();
        finalResult.add(result);
        
        return finalResult;
        
    }
    
    public IDataset queryBackUserByBusiArea(IData param)throws Exception{
    	String isExtendTime=param.getString("IS_EXTEND_TIME","");
    	String queryDepartId=param.getString("QUERY_DEPART_ID",""); 
    	
    	IDataset result=UserInfoQry.queryBackUserByDepartId(queryDepartId,isExtendTime, getPagination());
    	
    	return result;
    }
    
    public IDataset queryBackUser(IData param)throws Exception{
    	IDataset result=UserInfoQry.queryBackUser(param, getPagination());
    	
    	return result;
    }
    
    
    public IData extendBackUserData(IData param)throws Exception{
    	IDataset params=param.getDataset("EXTEND_DATAS");
    	UserInfoQry.extendBackUserByDepartId(params);
    	UserInfoQry.saveBackUserExtendLog(params);
    	
    	return new DataMap();
    }
    
    /**
     * 获取用户状态查询，流量平台接口ITF_CRM_USER_INFO_CHK
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset checkUserStateBySerialNumber(IData inparam) throws Exception
    {
        IDataUtil.chkParam(inparam, "SERIAL_NUMBER");

        IDataset returnDataset = new DatasetList();
        IData returnData = new DataMap();

        String serialNumber = inparam.getString("SERIAL_NUMBER");
        IData dataset = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isNotEmpty(dataset))
        {
        	
        	String status = dataset.getString("USER_STATE_CODESET");
            returnData.put("CATEGORY", "0"); // 正常
            returnData.put("SERIAL_NUMBER", serialNumber);
            returnData.put("USER_STATE_CODESET", dataset.getString("USER_STATE_CODESET"));
            
            if(StringUtils.equals(status, "0")){
            	 returnData.put("USER_STATUS", "0");// 正常
            	 returnData.put("SERIAL_NUMBER", serialNumber);
            }
            else
            {
            	 returnData.put("USER_STATUS", "3");// 停机
            	 returnData.put("SERIAL_NUMBER", serialNumber);
            }
        }
        else
        {
            returnData.put("USER_STATUS", "2");// 销户
            returnData.put("SERIAL_NUMBER", serialNumber);
            returnData.put("X_RESULTINFO", "该号码用户不存在");
        }

        returnDataset.add(returnData);

        return returnDataset;
    }
    
    /**
     * 获取用户状态查询
     * 
     * @param inparam
     * @return
     * @throws Exception
     * @author wukw3 add 20170614
     */
    public IData getUserStateBySerialNumber(IData inparam) throws Exception
    {
        IDataUtil.chkParam(inparam, "SERIAL_NUMBER");

        IData returnData = new DataMap();

        String serialNumber = inparam.getString("SERIAL_NUMBER");
        IData dataset = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isNotEmpty(dataset))
        {
        	
        	String status = dataset.getString("USER_STATE_CODESET");
            returnData.put("CATEGORY", "0"); // 正常
            returnData.put("SERIAL_NUMBER", serialNumber);
            returnData.put("USER_STATE_CODESET", dataset.getString("USER_STATE_CODESET"));
            
            if(StringUtils.equals(status, "0")){
            	 returnData.put("USER_STATUS_CODE", "0");// 正常
            	 returnData.put("USER_STATUS_NAME", "正常");// 正常
            	 returnData.put("SERIAL_NUMBER", serialNumber);
            }
            else
            {
            	 returnData.put("USER_STATUS_CODE", "3");// 停机
            	 returnData.put("USER_STATUS_NAME", "停机");// 停机
            	 returnData.put("SERIAL_NUMBER", serialNumber);
            }
            
            IDataset svcStates = UserSvcStateInfoQry.getUserMainState(dataset.getString("USER_ID","-1"));
            if(IDataUtil.isNotEmpty(svcStates) && svcStates.size() > 0){
            	IData svcData = svcStates.getData(0);
            	if(IDataUtil.isNotEmpty(svcData)){
            		String strServiceId = svcData.getString("SERVICE_ID");
            		String strStateCode = svcData.getString("STATE_CODE");
            		String stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode(strServiceId,strStateCode);
            		//取主服务状态
            		returnData.put("MAINSVC_STATUS_CODE", strStateCode);// 状态编码
            		returnData.put("MAINSVC_STATUS_NAME", stateName);// 状态名称
            	}
            }else{
            	returnData.put("USER_STATUS_CODE", "0");// 正常
           	 	returnData.put("USER_STATUS_NAME", "正常");// 正常
            }
        }
        else
        {
            returnData.put("USER_STATUS_CODE", "2");// 销户
       	 	returnData.put("USER_STATUS_NAME", " 销户");// 停机
       	 	returnData.put("MAINSVC_STATUS_CODE", "6");// 销户
       	    returnData.put("MAINSVC_STATUS_NAME", "销号");// 销户
            returnData.put("SERIAL_NUMBER", serialNumber);
            returnData.put("X_RESULTINFO", "该号码用户不存在");
        }

        return returnData;

    }
    
    /**
     * 20160603
     * <br/>
     * 根据手机号码获取查询对应的SIM卡号
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset queryUserSimInfoBySn(IData inparam) throws Exception
    {
    	 String serialNumber=inparam.getString("SERIAL_NUMBER");
    	 IDataset userinfo=UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	 if(IDataUtil.isNotEmpty(userinfo)){
        	 String user_id =userinfo.getData(0).getString("USER_ID");
             String res_type_code ="1";
             IDataset output = UserResInfoQry.queryUserSimInfo(user_id, res_type_code);
             return output;
    	 }
    	 return null;

    }
    
    /**
     * 根据CustId查询用户物联网信息是否同步
     * 
     * @param input
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public IDataset queryUserInfoByCustId(IData input) throws Exception
    {
        return UserInfoQry.queryUserInfoByCustId(input.getString("CUST_ID"));
    }
    
    /**
     * 根据SN查询用户全球通等级信息
     * 
     * @param input
     * @return
     * @throws Exception
     * @author wuhao5
     */
    public IData queryUserClassBySN(IData input) throws Exception
    {
        return UserClassInfoQry.queryUserClassBySN(input);
    }
    
    /**
     * 优化单位证件开户阀值权限设置需求
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRealNameUserLimitByPsptNew(IData input) throws Exception
    {
        String custName = input.getString("CUST_NAME");
        String psptId = input.getString("PSPT_ID");
        String userType = input.getString("RSRV_STR1");
        int cnt = UserInfoQry.getRealNameUserLimitByPsptNew(custName, psptId, userType);
        IDataset resultList = new DatasetList();
        IData param = new DataMap();
        param.put("CNT", cnt);
        resultList.add(param);
        return  resultList;
    }
    
    /**
     * 获取指定证件号已实名制开户的数量
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRealNameUserCountByPspt2New(IData input) throws Exception
    {
    	String custName = input.getString("CUST_NAME");
        String psptId = input.getString("PSPT_ID");
        String userType = input.getString("RSRV_STR1");
        int cnt = UserInfoQry.getRealNameUserCountByPspt2New(custName, psptId, userType);
        IDataset resultList = new DatasetList();
        IData param = new DataMap();
        param.put("CNT", cnt);
        resultList.add(param);
        return  resultList;
    }

    /**
     * 根据group_id查询用户信息
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getServceInfoForProductId(IData input) throws Exception
    {
        String group_id = input.getString("GROUP_ID");
        IDataset data = UserProductInfoQry.getServceInfoForProductId(group_id);
        return data;
    }
}
