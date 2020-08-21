/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

/**
 * @CREATED by gongp@2013-12-10 修改历史 Revision 2013-12-10 下午04:55:01
 */
public class PBossCall
{
    public static final Logger logger = Logger.getLogger(PBossCall.class);

    /*
     * @Function: resPreOccupy()
     * @Description: 商务电话（固话）预占
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-23 上午10:57:00 Modification History: Date Author Version Description
     * ---------------------------------------------------------* 2014-7-23 yxd v1.0.0 修改原因
     */
    public static IData batResPreOccupy(String serialNumber, String sign_path) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckBatchPhoneCodeInfo");
        param.put("RES_TYPE_CODE", "N");
        param.put("MOFFICE_ID", sign_path);
        param.put("RES_NO", serialNumber);
        param.put("X_GETMODE", "1");
        setPublicParam(param);
        IDataset set = callPBOSS("TT.CheckResInfo", param);
        IData result = new DataMap();
        if (DataSetUtils.isNotBlank(set))
        {
            result = set.getData(0);
        }
        return result;
    }

    // PBOSS
    public static IDataset callPBOSS(String svcName, IData inparams) throws Exception
    {
        String url = BizEnv.getEnvString("crm.call.PBOSSUrl");

        IDataset result = null;

        if (StringUtils.isBlank(url))
        {
            result = CSAppCall.call(svcName, inparams, false);
        }
        else
        {
            result = CSAppCall.call(url, svcName, inparams, false, true);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("pbosssvc result: " + result.toString());
        }

        return result;
    }

    /**
     * 固话号码校验加预占接口
     * 
     * @author yuezy
     * @param x_get_mode
     *            0:释放选占,1：释放实占,2:释放预占
     * @param serial_number
     *            //手机号码
     * @param res_type_code
     *            //资源类型
     * @return
     * @throws Exception
     */
    public static void chkResInfoTT(String resTradeCode, String xGetMode, String resNo, String resTypeCode, String mofficeId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("RES_TRADE_CODE", resTradeCode);
        param.put("X_GETMODE", xGetMode);
        param.put("RES_NO", resNo);
        param.put("MOFFICE_ID", mofficeId);
        param.put("RES_TYPE_CODE", resTypeCode);
        setPublicParam(param);
        callPBOSS("TT.CheckResInfo", param);
    }

    /**
     * @Function:
     * @Description: 商务电话捆绑号码查询
     * @param:resNo 商务电话号码
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-23 下午09:37:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-23 chengxf2 v1.0.0 修改原因
     */
    public static IData getFixedEasyPhoneInfo(String resNo) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("TRADE_TYPE_CODE", "9721");
        param.put("RES_TRADE_CODE", "IGetFixedEasyPhoneInfo");
        param.put("RES_NO", resNo);
        IDataset dataOutput = CSAppCall.call("TT.getResInfo", param);
        return dataOutput.first();
    }

    /**
     * @Function:
     * @Description: 订单撤销申请接口
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-23 下午09:19:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-23 chengxf2 v1.0.0 修改原因
     */
    public static IDataOutput orderCancelApply(String tradeId, String cancelTag, String acceptMonth, String cancelType) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("SUBSCRIBE_ID", tradeId);
        param.put("CANCEL_TAG", cancelTag);
        param.put("ACCEPT_MONTH", acceptMonth);
        param.put("CANCEL_TYPE", cancelType);
        return CSAppCall.callNGPf("PF_ORDER_CANCEL_APPLY", param);
    }

	//REQ201811010006关于NGBOSS系统装机撤单原因推送至装机系统的需求 wuhao5
    public static IDataOutput orderCancelApply(String tradeId, String cancelTag, String acceptMonth, String cancelType, String cancelReasonType, String cancesedReasonType, String remark) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        if(cancelReasonType == null)
        {
        	cancelReasonType = "";
        }else
        {
        	cancelReasonType = StaticUtil.getStaticValue("WIDE_CANCEL_REASON", cancelReasonType);
        }
        if(cancesedReasonType == null)
        {
        	cancesedReasonType = "";
        }else
        {
        	cancesedReasonType = StaticUtil.getStaticValue("WIDE_CANCEL_REASON_RELATION", cancesedReasonType);
        }
        if(remark == null)
        {
        	remark = "";
        }
        param.put("SUBSCRIBE_ID", tradeId);
        param.put("CANCEL_TAG", cancelTag);
        param.put("ACCEPT_MONTH", acceptMonth);
        param.put("CANCEL_TYPE", cancelType);
        param.put("CANCELREASON_TYPE", cancelReasonType);
        param.put("CANCESEDREASON_TYPE", cancesedReasonType);
        param.put("REMARK", remark);
        return CSAppCall.callNGPf("PF_ORDER_CANCEL_APPLY", param);             
        
    }

    /**
     * @Function: releaseMaterial()
     * @Description: 释放物品
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-23 下午2:41:12 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-23 yxd v1.0.0 修改原因
     */
    public static IData releaseMaterial(String materialNo) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckFixedMaterialUsedInfo");
        param.put("RES_NO", materialNo);
        param.put("RES_TYPE_CODE", "W");
        param.put("RES_KIND_CODE", "02");
        param.put("X_GETMODE", "1");
        setPublicParam(param);
        IDataset set = callPBOSS("TT.CheckResInfo", param);
        IData result = new DataMap();
        if (DataSetUtils.isNotBlank(set))
        {
            result = set.getData(0);
        }
        return result;
    }

    /**
     * @Function: releasePhone()
     * @Description: 号码释放
     * @param:xGetMode 0:释放选占,1：释放实占,2:释放预占
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-23 上午11:31:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-23 yxd v1.0.0 修改原因
     */
    public static IData releasePhone(String serialNumber, String xGetMode) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "IRes_Fixed_MphoneRelease");
        param.put("RES_NO", serialNumber);
        param.put("X_GETMODE", xGetMode);
        setPublicParam(param);
        IDataset set = callPBOSS("TT.ResTempOccupy", param);
        IData result = new DataMap();
        if (DataSetUtils.isNotBlank(set))
        {
            result = set.getData(0);
        }
        return result;
    }

    /**
     * @Function: resMaterialOccupy()
     * @Description: 物品实占
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-23 上午11:19:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-23 yxd v1.0.0 修改原因
     */
    public static IData resMaterialOccupy(String materialNo, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckFixedMaterialInfo");
        param.put("RES_TYPE_CODE", "W");
        param.put("RES_NO", materialNo);
        param.put("X_GETMODE", "1");
        param.put("OCCUPY_USER_ID", userId);
        param.put("RES_KIND_CODE", "02");
        setPublicParam(param);
        IDataset set = callPBOSS("TT.CheckResInfo", param);
        IData result = new DataMap();
        if (DataSetUtils.isNotBlank(set))
        {
            result = set.getData(0);
        }
        return result;
    }

    /**
     * @Function: resMaterialOccupy()
     * @Description: 物品实占
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yuezy
     * @date: 2014-7-23 上午11:19:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-23 yxd v1.0.0 修改原因
     */
    public static IData resMaterialOccupyGH(String materialNo, String userId, String resKindCode) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckFixedMaterialInfo");
        param.put("RES_TYPE_CODE", "W");
        param.put("RES_NO", materialNo);
        param.put("X_GETMODE", "1");
        param.put("OCCUPY_USER_ID", userId);
        param.put("RES_KIND_CODE", resKindCode);
        setPublicParam(param);
        IDataset set = callPBOSS("TT.CheckResInfo", param);
        IData result = new DataMap();
        if (DataSetUtils.isNotBlank(set))
        {
            result = set.getData(0);
        }
        return result;
    }

    /**
     * 铁通号码释放接口
     * 
     * @author yuezy
     * @param x_get_mode
     *            0:释放选占,1：释放实占,2:释放预占
     * @param serial_number
     *            //手机号码
     * @param res_type_code
     *            //资源类型
     * @return
     * @throws Exception
     */
    public static void ResOccupyUseTT(String resTradeCode, String xGetMode, String resNo, String resTypeCode) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("RES_TRADE_CODE", resTradeCode);
        param.put("X_GETMODE", xGetMode);
        param.put("RES_NO", resNo);
        param.put("RES_TYPE_CODE", resTypeCode);
        setPublicParam(param);
        callPBOSS("TT.ResTempOccupy", param);
    }

    /*
     * @Function: resPreOccupy()
     * @Description: 商务电话（固话）预占
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-23 上午10:57:00 Modification History: Date Author Version Description
     * ---------------------------------------------------------* 2014-7-23 yxd v1.0.0 修改原因
     */
    public static IData resPreOccupy(String serialNumber, String psptId) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckFixedPhoneInfo");
        param.put("RES_TYPE_CODE", "N");
        param.put("PSPT_ID", psptId);
        param.put("RES_NO", serialNumber);
        param.put("X_GETMODE", "1");
        setPublicParam(param);
        IDataset set = callPBOSS("TT.CheckResInfo", param);
        IData result = new DataMap();
        if (DataSetUtils.isNotBlank(set))
        {
            result = set.getData(0);
        }
        return result;
    }

    /**
     * @Function: resRealOccupy()
     * @Description: 商务电话（固话）实占
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-23 上午11:09:23 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-23 yxd v1.0.0 修改原因
     */
    public static IData resRealOccupy(String serialNumber, String psptId, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckFixedPhoneUsed");
        param.put("RES_TYPE_CODE", "N");
        param.put("PSPT_ID", psptId);
        param.put("RES_NO", serialNumber);
        param.put("X_GETMODE", "1");
        param.put("OCCUPY_USER_ID", userId);
        setPublicParam(param);
        IDataset set = callPBOSS("TT.CheckResInfo", param);
        IData result = new DataMap();
        if (DataSetUtils.isNotBlank(set))
        {
            result = set.getData(0);
        }
        return result;
    }

    /**
     * set公共参数
     * 
     * @param data
     * @throws Exception
     * @CREATE BY GONGP@2013-12-18
     */
    private static void setPublicParam(IData data) throws Exception
    {
        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());// 省编码
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入渠道
        String routeId = CSBizBean.getVisit().getStaffEparchyCode();
        // 客服工号，HNAN, 07XX 则默认到默认地州编码
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
            routeId = Route.getCrmDefaultDb();
        }
        data.put("TRADE_EPARCHY_CODE", routeId);// 受理地州
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 路由地州
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理部门
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 受理员工
        data.put(Route.ROUTE_EPARCHY_CODE, routeId);// 路由地州
        data.put("EPARCHY_CODE", routeId);
    }
    
    /**
     * 宽带改期 调用PBoss接口
     * @param IData input
     * @throws Exception
     * @CREATE BY zhengkai5@2017-9-11
     */
    public static void callPBOSS(IData input) throws Exception{
    	CSAppCall.callNGPf("PBOSS_ORDER_ModifyBookingTime", input);
    }
}
