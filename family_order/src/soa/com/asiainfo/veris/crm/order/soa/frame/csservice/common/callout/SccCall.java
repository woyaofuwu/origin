
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class SccCall
{
    public static IDataset chnlAcctInfoKfo(String departId, String objCode, String fPayFee, String tradeId, String tradeTypeCode, String userId, String serialNumber, String dealDate) throws Exception
    {
        IData data = new DataMap();
        setPubParam(data);
        data.put("DEPART_ID", departId);
        data.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        data.put("SERIAL_NUMBER", objCode);
        data.put("X_FPAY_FEE", fPayFee);
        data.put("TRADE_ID", tradeId);
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("TRADE_TYPE", "30");
        data.put("RECV_USER_ID", userId);
        data.put("PAY_SERIAL_NUMBER", serialNumber);
        data.put("DEAL_DATE", dealDate);
        data.put("IN_MODE_CODE", "7");

        return CSAppCall.call("TCM_ChnlAcctIntfKfo", data);
    }

    public static IDataset chnlAcctIntfCancel(String backChargeId, String objCode) throws Exception
    {
        IData data = new DataMap();
        setPubParam(data);
        data.put("BACK_CHARGE_ID", backChargeId);
        data.put("SERIAL_NUMBER", objCode);
        data.put("IN_MODE_CODE", "7");
        data.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

        return CSAppCall.call("TCM_ChnlAcctIntfCancel", data);
    }

    /**
     * 办理品牌变更调用营销管理接口
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset createBrandJob(IData param) throws Exception
    {
        //return CSAppCall.call("MS.BiIntfOutterSVC.createBrandJob", param);
    	return getRetVal();
    }

    /**
     * 办理国漫调用营销管理接口
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset createRoamJob(IData param) throws Exception
    {
        //return CSAppCall.call("MS.BiIntfOutterSVC.createRoamJob", param);
    	return getRetVal();
    }
    
    /**
     * 客户更改全网统一资费调用营销管理接口
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset createUnifiedPackageJob(IData param) throws Exception
    {
        //return CSAppCall.call("MS.BiIntfOutterSVC.createUnifiedPackageJob", param);
    	return getRetVal();
    }

    /***
     * @param data
     *            USER_ID|用户标识,CUST_ID|客户标识,CUST_NAME|客户姓名,USE_CUST_NAME|使用者姓名,CUST_TYPE|客户级别,CUST_MANAGER_ID|电话经理标识,
     *            EPARCHY_CODE|当前业务区,BRAND_CODE|品牌,PORT_OUT_DATE|携出申请时间,PORT_OUT_NETID|携出方,AVAGE_FEE|近三个月平均ARPU值,
     *            PRODUCT_ID
     *            |当前主套餐,SCORE_VALUE|当前积分,GROUP_CUST_ID|集团客户标识,GROUP_CUST_NAME|集团客户名称,GROUP_CUST_MANAGER_ID|集团客户经理标识,
     *            GROUP_CONTACT_PHONE|集团客户联系方式,CAMPN_ID|当前签约活动标识,CAMPN_NAME|当前签约活动名称,
     *            START_DATA|约定在网开始时间,END_DATA|约定在网结束时间,ERROR_MESSAGE|携出失败原因
     * @return IData
     **/
    public static IDataset createTransferFailJob(IData param) throws Exception
    {
        //return CSAppCall.call("MS.BiIntfOutterSVC.createTransferFailJob", param);
    	return getRetVal();
    }

    /**
     * * @param data USER_ID|用户标识 CUST_ID|客户标识 CUST_NAME|客户姓名 USE_CUST_NAME|使用者姓名 CUST_TYPE|客户级别 CUST_MANAGER_ID|电话经理标识
     * EPARCHY_CODE|当前业务区 BRAND_CODE|品牌 PORT_OUT_DATE|携出申请时间 PORT_OUT_NETID|携出方 AVAGE_FEE|近三个月平均ARPU值 PRODUCT_ID|当前主套餐
     * SCORE_VALUE|当前积分 GROUP_CUST_ID|集团客户标识 GROUP_CUST_NAME|集团客户名称 GROUP_ CUST_MANAGER_ID|集团客户经理标识
     * GROUP_CONTACT_PHONE|集团客户联系方式
     */
    public static IDataset createTransferJob(IData param) throws Exception
    {
        //return CSAppCall.call("MS.BiIntfOutterSVC.createTransferJob", param);
    	return getRetVal();
    }

    /**
     * @Function: delCustVipBySN
     * @Description: VIP级别清零
     * @param serialNumber
     * @throws Exception
     * @version: v1.0.0
     * @author: zhuyu
     * @date: 2014年7月17日 下午5:12:47
     */
    public static IDataset delCustVipBySN(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return CSAppCall.call("CM.CustVipSVC.delCustVipBySNHain", param);
    }

    public static IDataset getChnlAcctObjectCode(String departId, String staffId) throws Exception
    {
        IData data = new DataMap();
        data.put("DEPART_ID", departId);
        data.put("STAFF_ID", staffId);
        data.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        return CSAppCall.call("QCM_GetChnlAcctObjCode", data);
    }

    public static IDataset getCustVipInfo(String xGetMode, String userId, String serialNumber, String removeTag) throws Exception
    {
        IData param = new DataMap();
        param.put("X_GETMODE", xGetMode);
        if ("4".equals(xGetMode))
        {// 根据USER_ID查询
            param.put("USER_ID", userId);
        }
        else if ("0".equals(xGetMode))
        {// 根据SERIAL_NUMBER查询
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("REMOVE_TAG", removeTag);
        }

        return CSAppCall.call("CM_CustVip_GetVipInfo", param);
    }

    // TODO:没提供
    // 入参 USER_ID MSISDN USER_TYPE IDENT_CODE IDENT_CODE_TYPE IDENT_CODE_LEVEL ACCEP_BUSI_ID
    // ROUTE_EPARCHY_CODE
    // xuwb5 渠道提供服务 有USER_ID 入参 在此根据msisdn 查询出 USER_ID 做入参
    public static IDataset getUSPRequestInfo(String msisdn, String userType, String identCode, String identCodeType, String identCodeLevel, String tradeType) throws Exception
    {
        IData param = new DataMap();
        IData user = UcaInfoQry.qryUserInfoBySn(msisdn);
        param.put("MSISDN", msisdn);
        param.put("USER_ID", user.getString("USER_ID"));
        param.put("USER_TYPE", userType);
        param.put("IDENT_CODE", identCode);
        param.put("IDENT_CODE_TYPE", identCodeType);
        param.put("IDENT_CODE_LEVEL", identCodeLevel);
        param.put("ACCEP_BUSI_ID", tradeType);
        param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());// 由于服务是根据地市路由 加上这个。
        // setPubParam(param);
        // return CSAppCall.call("ITF_CHNL_getUSPRequestInfo", param);
        return CSAppCall.call("CHNL.ChnlmanmIntfSVC.getUSPRequestInfo", param);
    }

    /**
     * 根据集团编码查询
     * 
     * @param operType
     * @param groupId
     * @return
     * @throws Exception
     */
    public static IDataset qryTaxApplyByGroupId(String operType, String groupId) throws Exception
    {
        IData param = new DataMap();

        param.put("OPER_TYPE", operType);
        param.put("GROUP_ID", groupId);

        return CSAppCall.call("CM.CustomerSVC.getTaxApplyInfo", param);
    }

    public static IDataset saveChargeTouchInfo(IData param) throws Exception
    {
        return CSAppCall.call("CHNL.ChnlmanmIntfSVC.saveChargeTouchInfo", param);

    }

    private static void setPubParam(IData data) throws Exception
    {
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
    }
    
    /**
     * 客户欠费即将停机时刻工作生成接口
     * 
     * @Function: createDownTimeJob
     * @Description: TODO
     * @date Aug 28, 2014 5:14:01 PM
     * @param serialNumber
     * @param userId
     * @param custId
     * @param custName
     * @param custType
     * @param eparchyCode
     * @param brandCode
     * @param useCustName
     * @param custManagerId
     * @param rsrvDate1
     * @param rsrvDate2
     * @return
     * @throws Exception
     * @author longtian3
     */
    public static IDataset createDownTimeJob(String serialNumber, String userId, String custId, String custName, String custType, String eparchyCode, String brandCode, String useCustName, String custManagerId, String rsrvDate1, String rsrvDate2,
            String rsrvNum1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("CUST_NAME", custName);
        param.put("CUST_TYPE", custType);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("BRAND_CODE", brandCode);
        param.put("USE_CUST_NAME", useCustName);
        param.put("CUST_CODE", serialNumber);
        param.put("CUST_MANAGER_ID", custManagerId);
        param.put("RSRV_DATE1", rsrvDate1);
        param.put("RSRV_DATE2", rsrvDate2);
        param.put("RSRV_NUM1", rsrvNum1);
//         return CSAppCall.call("http://10.200.130.83:10000/service", "MS.BiIntfOutterSVC.createDownTimeJob", param,
//         true);
       // return CSAppCall.call("MS.BiIntfOutterSVC.createDownTimeJob", param);
        return getRetVal();

    }

    /**
     * 即将达到gprs流量
     * 
     * @Function: createGPRSThresholdJob
     * @Description: TODO
     * @date Aug 28, 2014 5:14:11 PM
     * @param serialNumber
     * @param userId
     * @param custId
     * @param custName
     * @param custType
     * @param eparchyCode
     * @param brandCode
     * @param useCustName
     * @param custManagerId
     * @param remindTag
     * @param discntCode
     * @param discntName
     * @return
     * @throws Exception
     * @author longtian3
     */
    public static IDataset createGPRSThresholdJob(String serialNumber, String userId, String custId, String custName, String custType, String eparchyCode, String brandCode, String useCustName, String custManagerId, String remindTag,
            String discntCode, String discntName, String rsrvStr7) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("CUST_NAME", custName);
        param.put("CUST_TYPE", custType);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("BRAND_CODE", brandCode);
        param.put("USE_CUST_NAME", useCustName);
        param.put("CUST_CODE", serialNumber);
        param.put("CUST_MANAGER_ID", custManagerId);
        param.put("REMIND_TAG", remindTag);
        param.put("DISCNT_CODE", discntCode);
        param.put("DISCNT_NAME", discntName);
        param.put("RSRV_STR7", rsrvStr7);// 流量阀值单位 OPEN_ALERT_FLOW

        IData userGprs = AcctCall.getUserGPRSTotalByUserId(userId);
        param.put("CURR_MONTH_EFF_FLOW", userGprs.getString("CURR_MONTH_EFF_FLOW"));// 当月可用流量
        param.put("USED_FLOW", userGprs.getString("USED_FLOW"));// 已用流量单位
        param.put("END_FLOW", userGprs.getString("END_FLOW"));// 剩余流量单位
        // return CSAppCall.call("http://10.200.130.83:10000/service", "MS.BiIntfOutterSVC.createGPRSThresholdJob",
        // param, true);
        //return CSAppCall.call("MS.BiIntfOutterSVC.createGPRSThresholdJob", param);
        return getRetVal();
    }

    /**
     * 缴费提醒关键时刻工作生成接口
     * 
     * @Function: createPaymentJob
     * @Description: TODO
     * @date Aug 28, 2014 5:14:08 PM
     * @param serialNumber
     * @param userId
     * @param custId
     * @param custName
     * @param custType
     * @param eparchyCode
     * @param brandCode
     * @param useCustName
     * @param rsrvNum1
     * @param rsrvNum2
     * @return
     * @throws Exception
     * @author longtian3
     */
    public static IDataset createPaymentJob(String serialNumber, String userId, String custId, String custName, String custType, String eparchyCode, String brandCode, String useCustName, String rsrvNum1, String rsrvNum2) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("CUST_NAME", custName);
        param.put("CUST_TYPE", custType);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("BRAND_CODE", brandCode);
        param.put("USE_CUST_NAME", useCustName);
        param.put("CUST_CODE", serialNumber);
        param.put("RSRV_NUM2", rsrvNum2);
        param.put("RSRV_NUM1", rsrvNum1);
        // return CSAppCall.call("http://10.200.130.83:10000/service", "MS.BiIntfOutterSVC.createPaymentJob", param,
        // true);
        //return CSAppCall.call("MS.BiIntfOutterSVC.createPaymentJob", param);
        return getRetVal();
    }
    
    public static IDataset createGPRSThresholdJob(String serialNumber, String userId, String custId, String custName, String custType, String eparchyCode, String brandCode, String useCustName, String custManagerId, String remindTag,
            String discntCode, String discntName, String rsrvStr7, 
            String currMonthEffFlow, String usedFlow, String endFlow) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("CUST_NAME", custName);
        param.put("CUST_TYPE", custType);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("BRAND_CODE", brandCode);
        param.put("USE_CUST_NAME", useCustName);
        param.put("CUST_CODE", serialNumber);
        param.put("CUST_MANAGER_ID", custManagerId);
        param.put("REMIND_TAG", remindTag);
        param.put("DISCNT_CODE", discntCode);
        param.put("DISCNT_NAME", discntName);
        param.put("RSRV_STR7", rsrvStr7);// 流量阀值单位 OPEN_ALERT_FLOW

        param.put("CURR_MONTH_EFF_FLOW", currMonthEffFlow);// 当月可用流量
        param.put("USED_FLOW", usedFlow);// 已用流量单位
        param.put("END_FLOW", endFlow);// 剩余流量单位

        //return CSAppCall.call("MS.BiIntfOutterSVC.createGPRSThresholdJob", param);
        return getRetVal();
    }
    
    public static IDataset getRetVal()throws Exception{
    	IDataset result = new DatasetList();
    	IData returnData = new DataMap();
    	returnData.put("X_RESULTCODE", "0");
		returnData.put("X_RESULTINFO", "ok");
		result.add(returnData);
		return result;
    }
    
    /**
     * 修改用户对应的个人客户资料生日信息
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset modifyCustInfoByBirthday(IData inparam) throws Exception
    {        	
//    	return CSAppCall.call("CM.CustPersonSVC.modifyCustInfoByBirthday", inparam);
    	//return CSAppCall.call("CC.outsvc.ICCOutOperateSV.modifyCustInfoByBirthday", inparam);
    	
    	 ServiceResponse response = BizServiceFactory.call("CCF.outsvc.ICCOutOperateSV.modifyCustInfoByBirthday", inparam, null);
         IDataset outset = response.getData();

         IDataset ds =  new DatasetList(outset);
         return ds;
    	
    }
}
