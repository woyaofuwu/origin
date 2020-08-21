
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.BizVisit;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl.ChnlInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class IBossCall
{
    public static Logger logger = Logger.getLogger(IBossCall.class);

    /**
     * 垃圾短信管理 BELL 添加黑名单
     */
    public static IDataset addBlkListToBell(String serialNumber, String smsTypeCode, String remark) throws Exception
    {
        IData param = new DataMap();
        param.put("ROUTEVALUE", serialNumber);
        param.put("ROUTETYPE", "01");
        param.put("BLK_NUM", "86" + serialNumber);
        param.put("BLK_TYPE", "1");
        param.put("START_TIME", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), "yyyyMMddHHmmss"));
        // param.put("END_TIME", SysDateMgr.getEndCycle20501231() + "235959");
        param.put("END_TIME", SysDateMgr.decodeTimestamp(SysDateMgr.getOtherSecondsOfSysDate(604800), "yyyyMMddHHmmss"));
        param.put("PROVINCE_CODE", 43);//
        param.put("KIND_ID", "BIP2C998_T2102090_0_0");
        // param.put("RULEID", "1234567890123456789012");// 未定

        // HXYD-YZ-REQ-20131216-008关于贝尔实时监控平台加黑原因的优化需 by zhouyl
        String ruleid = (smsTypeCode + "-" + remark).trim();
        if (ruleid.length() > 50)
        {
            param.put("RULEID", ruleid.substring(0, 50));// 未定
        }
        else
        {
            param.put("RULEID", ruleid);// 未定
        }
        IDataset res = dealInvokeUrl("BIP2C998_T2102090_0_0", "IBOSS3", param);
        return null;
    }

    /**
     * 协助处理请求归档
     *
     * @data 2014-2-19
     * @return
     * @throws Exception
     */
    public static IDataset AssistRequestArchival(IData data) throws Exception
    {
        data.put("KIND_ID", "BIP2C123_T2002123_0_0");
        return dealInvokeUrl("BIP2C123_T2002123_0_0", "IBOSS", data);
    }

    /**
     * 协助处理请求催办
     *
     * @data 2014-2-19
     * @return
     * @throws Exception
     */
    public static IDataset AssistRequestHasten(IData data) throws Exception
    {
        data.put("KIND_ID", "BIP2C124_T2002124_0_0");
        return dealInvokeUrl("BIP2C124_T2002124_0_0", "IBOSS", data);
    }

    /**
     * 协助处理请求查询
     *
     * @data 2014-2-19
     * @return
     * @throws Exception
     */
    public static IDataset AssistRequestQurey(IData data) throws Exception
    {
        data.put("KIND_ID", "BIP2C125_T2002125_0_0");
        return dealInvokeUrl("BIP2C125_T2002125_0_0", "IBOSS", data);
    }

    /**
     * 协助处理请求回复(落地报文处理)
     *
     * @data 2014-2-19
     * @return
     * @throws Exception
     */
    public static IDataset AssistRequestReply(IData data) throws Exception
    {
        data.put("KIND_ID", "BIP2C122_T2002122_0_0");
        return dealInvokeUrl("BIP2C122_T2002122_0_0", "IBOSS", data);
    }

    /**
     * 协助处理请求派发
     *
     * @data 2014-2-19
     * @return
     * @throws Exception
     */
    public static IDataset AssistRequestSend(IData data) throws Exception
    {
        data.put("KIND_ID", "BIP2C121_T2002121_0_0");
        return dealInvokeUrl("BIP2C121_T2002121_0_0", "IBOSS", data);
    }

    /**
     * 两不一快写卡信息回写
     */
    public static IDataset backWriteInfo(String transId, String serialNumber, String tempNumber, String imsi, String enck, String encOpc, String signature, String localProvCode) throws Exception
    {
        IData input = new DataMap();
        input.put("TRANS_ID", transId);
        input.put("KIND_ID", "BIP2B026_T2000007_0_0");
        input.put("ROUTETYPE", "01");
        input.put("ROUTEVALUE", serialNumber);
        input.put("TEMP_NUMBER", tempNumber);
        input.put("SERIAL_NUMBER", serialNumber);
        input.put("Result", "0");
        input.put("IMSI", imsi);
        input.put("EncK", enck);
        input.put("EncOpc", encOpc);
        input.put("Signature", signature);
        input.put("LocalProvCode", localProvCode);

        IDataset dataset = dealInvokeUrl("BIP2B026_T2000007_0_0", "IBOSS", input);
        return dataset;
    }

    /**
     * 关联副号码检验
     *
     * @data 2013-12-18
     * @param SERIAL_NUMBER
     * @param MAIN_USER_TYPE
     * @param MAIN_USER_VALUE
     * @param SUB_USER_TYPE
     * @param SUB_USER_VALUE
     * @return
     * @throws Exception
     */
    public static IData BankSignAddSubCheck(String serialNumber, String mainUserType, String mainUserValue, String subUserType, String subNum) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("MAIN_USER_TYPE", mainUserType);
        inData.put("MAIN_USER_VALUE", mainUserValue);
        inData.put("SUB_USER_TYPE", subUserType);
        inData.put("SUB_USER_VALUE", subNum);
        inData.put("KIND_ID", "BIP1A158_T1000165_0_0");
        inData.put("X_TRANS_CODE", "T1000165");

        IDataset dataset = dealInvokeUrl("BIP1A158_T1000165_0_0", "IBOSS2", inData);

        return dataset.getData(0);
    }

    /**
     * 总对总调用一级接口缴费充值
     *
     * @data 2013-12-18
     * @param SERIAL_NUMBER
     * @param TRADE_FEE
     * @param CHANNEL_ID
     * @param PAYMENT_ID
     * @param SUB_ID
     * @return
     * @throws Exception
     */
    public static IData BankSignPay(String serialNumber, String tradeFee, String channelId, String paymentId, String subId, String userType, String payedType, String chnl_type) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("TRADE_FEE", tradeFee);
        inData.put("CHANNEL_ID", channelId);
        inData.put("PAYMENT_ID", paymentId);
        inData.put("USER_TYPE", userType);
        inData.put("PAYED_TYPE", payedType);
        inData.put("SUB_ID", subId);
        inData.put("CNL_TYP", channelId);
        String kindId = "BIP1A164_T1000157_0_0"; // 先默认为非签约缴费
        inData.put("X_TRANS_CODE", "T1000157");

        if ("02".equals(channelId))
        {
            inData.put("KIND_ID", "BIP1A160_T1000157_0_0");
            kindId = "BIP1A160_T1000157_0_0";
        }
        else if ("03".equals(channelId))
        {
            inData.put("KIND_ID", "BIP1A162_T1000157_0_0");
            kindId = "BIP1A162_T1000157_0_0";
        }
        else if ("04".equals(channelId))
        {
            inData.put("KIND_ID", "BIP1A161_T1000157_0_0");
            kindId = "BIP1A161_T1000157_0_0";
        }

        IDataset dataset = dealInvokeUrl(kindId, "IBOSS2", inData);

        return dataset.getData(0);
    }

    /**
     * 无线音乐,手机视频,手机阅读归档
     *
     * @return
     * @throws Exception
     */
    public static IData BussToHisIBOSS(String indictSeq, String remark) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("KIND_ID", "BIP2B322_T2001322_0_0");
        inparam.put("INDICTSEQ", indictSeq);
        inparam.put("HANDLINGDEPT", CSBizBean.getVisit().getDepartId());
        inparam.put("HANDLINGSTAFF", CSBizBean.getVisit().getStaffId());
        inparam.put("HANDLINGCOMMENT", remark);
        inparam.put("TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));

        IDataset res = dealInvokeUrl("BIP2B322_T2001322_0_0", "IBOSS", inparam);
        return res.getData(0);
    }

    /**
     * 便民平台调用IBOSS接口
     *
     * @param visit
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset callBmptAuth(IVisit visit, String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("KIND_ID", "BIP2B063_T2001063_0_0");// 交易唯一标识
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");

        data.put("ID_TYPE", "01");
        data.put("ID_VALUE", serialNumber);
        data.put("BIZ_TYPE_CODE", "43");
        IDataset result = dealInvokeUrl("BIP2B063_T2001063_0_0", "IBOSS", data);
        return result;
    }

    /**
     * 订单撤销接口
     *
     * @param kind_id
     * @param orderId
     * @return
     */
    public static IData callCustomerInfo(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        // pd.setData(param);
        param.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());// 省别编码
        param.put("IN_MODE_CODE", "1");// 接入方式：td_s_static 表 type_id
        // ='OPEN_INMODECODE'

        param.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());// 交易地州编码
        param.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());// 路由地州编码

        param.put("ROUTETYPE", "01");// 路由类型 00-省代码，01-手机号
        param.put("ROUTEVALUE", serialNumber);

        param.put("KIND_ID", "BIP1A102_T1000102_0_0");// 交易唯一标识
        param.put("SERIAL_NUMBER", serialNumber);

        IDataset result = dealInvokeUrl("BIP1A102_T1000102_0_0", "IBOSS", param);
        return result.getData(0);
    }

    /**
     * 订单撤销接口
     *
     * @param kind_id
     * @param orderId
     * @return
     */
    public static IData callCustomerInfo4HaiNan(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        // pd.setData(param);
        param.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());// 省别编码
        param.put("IN_MODE_CODE", "1");// 接入方式：td_s_static 表 type_id
        // ='OPEN_INMODECODE'

        param.put("TRADE_EPARCHY_CODE", "0872");// 交易地州编码
        param.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        param.put(Route.ROUTE_EPARCHY_CODE, "0872");// 路由地州编码

        param.put("ROUTETYPE", "01");// 路由类型 00-省代码，01-手机号
        param.put("ROUTEVALUE", serialNumber);

        param.put("KIND_ID", "BIP1A102_T1000102_0_0");// 交易唯一标识
        param.put("SERIAL_NUMBER", serialNumber);

        IDataset result = dealInvokeUrl("BIP1A102_T1000102_0_0", "IBOSS", param);
        return result.getData(0);
    }

    /**
     * 游戏平台充值操作调用IBOSS接口
     *
     * @param visit
     * @param serialNumber
     * @param operFee
     * @param spCode
     * @param bizCode
     * @return
     * @throws Exception
     */
    public static IDataset callGameAddMoney(IVisit visit, String serialNumber, String operFee, String spCode, String bizCode, String transId) throws Exception
    {
        IData param = new DataMap();
        param.put("ID_TYPE", "01");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("OPER_CODE", "21");// 充值操作
        param.put("CHARGE_VALUE", operFee);// 单位为分
        param.put("BIZ_TYPE_CODE", PlatConstants.PLAT_GAME);
        param.put("SP_CODE", spCode);
        param.put("BIZ_CODE", bizCode);
        param.put("ACCEPT_DATE", SysDateMgr.getSysDate().substring(0, 10).replaceAll("-", ""));
        param.put("CHARGE_FLG", "1");
        param.put("CMGP_CHANNEL", "08");
        param.put("CMGP_CANCELFLAG", "1");
        param.put("KIND_ID", "BIP2B143_T2001109_0_0");
        param.put("ROUTETYPE", "00");// 路由类型
        param.put("ROUTEVALUE", "000");
        param.put("TRANS_ID", transId);
        IDataset result = dealInvokeUrl("BIP2B143_T2001109_0_0", "IBOSS", param);
        return result;
    }

    /**
     * 游戏平台点播操作调用IBOSS接口
     *
     * @param visit
     * @param serialNumber
     * @param spCode
     * @param bizCode
     * @return
     * @throws Exception
     */
    public static IDataset callGameOrderPlay(IVisit visit, String serialNumber, String spCode, String bizCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ID_TYPE", "01");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("OPER_CODE", "25");// 点播操作
        param.put("BIZ_TYPE_CODE", PlatConstants.PLAT_GAME);
        param.put("SP_CODE", spCode);
        param.put("BIZ_CODE", bizCode);
        param.put("ACCEPT_DATE", SysDateMgr.getSysDate().substring(0, 10).replaceAll("-", ""));
        param.put("CHARGE_FLG", "1");
        param.put("CMGP_CHANNEL", "08");
        param.put("CMGP_CANCELFLAG", "1");
        param.put("KIND_ID", "BIP2B143_T2001109_0_0");
        param.put("ROUTETYPE", "00");// 路由类型
        param.put("ROUTEVALUE", "000");

        IDataset result = dealInvokeUrl("BIP2B143_T2001109_0_0", "IBOSS", param);

        return result;
    }


    public static IDataset callHttpKIBOSS(String svcName, IData data) throws Exception
    {

        // data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url = BizEnv.getEnvString("crm.KIBOSS");

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        // out = new String(out.getBytes("UTF-8"));

        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }

        List list = Wade3DataTran.strToList(out);
        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + list);
        }
        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);
        if (logger.isDebugEnabled())
        {
            logger.debug("Wade3DataTran.wade3To4Dataset:" + dataset.toString());
        }
        return dataset;
    }

    public static IDataset callHttpIBOSS(String svcName, IData data) throws Exception
    {

        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url =BizEnv.getEnvString("crm.IBOSSUrl");

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        // out = new String(out.getBytes("UTF-8"));

        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);

        return dataset;
    }

    public static IDataset callHttpIBOSS7(String svcName, IData data) throws Exception
    {

        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url =BizEnv.getEnvString("crm.IBOSSUrl7");

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        // out = new String(out.getBytes("UTF-8"));

        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);

        return dataset;
    }

    public static IDataset callHttpIBOSS8(String svcName, IData data) throws Exception
    {

        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url =BizEnv.getEnvString("crm.IBOSSUrl8");

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        // out = new String(out.getBytes("UTF-8"));

        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);

        return dataset;
    }
    
    public static IDataset callHttpIBOSS9(String svcName, IData data) throws Exception
    {

        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url =BizEnv.getEnvString("crm.IBOSSUrl9");

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        // out = new String(out.getBytes("UTF-8"));

        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);

        return dataset;
    }

    public static IDataset callHttpIBOSS2(String svcName, IData data) throws Exception
    {

        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url = BizEnv.getEnvString("crm.IBOSSUrl2");

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        // out = new String(out.getBytes("UTF-8"));

        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);

        return dataset;
    }

    public static IDataset callHttpIBOSS3(String svcName, IData data) throws Exception
    {

        // data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url = BizEnv.getEnvString("crm.IBOSSUrl3");

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        // out = new String(out.getBytes("UTF-8"));

        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);

        return dataset;
    }

    //电话实名制需求，新增S模块调用
    public static IDataset callHttpIBOSS4(String svcName, IData data) throws Exception
    {
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("IP_REMOTE_ADDR",  CSBizBean.getVisit().getRemoteAddr());//wangsc10-传业务员办理业务的IP地址到IP_REMOTE_ADDR字段给一级BOSS
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));
        String inparams = Wade3DataTran.toWadeString(data);
        String url = "";
        if("1".equals(data.getString("para_code1")))
        {
            url = BizEnv.getEnvString("crm.IBOSSUrl5");
        }else
        {
            url = BizEnv.getEnvString("crm.IBOSSUrl4");
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }
        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }
        List list = Wade3DataTran.strToList(out);
        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);
        return dataset;
    }
    public static IDataset callMobilePayCancel(IVisit visit, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2B260_T2040036_0_0");
        param.put("ROUTETYPE", "00");
        param.put("ROUTEVALUE", "000");

        param.put("IDTYPE", "01");
        param.put("IDVALUE", serialNumber);
        param.put("BIZ_TYPE", "54");
        param.put("OPR_CODE", "01");

        IDataset result = dealInvokeUrl("BIP2B260_T2040036_0_0", "IBOSS", param);

        return result;
    }

    /**
     * 统一积分兑换
     *
     * @param kind_id
     * @param org_id
     * @param org_name
     * @param ord_req_seq
     * @param user_brand
     * @param user_local_prov
     * @param ord_opr_time
     * @param user_score
     * @param item_id
     * @param item_count
     * @param serial_number
     * @param user_level
     * @param cust_name
     * @param deliv_prov
     * @param city
     * @param destrict
     * @param cust_addr
     * @param cust_tel
     * @param cust_addr_code
     * @param deliv_time_req
     * @param item_type
     * @return
     * @throws Exception
     */
    public static IData callScoreConvert(String org_id, String org_name, String ord_req_seq, String user_brand, String user_local_prov, String ord_opr_time, String user_score, String item_id, String item_count, String serial_number,
                                         String user_level, String cust_name, String deliv_prov, String city, String destrict, String cust_addr, String cust_tel, String cust_addr_code, String deliv_time_req) throws Exception
    {

        IData param = new DataMap();
        param.put("KIND_ID", "BIP5A041_T5000041_0_0");
        param.put("ORG_ID", org_id);
        param.put("ORG_NAME", org_name);
        param.put("ORD_REQ_SEQ", ord_req_seq);
        param.put("USER_BRAND", user_brand);
        param.put("USER_LOCAL_PROV", user_local_prov);
        param.put("ORD_OPR_TIME", ord_opr_time);
        param.put("USER_SCORE", user_score);
        param.put("ITEM_ID", item_id);
        param.put("ITEM_COUNT", item_count);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("USER_LEVEL", user_level);
        param.put("STAR_LEVEL", user_level);
        if (StringUtils.isNotBlank(city) && StringUtils.isNotBlank(deliv_prov) && StringUtils.isNotBlank(destrict))
        {
            param.put("CUST_NAME", cust_name);
            param.put("DELIV_PROV", deliv_prov);
            param.put("CITY", city);
            param.put("DESTRICT", destrict);
            param.put("CUST_ADDR", cust_addr);
            param.put("CUST_TEL", cust_tel);
            param.put("CUST_ADDR_CODE", cust_addr_code);
            param.put("DELIV_TIME_REQ", deliv_time_req);
        }
        IDataset dataset = dealInvokeUrl("BIP5A041_T5000041_0_0", "IBOSS", param);

        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * 订单撤销接口
     *
     * @param kind_id
     * @param orderId
     * @return
     * @throws Exception
     */
    public static IData cancelIboss(String orderSubId) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP5A043_T5000043_0_0");
        param.put("SUB_ORDER_ID", orderSubId);
        IDataset dataset = dealInvokeUrl("BIP5A043_T5000043_0_0", "IBOSS", param);

        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * 换卡通知接口
     *
     * @return
     * @throws Exception
     */
    public static IData changCard2NotifyIBOSS(String sn, String oldImsi, String newImsi, String inModeCode, String tradeEparchyCode, String tradeCityCode, String tradeDepartId, String tradeStaffId, String provinceCode, String routeType,
                                              String routeValue) throws Exception
    {
        IData ip = new DataMap();

        ip.put("IDVALUE", sn);
        ip.put("IMSI_CODE", oldImsi);
        ip.put("IMSI", newImsi);
        ip.put("BACK_TAG", "1");
        ip.put("SUCCESS_FLAG", "1");
        ip.put("IN_MODE_CODE", inModeCode);
        ip.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
        ip.put("TRADE_CITY_CODE", tradeCityCode);
        ip.put("TRADE_DEPART_ID", tradeDepartId);
        ip.put("TRADE_STAFF_ID", tradeStaffId);
        ip.put("KIND_ID", "BIP2B099_T2040008_0_0");
        ip.put("PROVINCE_CODE", provinceCode);
        ip.put("ROUTETYPE", "00");
        ip.put("ROUTEVALUE", "000");
        return callHttpIBOSS("TCS_CrmToPlat", ip).getData(0);
    }

    /**
     * 向手机支付业务系统申请用户换卡
     *
     * @return
     * @throws Exception
     */
    public static IDataset changCard2PayPlat(BizVisit visit, String sn) throws Exception
    {
        IData params = new DataMap();
        params.put("KIND_ID", "BIP2B099_T2040007_0_0");
        params.put("X_TRANS_CODE", "TCS_CrmToPlat");
        params.put("PROVINCE_CODE", visit.getProvinceCode());
        params.put("IN_MODE_CODE", visit.getInModeCode());
        params.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        params.put("TRADE_CITY_CODE", visit.getCityCode());
        params.put("TRADE_DEPART_ID", visit.getDepartCode());
        params.put("TRADE_STAFF_ID", visit.getStaffId());
        params.put("ROUTETYPE", "00");
        params.put("ROUTEVALUE", "000");
        params.put("IDVALUE", sn);
        return callHttpIBOSS("TCS_CrmToPlat", params);
    }

    /**
     * 补卡通知接口
     *
     * @return
     * @throws Exception
     */
    public static IData changCardNotifyIBOSS(String sn, String oldImsi, String newImsi, String inModeCode, String tradeEparchyCode, String tradeCityCode, String tradeDepartId, String tradeStaffId, String provinceCode, String routeType,
                                             String routeValue) throws Exception
    {
        IData ip = new DataMap();

        ip.put("IDVALUE", sn);
        ip.put("IMSI_CODE", oldImsi);
        ip.put("IMSI", newImsi);
        ip.put("IN_MODE_CODE", inModeCode);
        ip.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
        ip.put("TRADE_CITY_CODE", tradeCityCode);
        ip.put("TRADE_DEPART_ID", tradeDepartId);
        ip.put("TRADE_STAFF_ID", tradeStaffId);
        ip.put("KIND_ID", "BIP2B098_T2040005_0_0");
        ip.put("PROVINCE_CODE", provinceCode);
        ip.put("ROUTETYPE", routeType);
        ip.put("ROUTEVALUE", routeValue);
        return callHttpIBOSS("TCS_CrmToPlat", ip).getData(0);
    }

    public static IData changcardToIboss(String OprCode, String OldIMSI, String NewIMSI, String OldPriMSISDN, String NewPriMSISDN, String PriProvince, String PriCity, String SecNumList, String EffectiveTime) throws Exception
    {
        IData inparam = new DataMap();

        // inparam.put("IN_MODE_CODE", inModeCode);
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_CITY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getCityCode());
        inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("OldPriMSISDN", OldPriMSISDN);
        inparam.put("NewPriMSISDN", NewPriMSISDN);
        inparam.put("PriProvince", PriProvince);
        inparam.put("ACCEPT_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        inparam.put("NewIMSI", NewIMSI);
        inparam.put("RSRV_VALUE_CODE", "SIMM");
        inparam.put("KIND_ID", "BIP2B158_T2001128_0_0");
        inparam.put("OldIMSI", OldIMSI);
        inparam.put("OprCode", OprCode);
        inparam.put("EFFTT", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));

        return callHttpIBOSS("TCS_CrmToPlat", inparam).getData(0);
    }

    /**
     * 修改手机支付账户的实名
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset changeMobilePayAccountTrueName(String serialNumber, String trueName, String reqNum, String transId) throws Exception
    {
        IData data = new DataMap();
        data.put("KIND_ID", "BIP2B089_T2040033_0_0");// 交易唯一标识
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");

        data.put("REQ_NUM", reqNum);
        data.put("TRANS_ID", transId);
        data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        data.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("NAME", trueName);
        data.put("SERIAL_NUMBER", serialNumber);
        return callHttpIBOSS("IBOSS", data);
    }

    /**
     * 改号取消数据同步到改号平台
     *
     * @param kind_id
     * @param x_trans_code
     * @param old_id_value
     * @param old_imsi
     * @param new_id_value
     * @param new_imsi
     * @param opr_code
     * @param reserve
     * @return
     * @throws Exception
     */
    public static IData changePhoneCancelToPlat(String kind_id, String x_trans_code, String old_id_value, String old_imsi, String new_id_value, String new_imsi, String opr_code, String reserve) throws Exception
    {
        IData inData = new DataMap();
        inData.put("KIND_ID", kind_id); // 交易唯一标识
        inData.put("X_TRANS_CODE", x_trans_code); // 交易编码-IBOSS
        inData.put("OLD_ID_VALUE", old_id_value);
        inData.put("OLD_IMSI", old_imsi);// OldIMSI
        inData.put("NEW_ID_VALUE", new_id_value);
        inData.put("NEW_IMSI", new_imsi); // NewIMSI
        inData.put("OPR_CODE", opr_code); // 02-关联关系取消数据同步到改号平台
        inData.put("RESERVE", reserve);

        IDataset dataset = dealInvokeUrl(kind_id, "IBOSS", inData);

        return dataset.getData(0);
    }

    /**
     * 异地手机支付充值
     *
     * @param reqNum
     * @param transId
     * @param provCode
     * @param serialNumber
     * @param payMoney
     * @return
     * @throws Exception
     */
    public static IDataset chargeMobilePay(String reqNum, String transId, String provCode, String serialNumber, String payMoney) throws Exception
    {
        IData data = new DataMap();
        data.put("KIND_ID", "BIP2B085_T2040015_0_0");// 跨省手机充值,交易唯一标识
        data.put("ROUTETYPE", "00");//
        data.put("ROUTEVALUE", "000");//

        data.put("REQNUM", reqNum);
        data.put("BOSSSEQ", transId);
        data.put("ACTIONTIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        data.put("HOMEPROV", provCode);// 用户归属省代码 F3
        data.put("PROVINCE", Route.getCrmDefaultDb());// 受理省省代码
        data.put("ACTIONID", CSBizBean.getVisit().getDepartId());
        data.put("ACTIONUSERID", CSBizBean.getVisit().getStaffId());
        data.put("MSISDN", serialNumber);
        long amount = 0;
        try
        {
            amount = (long) (Double.valueOf(payMoney) * 1000);
        }
        catch (NumberFormatException e)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "充值金额错误！");
        }
        data.put("PAYED", amount);// 充值金额,单位：厘

        return callHttpIBOSS("IBOSS", data);
    }

    /**
     * xiekl 新增 平台业务 移动公交鉴权
     *
     * @return 订购时opr为1 退订时opr为2
     * @throws Exception
     */
    public static IData checkMobileBusAuth(String opr, String serialNumber, String busCode, String icCode) throws Exception
    {
        IData data = new DataMap();
        data.put("KIND_ID", "BIP1Y001_T1000001_0_0");// 业务鉴权交易唯一标识
        data.put("ROUTETYPE", "00");// 必传
        data.put("ROUTEVALUE", "000");// 必传
        data.put("OPR", opr);
        data.put("MOBILE_NO", serialNumber);
        data.put("BUS_CODE", busCode);
        data.put("IC_CODE", icCode);
        IDataset dataset = dealInvokeUrl("BIP1Y001_T1000001_0_0", "IBOSS", data);

        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;

    }

    /**
     * 台湾副卡号码鉴权
     *
     * @param inModeCode
     * @param serialNumberF
     * @param idCardType
     * @param idCardNum
     * @param routeType
     * @param routeValue
     * @param routeEparchycode
     * @param provinceCode
     * @param acceptDate
     * @param kingId
     * @param userPassWd
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IData checkResourceForMphone(String inModeCode, String serialNumberF, String idCardType, String idCardNum, String routeType, String routeValue, String routeEparchycode, String provinceCode, String acceptDate, String kingId,
                                               String userPassWd) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("IN_MODE_CODE", inModeCode);
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_CITY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getCityCode());
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeEparchycode);
        inparam.put("ROUTETYPE", routeType);
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("SERIAL_NUMBER_F", serialNumberF);
        inparam.put("USER_PASSWD", userPassWd);
        inparam.put("IDCARDTYPE", idCardType);
        inparam.put("IDCARDNUM", idCardNum);
        inparam.put("PROVINCE_CODE", provinceCode);
        inparam.put("ACCEPT_DATE", acceptDate);
        inparam.put("KIND_ID", kingId);

        return callHttpIBOSS("TCS_CrmToPlat", inparam).getData(0);
    }

    /**
     * 两不一快写卡请求校验
     */
    public static IDataset checkUserCardFlow(String transId, String serialNumber, String tempNumber) throws Exception
    {
        IData input = new DataMap();
        input.put("TRANS_ID", transId);
        input.put("KIND_ID", "BIP2B026_T2000005_0_0");
        input.put("ROUTETYPE", "01");
        input.put("ROUTEVALUE", tempNumber);
        input.put("TEMP_NUMBER", tempNumber);
        input.put("SERIAL_NUMBER", serialNumber);

        IDataset dataset = dealInvokeUrl("BIP2B026_T2000005_0_0", "IBOSS", input);
        return dataset;
    }

    public static IData confirmCMMB(String operCode, String serialNumber, String intfTradeId, String bizTypeCode, String spCode, String bizCode, String resultCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ID_TYPE", "01");
        param.put("IDVALUE", serialNumber);
        param.put("INTF_TRADE_ID", intfTradeId);
        param.put("X_RSPCODE", resultCode);
        param.put("OPER_CODE", operCode);
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("SP_CODE", spCode);
        param.put("BIZ_CODE", bizCode);
        param.put("ACCEPT_DATE", SysDateMgr.getSysDate("yyyyMMdd"));
        param.put("KIND_ID", "BIP2B036_T2001025_0_0");
        param.put("ROUTETYPE", "00");
        param.put("ROUTEVALUE", "000");
        param.put("X_RESULTCODE", resultCode);

        IDataset dataset = dealInvokeUrl("BIP2B036_T2001025_0_0", "IBOSS", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;

    }

    /**
     * 平台服务第三方平台确认
     *
     * @param idata
     * @return
     * @throws Exception
     */
    public static IData confirmThirdPlatform(IData idata) throws Exception
    {
        IData param = new DataMap();
        param.put("START_DATE", DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        param.put("OPR_SOURCE", "08");// BOSS
        param.put("CHANNEL_CODE", "99");// 99－本操作与手机搜索平台无关
        param.put("KIND_ID", "BIP6B639_T6001602_0_0");
        param.put("X_TRANS_CODE", "");// 交易编码
        param.putAll(idata);

        IDataset dataset = dealInvokeUrl("BIP6B639_T6001602_0_0", "IBOSS6", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * 确认WLAN电子卡费用
     *
     * @author xiekl
     * @return
     * @throws Exception
     */
    public static IDataset confirmWlanElecCardFee(String oprNum, String serialNumber, String seq, String resultCode, String resultInfo, String fee) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2B280_T2001224_0_0");
        inparam.put("X_TRANS_CODE", "");
        inparam.put("OPR_NUMB", oprNum);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("WLAN_SEQ", seq);
        inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("X_RESULTCODE", resultCode);
        inparam.put("X_RESULTINFO", resultInfo);
        inparam.put("FEE", fee);
        IDataset dataSet = dealInvokeUrl("BIP2B280_T2001224_0_0", "IBOSS", inparam);

        return dataSet;
    }

    /**
     * 全能IBoss接口
     *
     * @param IData
     *            (KIND_ID && more...)
     * @return IData
     * @exception Exception
     * @author hui
     */
    public static IData connetionIboss(IData data) throws Exception
    {
        IDataset dataset = new DatasetList();
        String kindId = data.getString("KIND_ID", "");
        if (IDataUtil.isNotEmpty(data) && StringUtils.isNotBlank(kindId))
        {
            dataset = dealInvokeUrl(kindId, "IBOSS", data);
        }
        if (IDataUtil.isEmpty(dataset))
        {
            return new DataMap();
        }
        return dataset.getData(0);
    }

    public static IData dealArchIboss(IData idata) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2C048_T2002048_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");// 交易编码
        param.putAll(idata);

        IDataset dataset = dealInvokeUrl("BIP2C048_T2002048_0_0", "IBOSS6", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;

    }

    public static IData dealBadInfo4Iboss(IData idata) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2C045_T2101005_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");// 交易编码
        param.putAll(idata);

        IDataset dataset = dealInvokeUrl("BIP2C045_T2101005_0_0", "IBOSS6", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;

    }

    public static IData dealBadInfoIboss(IData idata) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2C050_T2002050_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");// 交易编码
        param.putAll(idata);

        IDataset dataset = dealInvokeUrl("BIP2C050_T2002050_0_0", "IBOSS6", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;

    }

    public static IData dealDedInfoIboss(IData idata) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2C044_T2002044_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");// 交易编码
        param.putAll(idata);

        IDataset dataset = dealInvokeUrl("BIP2C044_T2002044_0_0", "IBOSS6", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * 融合总机欢迎词审核
     *
     * @data 2014-7-4
     * @return
     * @throws Exception
     */
    public static IDataset DealGrpAuditWelcomeVoice(IData inData) throws Exception
    {
        IData temp = new DataMap();
        temp.put("BG_NAME", inData.getString("BG_NAME"));
        temp.put("WORDSID", inData.getString("WORDSID"));
        temp.put("CHECK_STATUS", inData.getString("CHECK_RESULT"));
        temp.put("REASON", inData.getString("REASON"));
        temp.put("KIND_ID", "CTX1A019_T1000001_0_0");

        IDataset dataset = dealInvokeUrl("CTX1A019_T1000001_0_0", "IBOSS8", temp);

        return dataset;
    }

    public static IData dealHLRStateChangeIboss(IData idata) throws Exception
    {

        IDataset dataset = dealInvokeUrl(idata.getString("KIND_ID", ""), "IBOSS", idata);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;

    }

    public static IData dealHLRStopAndOpen(IData ibossData) throws Exception
    {
        IDataset dataset = dealInvokeUrl(ibossData.getString("KIND_ID"), "IBOSS6", ibossData);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    public static IDataset dealIboss(IVisit visit, IData param, String KIND_ID) throws Exception
    {

        param.put("KIND_ID", KIND_ID);// 交易唯一标识
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        IDataset result = dealInvokeUrl(KIND_ID, "IBOSS", param);
        return result;
    }

    /**
     * 因网状网变更,参照一级BOSS逻辑 kind_id和这个TD_B_IBBUSI_SIGN查询出来的busi_sign能对上的，就调用callHttpIBOSS2走网状网, 对不上则调默认的callHttpIBOSS
     * callHttpIBOSS3可以预留给webservice之类的特殊接口
     */
    public static IDataset dealInvokeUrl(String kindId, String ibossUrl, IData param) throws Exception
    {
        // TODO Auto-generated method stub
        IDataset result = null;

        IDataset infos = null;
        if (!"".equals(kindId))
        {
            IData data = new DataMap();
            data.put("BUSI_SIGN", kindId);

            // ucr_uif1用户下TD_B_IBBUSI_SIGN建同义词到cen用户
        }
        param.put("IP_REMOTE_ADDR",  CSBizBean.getVisit().getRemoteAddr());//wangsc10-传业务员办理业务的IP地址到IP_REMOTE_ADDR字段给一级BOSS
        
        if (IDataUtil.isNotEmpty(infos))
        {
            result = callHttpIBOSS2("IBOSS", param);
        }
        else
        {
            if ("IBOSS".equals(ibossUrl))
            {
                result = callHttpIBOSS("IBOSS", param);
            }
            else if ("IBOSS2".equals(ibossUrl))
            {
                result = callHttpIBOSS2("IBOSS", param);
            }
            else if ("IBOSS3".equals(ibossUrl))
            {
                result = callHttpIBOSS3("IBOSS", param);
            }
            else if("IBOSS6".equals(ibossUrl))
            {
                result = callHttpIBOSS6("IBOSS", param);
            }
            else if("IBOSS7".equals(ibossUrl))
            {
                result = callHttpIBOSS7("IBOSS", param);
            }
            else if("IBOSS8".equals(ibossUrl))
            {
                result = callHttpIBOSS8("IBOSS", param);
            }
            else
            {
                result = new DatasetList();
                IData data = new DataMap();
                data.put("X_RESULTCODE", "-1");
                data.put("X_RESULTINFO", "无法识别的ibossUrl:" + ibossUrl);
                result.add(data);
            }
        }

        return result;
    }

    public static IData dealQBadInfoIboss(IData idata) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2C052_T2002052_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");// 交易编码
        param.putAll(idata);

        IDataset dataset = dealInvokeUrl("BIP2C052_T2002052_0_0", "IBOSS6", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;

    }

    public static IData dealQBadnessImpeachInfoIboss(IData idata) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2C046_T2002046_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");// 交易编码
        param.putAll(idata);

        IDataset dataset = dealInvokeUrl("BIP2C046_T2002046_0_0", "IBOSS", param);//k3
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;

    }

    public static IData dealreturnBadIboss(IData idata) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2C049_T2002049_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");// 交易编码
        param.putAll(idata);

        IDataset dataset = dealInvokeUrl("BIP2C049_T2002049_0_0", "IBOSS", param);//k3

        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;

    }

    /**
     * 垃圾短信管理 BELL 删除黑名单
     */
    public static IDataset delBlkListToBell(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("ROUTEVALUE", serialNumber);
        param.put("ROUTETYPE", "01");
        param.put("BLK_NUM", "86" + serialNumber);
        param.put("BLK_TYPE", "1");
        param.put("PROVINCE_CODE", 43);//
        param.put("KIND_ID", "BIP2C999_T2102090_0_0");
        IDataset dataset = dealInvokeUrl("BIP2C999_T2102090_0_0", "IBOSS3", param);

        return null;
    }

    public static IDataset getMPayInfo(String idType, String idValue, String userPasswd, String idCardType, String idCardNum, String startDate, String endDate, String routeType, String mobileNum, String tradeid) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode()); // 省别编码
        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 交易地州编码
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 交易城市代码
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 员工部门编码
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 员工工号
        inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode()); // 路由地州编码
        inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId()); // 部门编号

        inparam.put("X_TRANS_CODE", "IBOSS");// 交易编码
        inparam.put("IDTYPE", idType);
        inparam.put("IDVALUE", idValue);
        inparam.put("USER_PASSWD", userPasswd);
        inparam.put("IDCARDTYPE", idCardType);
        inparam.put("IDCARDNUM", idCardNum);
        /* 0基本资料 1 实时话费 2 账户资料 3账本资料 4帐单资料 5大客户资料 6积分信息 8 业务开通资料 */
        inparam.put("TYPEIDSET", "0");
        inparam.put("START_DATE", SysDateMgr.getSysTime());
        inparam.put("END_DATE", SysDateMgr.getDateForYYYYMMDD(SysDateMgr.END_TIME_FOREVER));
        inparam.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码

        inparam.put("ROUTETYPE", routeType);// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE")))// 路由值
            inparam.put("ROUTEVALUE", mobileNum);
        else
            inparam.put("ROUTEVALUE", CSBizBean.getVisit().getProvinceCode());

        inparam.put("KIND_ID", "BIP2B099_T2040006_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "IBOSS");// 交易编码
        inparam.put("IDVALUE", mobileNum); // 老代码，这里就是手机号码，IDVALUE

        IDataset dataset = dealInvokeUrl("BIP2B099_T2040006_0_0", "IBOSS", inparam);

        return dataset;
    }

    // 获取用户商场数据
    public static IData getScoreShape(String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        param.put("KIND_ID", "BOSS_UPMS_0_0");
        IDataset res = callHttpIBOSS2("TCS_CrmToPlat", param);
        IData returnData = new DataMap();
        if (IDataUtil.isNotEmpty(res))
        {
            returnData = res.getData(0);
        }

        return returnData;
    }

    public static IDataset getSimCardInfo(String serialNumber, String chargeId, String tradeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode()); // 省别编码
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 交易地州编码
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 交易城市代码
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 员工部门编码
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 员工工号
        inparam.put("RES_TYPE_CODE", "I"); // 资源类型[实体卡]
        inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode()); // 路由地州编码
        inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId()); // 部门编号

        inparam.put("ROUTETYPE", "01"); // 00 2013-11-22
        inparam.put("ROUTEVALUE", serialNumber);// "000"); 2013-11-22
        inparam.put("ID_TYPE", "01");
        inparam.put("ID_VALUE", serialNumber);

        inparam.put("KIND_ID", "BIP2B006_T2000001_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "IBOSS");// 交易编码
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("CHARGE_ID", SeqMgr.getChargeId());// 受理省rps系统流水号
        inparam.put("ORIGDOMAIN", "BOSS");
        inparam.put("HOMEDOMAIN", "BOSS");

        // 前台收费业务 云南暂不收费 暂时给0
        inparam.put("TRADE_TYPE_CODE", "0");// 系统业务类型 0-异地补换卡，异地写卡 1-异地缴费
        inparam.put("PAY_MONEY_CODE", "0");// 收款方式 0-现金 1-POS机 2-支票
        inparam.put("NET_TYPE_CODE", "00");// 网别类别
        inparam.put("FEE_ITEM_TYPE_CODE", "0");// 费用明细类型 0-异地补换卡，异地写卡 1-异地缴费
        inparam.put("IBAFEE", "0");// 手续费
        inparam.put("IBBFEE", "0");// 本金
        inparam.put("IBSFEE", "0");// 服务费
        inparam.put("CANCEL_TAG", "0");
        inparam.put("PROCID", SeqMgr.getTradeId());

        IDataset dataset = dealInvokeUrl("BIP2B006_T2000001_0_0", "IBOSS", inparam);

        return dataset;
    }

    /**
     * 两不一快请求写卡数据
     */
    public static IDataset getWriteCardInfo(String transId, String serialNumber, String tempNumber, String cardSn, String iccid) throws Exception
    {
        IData input = new DataMap();
        input.put("TRANS_ID", transId);
        input.put("KIND_ID", "BIP2B026_T2000006_0_0");
        input.put("ROUTETYPE", "01");
        input.put("ROUTEVALUE", serialNumber);
        input.put("TEMP_NUMBER", tempNumber);
        input.put("SERIAL_NUMBER", serialNumber);
        input.put("Result", "0");
        input.put("CARDSN", cardSn);
        input.put("ICCID", iccid);

        IDataset dataset = dealInvokeUrl("BIP2B026_T2000006_0_0", "IBOSS", input);
        return dataset;

    }

    /**
     * @param serialNumber
     * @param opr
     * @param routeType
     * @param routeValue
     * @param provinceCode
     * @param kindId
     * @param provinceA
     * @param tradeCityCode
     * @param outGroupId
     * @param outNetType
     * @param efftt
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IData HKOneCardMuilNumber(String imsi, String serialNumber, String opr, String routeType, String routeValue, String provinceCode, String kindId, String provinceA, String tradeCityCode, String outGroupId, String outNetType, String efftt)
            throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("IMSI", imsi);
        inparam.put("ROUTETYPE", routeType);
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PROVINCE_CODE", provinceCode);
        inparam.put("KIND_ID", kindId);
        inparam.put("OPR", opr);
        inparam.put("PROVINCE_A", provinceA);
        inparam.put("TRADECITYCODE", tradeCityCode);
        inparam.put("OUT_GROUP_ID", outGroupId);
        inparam.put("OUT_NET_TYPE", outNetType);
        inparam.put("EFFTT", efftt);

        return callHttpIBOSS("TCS_CrmToPlat", inparam).getData(0);
    }

    /**
     * @param imsi
     * @param serialNumberA
     * @param serialNumber
     * @param opr
     * @param routeType
     * @param routeValue
     * @param resTradeType
     * @param provinceCode
     * @param latencyFeeSum
     * @param kindId
     * @param fee
     * @param provinceA
     * @param tradeCityCode
     * @param outGroupId
     * @param outNetType
     * @param efftt
     * @param validTo
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IData HKOneCardMuilNumber(String imsi, String serialNumberA, String serialNumber, String opr, String routeType, String routeValue, String resTradeType, String provinceCode, String latencyFeeSum, String kindId, String fee,
                                            String provinceA, String tradeCityCode, String outGroupId, String outNetType, String efftt, String validTo) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("ROUTETYPE", routeType);
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("SERIAL_NUMBER_A", serialNumberA);
        inparam.put("IMSI", imsi);
        inparam.put("PROVINCE_CODE", provinceCode);
        inparam.put("KIND_ID", kindId);
        inparam.put("OPR", opr);
        inparam.put("RES_TRADE_TYPE", resTradeType);
        inparam.put("FEE", fee);
        inparam.put("LATENCY_FEE_SUM", latencyFeeSum);
        inparam.put("PROVINCE_A", provinceA);
        inparam.put("TRADECITYCODE", tradeCityCode);
        inparam.put("OUT_GROUP_ID", outGroupId);
        inparam.put("OUT_NET_TYPE", outNetType);
        inparam.put("EFFTT", efftt);
        inparam.put("VALID_TO", validTo);

        return callHttpIBOSS("TCS_CrmToPlat", inparam).getData(0);
    }

    /**
     *
     *
     * @param imsi
     * @param serialNumber
     * @param opr
     * @param routeType
     * @param routeValue
     * @param provinceCode
     * @param kindId
     * @param resultCode
     * @param resultDesc
     * @return
     * @throws Exception
     */
    public static IData HKOneCardMuilNumber(String imsi,String serialNumber, String opr, String routeType, String routeValue, String provinceCode, String kindId, String resultCode, String resultDesc)
            throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("IMSI", imsi);
        inparam.put("ROUTETYPE", routeType);
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PROVINCE_CODE", provinceCode);
        inparam.put("KIND_ID", kindId);
        inparam.put("OPR", opr);
        inparam.put("RESULT_CODE", resultCode);
        inparam.put("RESULT_DESC", resultDesc);

        return callHttpIBOSS("TCS_CrmToPlat", inparam).getData(0);
    }


    public static IDataset ibossTwoCheckBack(IData data) throws Exception
    {
        data.put("KIND_ID", "BIP6B637_T6001601_0_0");
        return callHttpIBOSS("IBOSS6", data);
    }

    /**
     * 二次确认FOR铃音盒
     */
    public static IDataset ibossTwoCheckBack4LYH(IData data) throws Exception
    {
        data.put("KIND_ID", "BIP5B044_T5101017_0_0");
        return callHttpIBOSS7("IBOSS", data);
    }

    /**
     * @data 2013-8-20
     * @param routeType
     * @param serialNumber
     * @param provinceCode
     * @param idCardType
     * @param idCardNum
     * @param userName
     * @param contactAddress
     * @param contactPhone
     * @param operateDate
     * @return
     * @throws Exception
     */
    public static IData InfoRegisterAdviceSubmitInfoIBOSS(String routeType, String serialNumber, String provinceCode, String idCardType, String idCardNum, String userName, String contactAddress, String contactPhone, String operateDate)
            throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2B012_T2101010_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        inparam.put("ROUTETYPE", routeType);
        if (inparam.getString("ROUTETYPE").equals("01"))// 路由值
            inparam.put("ROUTEVALUE", serialNumber);
        else
            inparam.put("ROUTEVALUE", provinceCode);

        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PSPT_TYPE_CODE", idCardType);
        inparam.put("PSPT_ID", idCardNum);
        inparam.put("CUST_NAME", userName);
        inparam.put("PSPT_ADDR", contactAddress);
        inparam.put("PHONE", contactPhone);
        inparam.put("OPER_TIME", operateDate);

        IDataset dataset = dealInvokeUrl("BIP2B012_T2101010_0_0", "IBOSS", inparam);

        return dataset.getData(0);
    }

    /**
     * @Function: invokeIBossInterface
     * @Description: 接口调用界面调用IBOSS接口
     * @param: @param param
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 下午03:04:47 2013-8-3 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-8-3 longtian3 v1.0.0 TODO:
     */
    public static IDataset invokeIBossInterface(IData param) throws Exception
    {

        IDataset dataset = dealInvokeUrl(param.getString("KIND_ID", ""), "IBOSS", param);

        return dataset;
    }

    public static IDataset isExistVerify(String provCodes, String serialNumber, String seqTradeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("ID_TYPE", "01");
        inparam.put("ID_VALUE", serialNumber);

        inparam.put("KIND_ID", "BIP2B008_T2000003_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "IBOSS");// 交易编码
        inparam.put("CHARGE_ID", seqTradeId);// 多号共存验证流水号 暂时和procid相同
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PROVINCE_NO", provCodes + "0");
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", provCodes);

        IDataset dataset = dealInvokeUrl("BIP2B008_T2000003_0_0", "IBOSS", inparam);

        return dataset;

    }

    /**
     * 移动渠道办理解约信息同步
     *
     * @data 2013-12-18
     * @return
     * @throws Exception
     */
    public static IDataset MainsignCancelSync(IData inData) throws Exception
    {
        inData.put("KIND_ID", "BIP1A155_T1000153_0_0");
        inData.put("X_TRANS_CODE", "T1000153");
        IDataset dataset = dealInvokeUrl("BIP1A155_T1000153_0_0", "IBOSS2", inData);

        return dataset;
    }

    /**
     * 自动解约信息同步
     *
     * @data 2013-12-18
     * @return
     * @throws Exception
     */
    public static IDataset MainsignCancelSyncByAuto(IData inData) throws Exception
    {
        inData.put("KIND_ID", "BIP1A157_T1000153_0_0");
        inData.put("X_TRANS_CODE", "T1000153");
        IDataset dataset = dealInvokeUrl("BIP1A157_T1000153_0_0", "IBOSS2", inData);

        return dataset;
    }

    /**
     * 签约信息变更同步
     *
     * @data 2013-12-18
     * @return
     * @throws Exception
     */
    public static IDataset MainsignChangeSync(IData inData) throws Exception
    {
        inData.put("KIND_ID", "BIP1A154_T1000153_0_0");
        inData.put("X_TRANS_CODE", "T1000153");
        IDataset dataset = dealInvokeUrl("BIP1A154_T1000153_0_0", "IBOSS2", inData);

        return dataset;
    }

    /**
     * 签约信息同步
     *
     * @data 2013-12-18
     * @return
     * @throws Exception
     */
    public static IDataset MainsignSync(IData inData) throws Exception
    {
        inData.put("KIND_ID", "BIP1A151_T1000153_0_0");
        inData.put("X_TRANS_CODE", "T1000153");
        IDataset dataset = dealInvokeUrl("BIP1A151_T1000153_0_0", "IBOSS2", inData);

        return dataset;
    }

    /**
     * 手机电视通知接口
     *
     * @return
     * @throws Exception
     */
    public static IData mbVisionNotifyIBOSS(String tradeId, String oldImsi, String newImsi, String sn) throws Exception
    {
        IData ip = new DataMap();

        IData params = new DataMap();
        params.put("KIND_ID", "BIP2B404_T2101054_0_0");
        params.put("OPR_NUMB", tradeId); // td.getString ( "TRADE_ID" , "" ) ) ;
        // //td.getTradeId());
        params.put("OLD_IMSI", oldImsi);
        params.put("NEW_IMSI", newImsi);
        params.put("ROUTETYPE", "00");
        params.put("ROUTEVALUE", "000");
        params.put("MSISDN", sn);

        IDataset dataset = dealInvokeUrl("BIP2B404_T2101054_0_0", "IBOSS7", params);

        return dataset.getData(0);
    }

    public static IDataset operSMSTimeOut(String idType, String serialNumber, String transId, String rspcode, String routeEparchyCOde, String tradeEparchyCOde) throws Exception
    {
        IData iData = new DataMap();
        iData.put("KIND_ID", "BIP2B082_T2040025_0_0");// 小额授信资料查询结果
        iData.put("ID_TYPE", idType);
        iData.put("SERIAL_NUMBER", serialNumber);
        iData.put("TRANS_ID", transId);
        iData.put("RSP_CODE", rspcode);// 给IBOSS的返回标识 00-用户回复授权 01- 用户返回不同意
        // 02-超时
        // iData.put("ROUTEss_EPARCHY_CODE", routeEparchyCOde);//
        // 由于IBOSS不传任何参数，CRM侧只能设为INTF
        iData.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCOde); // 路由地州编码
        iData.put("TRADE_EPARCHY_CODE", tradeEparchyCOde);

        IDataset dataset = dealInvokeUrl("BIP2B082_T2040025_0_0", "IBOSS", iData);

        return dataset;
    }

    /**
     * 临时卡预销
     */
    public static IDataset preDestory(String transId, String serialNumber, String tempNumber, String iccid, String imsi) throws Exception
    {
        IData input = new DataMap();
        input.put("TRANS_ID", transId);
        input.put("KIND_ID", "BIP2B026_T2101009_0_0");
        input.put("ROUTETYPE", "01");
        input.put("ROUTEVALUE", tempNumber);
        input.put("TEMP_NUMBER", tempNumber);
        input.put("SERIAL_NUMBER", serialNumber);
        input.put("Result", "0");
        input.put("ICCID", iccid);
        input.put("IMSI", imsi);

        IDataset dataset = dealInvokeUrl("BIP2B026_T2101009_0_0", "IBOSS", input);
        return dataset;
    }

    /**
     * 融合总机欢迎词查询
     *
     * @data 2014-7-4
     * @return
     * @throws Exception
     */
    public static IDataset QryGrpAuditWelcomeVoice(IData inData) throws Exception
    {
        IData temp = new DataMap();
        temp.put("CUST_ID", inData.getString("CUST_ID"));
        temp.put("PRODUCT_ID", inData.getString("PRODUCT_ID"));// 融合总机
        temp.put("USER_ID", inData.getString("USER_ID"));
        temp.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());
        temp.put("KIND_ID", "CTX1A016_T1000001_0_0");
        temp.put("BG_NAME", inData.getString("BG_NAME"));

        IDataset dataset = dealInvokeUrl("CTX1A016_T1000001_0_0", "IBOSS8", temp);

        return dataset;
    }

    /**
     * 查询手机支付绑定银行卡信息
     *
     * @param reqNum
     *            交易码
     * @param transId
     *            交易流水
     * @param serialNumber
     *            手机号
     * @return
     * @throws Exception
     */
    public static IDataset queryAccountBindBankInfos(String reqNum, String transId, String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("KIND_ID", "BIP2B088_T2040032_0_0");
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");
        data.put("REQ_NUM", reqNum);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("TRANS_ID", transId);
        data.put("ACTION_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        data.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("STAFF_ID", CSBizBean.getVisit().getStaffId());

        return callHttpIBOSS("IBOSS", data);
    }

    /**
     * 查询可用应用软件包
     *
     * @data 2013-8-1
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IData queryApplicationSoftwareIBOSS(String serialNumber) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP1A117_T1000117_0_0");
        inparam.put("PHONENUM", serialNumber);
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("TRADE_DEPART_PASSWD", "");
        inparam.put("IN_MODE_CODE", "0");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    public static IData queryBasicInfo(BizVisit visit, String serialNumber, String idCardType, String idCardNum) throws Exception
    {
        IData param = new DataMap();
        param.put("PROVINCE_CODE", visit.getDepartCode());// 省别编码
        param.put("IN_MODE_CODE", visit.getInModeCode());
        // 接入方式0 营业厅 1 客服(callcenter)2 网上客服 3 网上营业厅 4 银行 5 短信平台 6 一级BOSS 7 手机支付
        // 8 统一帐户服务系统(uasp)
        // 9 短信营销/短信营业厅/短信代办 A 触摸屏 B 自助打印机 C 多媒体 D 自助营业厅 E 个人代扣/银行代扣 F 电话开通 G
        // 168点播信息
        // H 空中充值 I 积分平台 J 彩铃接口 K 梦网接口 L WAP接口 M 大客户接口 N 电信卡余额 O 家校通 P 缴费卡缴费 Q
        // 手机钱包 R POS机缴费

        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 交易地州编码
        param.put("TRADE_CITY_CODE", visit.getCityCode());// 交易城市代码
        param.put("TRADE_DEPART_ID", visit.getDepartId());// 员工部门编码
        param.put("TRADE_STAFF_ID", visit.getStaffId());// 员工城市编码
        param.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());// 路由地州编码
        param.put("ROUTETYPE", "01");// 路由类型 00-省代码，01-手机号
        param.put("ROUTEVALUE", serialNumber);

        param.put("KIND_ID", "BIP1A001_T1000002_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");// 交易编码

        param.put("SERIAL_NUMBER", serialNumber);
        param.put("IDTYPE", "01"); // 01 手机号
        param.put("IDVALUE", serialNumber); // 根据IDTYPE设置对应的值
        param.put("IDCARDTYPE", idCardType);
        param.put("IDCARDNUM", idCardNum);
        param.put("USER_PASSWD", ""); // 有证件，这个可以没有
        param.put("TYPEIDSET", "0"); /* 0 基本资料 */
        param.put("START_DATE", ""); // 可以不传
        param.put("END_DATE", ""); // 可以不传

        IDataset dataset = dealInvokeUrl("BIP1A001_T1000002_0_0", "IBOSS7", param);
        return dataset.getData(0);
    }

    /**
     * 无线音乐,手机视频,手机阅读查询
     *
     * @return
     * @throws Exception
     */
    public static IData queryBussQureySeriveIBOSS(String kindId, String indictSeq, String svcTypeId, String provinceID, String svcCity, String originTime, String contactChannel, String operateConditions, String serNumber, String serviceTypeId,
                                                  String operateTypeId, String currPage) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("KIND_ID", kindId);
        inparam.put("INDICTSEQ", indictSeq);
        inparam.put("CALLERNO", serNumber);
        inparam.put("SVCTYPEID", svcTypeId);
        inparam.put("HOMEPROV", provinceID);
        inparam.put("SVCCITY", svcCity);
        inparam.put("QUERYPAGENUM", currPage);
        inparam.put("ORIGINTIME", originTime);
        inparam.put("ACCEPTTIME", originTime);
        inparam.put("ACCEPTSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());
        inparam.put("CONTACTCHANNEL", contactChannel);
        inparam.put("SERVICETYPEID", serviceTypeId);
        inparam.put("OPERATETYPEID", operateTypeId);
        inparam.put("OPERATECONDITIONS", operateConditions);
        inparam.put("SERIAL_NUMBER", serNumber);//记录手机号码，方便IBOSS查日志
        IDataset dataset = dealInvokeUrl(kindId, "IBOSS", inparam);
        return dataset.getData(0);
    }
    public static IData queryBussQureySeriveIBOSS(String kindId, String indictSeq, String svcTypeId, String provinceID, String svcCity, String originTime, String contactChannel, String operateConditions, String serNumber, String serviceTypeId,
                                                  String operateTypeId, String currPage,String subsLevel) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("KIND_ID", kindId);
        inparam.put("INDICTSEQ", indictSeq);
        inparam.put("CALLERNO", serNumber);
        inparam.put("SVCTYPEID", svcTypeId);
        inparam.put("HOMEPROV", provinceID);
        inparam.put("SVCCITY", svcCity);
        inparam.put("QUERYPAGENUM", currPage);
        inparam.put("ORIGINTIME", originTime);
        inparam.put("ACCEPTTIME", originTime);
        inparam.put("ACCEPTSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());
        inparam.put("CONTACTCHANNEL", contactChannel);
        inparam.put("SERVICETYPEID", serviceTypeId);
        inparam.put("OPERATETYPEID", operateTypeId);
        inparam.put("OPERATECONDITIONS", operateConditions);
        inparam.put("SUBSLEVEL", subsLevel);
        inparam.put("SERIAL_NUMBER", serNumber);//记录手机号码，方便IBOSS查日志

        IDataset dataset = dealInvokeUrl(kindId, "IBOSS", inparam);
        return dataset.getData(0);
    }

    /**
     * 查询 DM机卡配对关系
     *
     * @data 2013-8-8
     * @param phonenum
     * @param imeinum
     * @param kindid
     * @return
     * @throws Exception
     */
    public static IData queryDMBusiCardSelIBOSS(String phonenum, String imeinum, String kindid) throws Exception
    {

        IData inparam = new DataMap();
        if (phonenum != null && phonenum.trim().length() != 0)
        {
            inparam.put("PHONENUM", phonenum);
        }

        if (imeinum != null && imeinum.trim().length() != 0)
        {
            inparam.put("IMEINUM", imeinum);
        }

        if (kindid != null && kindid.trim().length() != 0)
        {
            inparam.put("KIND_ID", kindid);
        }
        else
        {
            inparam.put("KIND_ID", "BIP1A113_T1000113_0_0");
        }
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 查询 历史机卡配对关系
     *
     * @data 2013-8-8
     * @param phonenum
     * @param imeinum
     * @return
     * @throws Exception
     */
    public static IData queryDMBusiHCardIBOSS(String phonenum, String imeinum) throws Exception
    {

        IData inparam = new DataMap();

        if (phonenum != null && phonenum.trim().length() != 0)
        {
            inparam.put("PHONENUM", phonenum);
        }
        if (imeinum != null && imeinum.trim().length() != 0)
        {
            inparam.put("IMEINUM", imeinum);
        }

        inparam.put("KIND_ID", "BIP1A114_T1000114_0_0");
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 查询 DM锁定|解锁|数据清除
     *
     * @data 2013-8-8
     * @param operType
     * @param imeiStr
     * @param operateId
     * @param provCode
     * @return
     * @throws Exception
     */
    public static IData queryDMTradeLockIBOSS(String operType, String imeiStr, String operateId, String provCode, String kindId) throws Exception
    {

        IData inparam = new DataMap();

        inparam.put("IMEINUM", imeiStr);
        inparam.put("KIND_ID", kindId);
        inparam.put("OPERATEID", operateId);
        inparam.put("APPLY_TYPE", operType);
        inparam.put("PROVINCE_CODE", provCode);
        inparam.put("TRADE_DEPART_PASSWD", "");
        inparam.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());
        inparam.put("PROV_CODE", provCode);
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("X_RSPTYPE", "");
        inparam.put("X_RSPCODE", "");
        inparam.put("X_RSPDESC", "");
        inparam.put("X_TRANS_CODE", "ITF_IBOT_INSDMDEAL");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 查询可用固件回退包
     *
     * @data 2013-8-1
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IData queryFirmwareReturnIBOSS(String serialNumber) throws Exception
    {

        IData inparam = new DataMap();

        inparam.put("KIND_ID", "BIP1A116_T1000116_0_0");
        inparam.put("PHONENUM", serialNumber);
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("TRADE_DEPART_PASSWD", "");
        inparam.put("IN_MODE_CODE", "0");
        inparam.put("X_TRANS_CODE", "ITF_IBOQ_DMDEAL");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 查询可用固件升级包
     *
     * @data 2013-8-1
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IData queryFirmwareUpgradeIBOSS(String serialNumber) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP1A115_T1000115_0_0");
        inparam.put("PHONENUM", serialNumber);
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("TRADE_DEPART_PASSWD", "");
        inparam.put("IN_MODE_CODE", "0");
        inparam.put("X_TRANS_CODE", "ITF_IBOQ_DMDEAL");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 自有业务查询
     *
     * @param pd
     * @param td
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryFromIBOSS(String indictSeq, String svcTypeID, String operateConditions, String serialNumber, String custName, String oprSource, String bizTypeCode, String operCode) throws Exception
    {
        IData data = new DataMap();

        data.put("KIND_ID", "BIP2C092_T2002092_0_0");// 交易唯一标识
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");
        data.put("SVCTYPEID", svcTypeID);
        data.put("OPERATECONDITIONS", operateConditions);
        data.put("INDICTSEQ", indictSeq);
        /**
         * 受理渠道 01 10086人工;02 10086自动;03 网站;04 短信营业厅;05 掌上营业厅（WAP）;06 USSD;07 自助终端;08 营业厅;09 外呼;10 其他渠道;11 信产部转办; 12
         * 信产部立案;13 客户来电;14 客户来函;15 客户来访;16 集团热线;17 中消协转;18 工信部自查;19 信产部清算司;20 信产部服务处;21 质量万里行; 22 Email;23 国漫热线;24
         * 终端服务厂商;25 总部转办;26 工信部化解;27 总部督办;28 10088人工;29 10088自动;30 315平台;32 热线自查;33 互联网监控; 34 10086官方微博;35 综合部敏感信息;36
         * CEO信箱;37 10086999短信平台;
         */
        data.put("CONTACTCHANNEL", oprSource);
        data.put("CALLERNO", serialNumber);
        data.put("CALLEDNO", "");// 被叫号码
        data.put("SUBSNAME", custName);
        data.put("SUBSLEVEL", "");// 客户级别 01 钻石卡客户;02 金卡客户;03 银卡客户;04 普通客户;
        data.put("SUBSBRAND", "");// 客户品牌编码 01 全球通;02 动感地带;03
        // 神州行（所有地方性品牌均纳入神州行品牌）;04 外省移动客户;05 他网;
        data.put("HOMEPROV", Route.getCrmDefaultDb());
        data.put("SVCCITY", CSBizBean.getVisit().getCityCode() + "-" + CSBizBean.getVisit().getCityName());
        data.put("ORIGINTIME", SysDateMgr.getSysTime());// yyyy-MM-dd
        // HH:mm:ss，省客服收到用户查询的时间
        data.put("ACCEPTTIME", SysDateMgr.getSysTime());// 本查询工单提交时间yyyy-MM-dd
        // HH:mm:ss
        data.put("ACCEPTSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());// “编码-描述”形式
        data.put("SERVICETYPEID", bizTypeCode);// 业务类别,参考“业务类别编码表”
        data.put("OPERATETYPEID", operCode);// 操作类型,参考“操作类型编码表”

        IDataset dataset = dealInvokeUrl("BIP2C092_T2002092_0_0", "IBOSS", data);

        return dataset;
    }

    /**
     * --TODO 接口名名需要更加明确
     *
     * @param indictSeq
     *            服务请求标识
     * @param curPage
     * @return
     * @throws Exception
     *             xiekl
     */
    public static IDataset queryFromIBOSSLeft(String indictSeq, int curPage) throws Exception
    {
        IData data = new DataMap();

        data.put("KIND_ID", "BIP2C093_T2002093_0_0");// 交易唯一标识 未到达业务查询结果获取
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");
        data.put("INDICTSEQ", indictSeq);// 服务请求标识
        data.put("QUERYPAGENUM", curPage);// 请求页码
        IDataset dataset = dealInvokeUrl("BIP2C093_T2002093_0_0", "IBOSS", data);

        return dataset;
    }

    // 库存查询
    public static IDataset queryGiftCount(String itemId, String provId, String cityId, String district) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP5A044_T5000044_0_0");
        param.put("ORG_ID", "0003");
        param.put("ITEM_ID", itemId);
        param.put("P_PROV", provId);
        // 增加地市、区县编码
        param.put("CITY_ID", cityId);
        param.put("DISTRICT", district);
        IDataset dataset = dealInvokeUrl("BIP5A044_T5000044_0_0", "IBOSS", param);

        return dataset;
    }

    /**
     * 普通订单查询接口
     *
     * @param kind_id
     * @param orderId
     * @return
     * @throws Exception
     */
    public static IData queryIboss(String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP1A008_T1000018_0_0");
        param.put("ORDER_ID", orderId);

        IDataset dataset = dealInvokeUrl("BIP1A008_T1000018_0_0", "IBOSS", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * 支持积分商城积分现金混合支付需求
     *
     * @param kind_id
     * @param orderId
     * @return
     * @throws Exception
     */
    public static IData queryIboss(String kindId, String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", kindId);
        param.put("ORDER_ID", orderId);

        IDataset dataset = dealInvokeUrl("BIP5A045_T5000045_0_0", "IBOSS", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * 查询 可采集或可配置业务列表
     *
     * @data 2013-8-10
     * @param provCode
     * @return
     * @throws Exception
     */
    public static IData queryInfoGatherIBOSS(String provCode) throws Exception
    {

        IData inparam = new DataMap();

        inparam.put("KIND_ID", "BIP1A112_T1000112_0_0");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("PROV_CODE", provCode);
        inparam.put("ROUTETYPE", "00");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 国漫日套餐业务
     *
     * @param imsi
     * @param serialNumber
     * @param oprCode
     * @param routeType
     * @param routeValue
     * @param kindId
     * @param provinceA
     * @param outGroupId
     * @param outNetType
     * @param efftt
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IData QueryInterRoamDay(String imsi, String serialNumber, String oprCode, String routeType, String routeValue, String kindId, String provinceA, String outGroupId, String outNetType, String efftt) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("ROUTETYPE", routeType);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("IMSI", imsi);
        inparam.put("KIND_ID", kindId);
        inparam.put("OPR", oprCode);
        inparam.put("PROVINCE_A", provinceA);
        inparam.put("TRADECITYCODE", CSBizBean.getTradeEparchyCode());
        inparam.put("OUT_GROUP_ID", outGroupId);
        inparam.put("OUT_NET_TYPE", outNetType);
        inparam.put("EFFTT", efftt);

        IDataset dataset = dealInvokeUrl(kindId, "IBOSS", inparam);

        return dataset.getData(0);
    }

    /**
     * IVR拨打记录查询
     *
     * @data 2013-8-1
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IData queryMdoIvrOneIBOSS(String king, String serialNumber, String routeType, String routeValue, String operNunb, String exBeginTime, String exEndTime, String oprSource, String custName, String provCode) throws Exception
    {

        IData data = new DataMap();

        data.put("KIND_ID", king);// 交易唯一标识,IVR拨打记录查询
        data.put("ROUTETYPE", routeType);
        data.put("ROUTEVALUE", routeValue);
        data.put("INDICTSEQ", operNunb);
        data.put("MSISDN", serialNumber);
        data.put("QUERYSTARTTIME", exBeginTime);
        data.put("QUERYENDTIME", exEndTime);
        data.put("CONTACTCHANNEL", oprSource);
        data.put("CALLERNO", serialNumber);
        data.put("CALLEDNO", "");
        data.put("SUBSNAME", custName);
        data.put("SUBSLEVEL", "");
        data.put("SUBSBRAND", "");
        data.put("SVCTYPEID", "10010399");
        data.put("HOMEPROV", provCode);
        data.put("SVCCITY", CSBizBean.getVisit().getCityCode() + "-" + CSBizBean.getVisit().getCityName());
        data.put("ORIGINTIME", SysDateMgr.getSysTime());
        data.put("ACCEPTTIME", SysDateMgr.getSysTime());
        data.put("ACCEPTSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());
        IDataset dataset = dealInvokeUrl(king, "IBOSS", data);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    public static IData queryMdoIvrThreeIBOSS(String king, String serialNumber, String routeType, String routeValue, String operNunb, String exBeginTime, String exEndTime, String oprSource, String custName, String provCode) throws Exception
    {

        IData data = new DataMap();

        data.put("KIND_ID", king);// 交易唯一标识,IVR拨打记录查询
        data.put("ROUTETYPE", routeType);
        data.put("ROUTEVALUE", routeValue);

        data.put("INDICTSEQ", operNunb);
        data.put("MSISDN", serialNumber);
        data.put("QUERYSTARTTIME", exBeginTime);
        data.put("QUERYENDTIME", exEndTime);
        data.put("CONTACTCHANNEL", oprSource);
        data.put("CALLERNO", serialNumber);
        data.put("CALLEDNO", "");
        data.put("SUBSNAME", custName);
        data.put("SUBSLEVEL", "");
        data.put("SUBSBRAND", "");
        data.put("SVCTYPEID", "10010399");
        data.put("HOMEPROV", provCode);
        data.put("SVCCITY", CSBizBean.getVisit().getCityCode() + "-" + CSBizBean.getVisit().getCityName());
        data.put("ORIGINTIME", SysDateMgr.getSysTime());
        data.put("ACCEPTTIME", SysDateMgr.getSysTime());
        data.put("ACCEPTSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());
        data.put("HANDLINGDEPT", CSBizBean.getVisit().getDepartId() + "-" + CSBizBean.getVisit().getDepartName());
        data.put("HANDLINGSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());
        data.put("HANDLINGCOMMENT", "OK");
        data.put("TIME", SysDateMgr.getSysTime());
        IDataset dataset = dealInvokeUrl(king, "IBOSS", data);
        return dataset.getData(0);
    }

    public static IData queryMdoIvrTwoIBOSS(String king, String serialNumber, String routeType, String routeValue, String operNunb, String exBeginTime, String exEndTime, String oprSource, String custName, String provCode, int queryPageNum)
            throws Exception
    {

        IData data = new DataMap();

        data.put("KIND_ID", king);// 交易唯一标识,IVR拨打记录查询
        data.put("ROUTETYPE", routeType);
        data.put("ROUTEVALUE", routeValue);

        data.put("INDICTSEQ", operNunb);
        data.put("MSISDN", serialNumber);
        data.put("QUERYSTARTTIME", exBeginTime);
        data.put("QUERYENDTIME", exEndTime);
        data.put("CONTACTCHANNEL", oprSource);
        data.put("CALLERNO", serialNumber);
        data.put("CALLEDNO", "");
        data.put("SUBSNAME", custName);
        data.put("SUBSLEVEL", "");
        data.put("SUBSBRAND", "");
        data.put("SVCTYPEID", "10010399");
        data.put("HOMEPROV", provCode);
        data.put("SVCCITY", CSBizBean.getVisit().getCityCode() + "-" + CSBizBean.getVisit().getCityName());
        data.put("ORIGINTIME", SysDateMgr.getSysTime());
        data.put("ACCEPTTIME", SysDateMgr.getSysTime());
        data.put("ACCEPTSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());
        data.put("QUERYPAGENUM", queryPageNum);

        IDataset dataset = dealInvokeUrl(king, "IBOSS", data);
        return dataset.getData(0);
    }

    /**
     * 查询手机支付账户信息
     *
     * @param serialNumber
     * @param reqNum
     * @param transId
     * @return
     * @throws Exception
     */
    public static IDataset queryMobilePayAccountInfo(String serialNumber, String reqNum, String transId) throws Exception
    {
        IData data = new DataMap();

        data.put("KIND_ID", "BIP2B092_T2040009_0_0");// 交易唯一标识
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");

        data.put("REQ_NUM", reqNum);
        data.put("BOSS_SEQ", transId);
        data.put("ACTION_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        data.put("ACTION_ID", CSBizBean.getVisit().getDepartId());
        data.put("ACTION_USERID", CSBizBean.getVisit().getStaffId());
        data.put("SERIAL_NUMBER", serialNumber);
        return callHttpIBOSS("IBOSS", data);
    }

    /**
     * 查询手机支付账户支付信息
     *
     * @param serialNumber
     * @param reqNum
     * @param transId
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public static IDataset queryMobilePayTransactionInfo(String serialNumber, String reqNum, String transId, String startTime, String endTime) throws Exception
    {
        IData data = new DataMap();
        data.put("KIND_ID", "BIP2B094_T2040011_0_0");// 交易唯一标识
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");

        data.put("REQ_NUM", reqNum);
        data.put("BOSS_SEQ", transId);
        data.put("ACTION_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        data.put("ACTION_ID", CSBizBean.getVisit().getDepartId());
        data.put("ACTION_USERID", CSBizBean.getVisit().getStaffId());
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("EX_BEGIN_TIME", startTime.replace("-", "") + "000000");
        data.put("EX_END_TIME", endTime.replace("-", "") + "235959");

        return callHttpIBOSS("IBOSS", data);
    }

    /**
     * 国漫一卡多号平台信息查询
     *
     * @param serialNumber
     * @param provinceA
     * @param kindId
     * @param routeType
     * @param routeValue
     * @param routeEparchycode
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IData queryOneCardMultiPlatInfo(String serialNumber, String provinceA, String kindId, String routeType, String routeValue, String routeEparchycode) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PROVINCE_A", provinceA);
        inparam.put("TRADECITYCODE", CSBizBean.getTradeEparchyCode());
        inparam.put("KIND_ID", kindId);
        inparam.put("ROUTETYPE", routeType);
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeEparchycode);

        return callHttpIBOSS("TCS_CrmToPlat", inparam).getData(0);
    }

    /**
     * 一卡多号（影号）业务IBOSS查询接口
     *
     * @param serialNumber
     * @param visit
     * @return
     * @author maoke
     * @throws Exception
     */
    public static IData queryOneCardMultiSnIboss(String serialNumber, BizVisit visit) throws Exception
    {
        IData param = new DataMap();
        param.put("PRIPROVINCE", "7311");// 省别编码
        param.put("IN_MODE_CODE", "6");
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 交易地州编码
        param.put("TRADE_CITY_CODE", visit.getCityCode());// 交易城市代码
        param.put("TRADE_DEPART_ID", visit.getDepartId());// 员工部门编码
        param.put("TRADE_STAFF_ID", visit.getStaffId());// 员工城市编码
        param.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());// 路由地州编码
        param.put("ROUTETYPE", "00");// 路由类型 00-省代码，01-手机号
        param.put("ROUTEVALUE", "000");// 路由类型 00-省代码，01-手机号

        param.put("PRIMSISDN", serialNumber);
        param.put("KIND_ID", "BIP1A107_T1000101_0_0");// 交易唯一标识
        param.put("PRIMSISDN", serialNumber);
        param.put("X_TRANS_CODE", "");
        param.put("PRICITY", visit.getCityCode());

        IDataset dataset = dealInvokeUrl("BIP1A107_T1000101_0_0", "IBOSS", param);

        return dataset.getData(0);
    }

    /**
     * @data 2013-8-14
     * @param provCode
     * @return
     * @throws Exception
     */
    public static IData queryParamRequestFactoryListIBOSS(String provCode) throws Exception
    {

        IData inparam = new DataMap();

        inparam.put("KIND_ID", "BIP1A110_T1000110_0_0");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("PROV_CODE", provCode);
        inparam.put("ROUTETYPE", "00");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * @data 2013-8-14
     * @param provCode
     * @param manufactureId
     * @return
     * @throws Exception
     */
    public static IData queryParamRequestTermByHttpIBOSS(String provCode, String manufactureId) throws Exception
    {

        IData inparam = new DataMap();

        inparam.put("KIND_ID", "BIP1A111_T1000111_0_0");
        inparam.put("MANUFACTURERID", manufactureId);
        inparam.put("ROUTEVALUE", "000");
        inparam.put("PROV_CODE", provCode);
        inparam.put("ROUTETYPE", "00");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 异地复机 获得客户信息
     *
     * @data 2013-8-16
     * @param idType
     * @param idValue
     * @param userPasswd
     * @param idCardType
     * @param idCardNum
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IData queryRemoteOpenMobileCustInfoIBOSS(String idType, String idValue, String userPasswd, String idCardType, String idCardNum, String startDate, String endDate, String routeType, String mobileNum) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP1A001_T1000002_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码
        inparam.put("IDTYPE", idType);
        inparam.put("IDVALUE", idValue);
        inparam.put("USER_PASSWD", userPasswd);
        inparam.put("IDCARDTYPE", idCardType);
        inparam.put("IDCARDNUM", idCardNum);
        /* 0基本资料 1 实时话费 2 账户资料 3账本资料 4帐单资料 5大客户资料 6积分信息 8 业务开通资料 */
        inparam.put("TYPEIDSET", "0");
        inparam.put("START_DATE", startDate);
        inparam.put("END_DATE", endDate);
        inparam.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码

        inparam.put("ROUTETYPE", routeType);// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE")))// 路由值
            inparam.put("ROUTEVALUE", mobileNum);
        else
            inparam.put("ROUTEVALUE", CSBizBean.getVisit().getProvinceCode());

        IDataset dataset = dealInvokeUrl("BIP1A001_T1000002_0_0", "IBOSS7", inparam);

        return dataset.getData(0);
    }

    /**
     * 远程写卡 获得客户信息
     *
     * @data 2013-8-16
     * @param idType
     * @param idValue
     * @param userPasswd
     * @param idCardType
     * @param idCardNum
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IData queryRemoteRWCustInfo(String idType, String idValue, String userPasswd, String idCardType, String idCardNum, String startDate, String endDate, String routeType, String mobileNum, String tradeid) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode()); // 省别编码
        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 交易地州编码
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 交易城市代码
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 员工部门编码
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 员工工号
        inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode()); // 路由地州编码
        inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId()); // 部门编号

        inparam.put("KIND_ID", "BIP2B006_T1000002_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "IBOSS");// 交易编码
        inparam.put("IDTYPE", idType);
        inparam.put("IDVALUE", idValue);
        inparam.put("USER_PASSWD", userPasswd);
        inparam.put("IDCARDTYPE", idCardType);
        inparam.put("IDCARDNUM", idCardNum);
        /* 0基本资料 1 实时话费 2 账户资料 3账本资料 4帐单资料 5大客户资料 6积分信息 8 业务开通资料 */
        inparam.put("TYPEIDSET", "0");
        inparam.put("START_DATE", startDate);
        inparam.put("END_DATE", SysDateMgr.getDateForYYYYMMDD(SysDateMgr.END_TIME_FOREVER));
        inparam.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        inparam.put("PROCID", tradeid);

        inparam.put("ROUTETYPE", routeType);// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE")))// 路由值
            inparam.put("ROUTEVALUE", mobileNum);
        else
            inparam.put("ROUTEVALUE", CSBizBean.getVisit().getProvinceCode());

        IDataset dataset = dealInvokeUrl("BIP2B006_T1000002_0_0", "IBOSS", inparam);

        return dataset.getData(0);
    }

    /**
     * 查询 终端静态信息
     *
     * @data 2013-8-8
     * @param terminalId
     * @param prov_code
     * @return
     * @throws Exception
     */
    public static IData queryStaticInfoIBOSS(String terminalId, String prov_code) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP1A118_T1000118_0_0");
        inparam.put("IN_MODE_CODE", "0");// 默认为营业厅
        inparam.put("TERMINALID", terminalId);

        inparam.put("PROV_CODE", prov_code);
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 异地大客户信息查询
     *
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset queryVIPCustInfo(String kindId, String xTransCode, String routeType, String routeValue, String serialNumber, String idType, String idValue, String idCardType, String idCardNum, String userPasswd, String typeIdSet)
            throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("KIND_ID", kindId);
        inparams.put("X_TRANS_CODE", xTransCode);
        inparams.put("ROUTETYPE", routeType);
        inparams.put("ROUTEVALUE", routeValue);
        inparams.put("SERIAL_NUMBER", serialNumber);
        inparams.put("IDTYPE", idType);
        inparams.put("IDVALUE", idValue);
        inparams.put("IDCARDTYPE", idCardType);
        inparams.put("IDCARDNUM", idCardNum);
        inparams.put("USER_PASSWD", userPasswd);
        inparams.put("TYPEIDSET", typeIdSet);

        IDataset dataset = dealInvokeUrl(kindId, "IBOSS", inparams);

        return dataset;
    }

    public static IDataset realWriteCardActive(String serialNumber, String imsi0, String simCardNo0, String emptyCardId, String simCardNo, String imsi, String procId, String cardFee) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ROUTEVALUE", serialNumber);// 2013-11-25"000");
        inparam.put("ID_TYPE", "01");
        inparam.put("ID_VALUE", serialNumber);// SERIAL_NUMBER1

        inparam.put("KIND_ID", "BIP2B006_T2101006_0_0");
        inparam.put("X_TRANS_CODE", "IBOSS");
        inparam.put("ORIGDOMAIN", "BOSS");
        inparam.put("HOMEDOMAIN", "BOSS");
        inparam.put("ROUTETYPE", "01");// td.getString("ROUTETYPE"));
        inparam.put("PHONENUM", serialNumber);// SERIAL_NUMBER1
        inparam.put("SERIAL_NUMBER", serialNumber);// SERIAL_NUMBER1
        inparam.put("RSRV_STR1", imsi0);
        inparam.put("RSRV_STR3", simCardNo0);
        inparam.put("RSRV_STR4", emptyCardId);
        inparam.put("RSRV_STR19", simCardNo);
        inparam.put("RSRV_STR20", imsi);
        inparam.put("OPER_FEE", "00");
        // inparam.put("PROCID", procId);
        // inparam.put("CARD_FEE", cardFee);// 卡费（和一级BOSS约定的自定义字段）
        inparam.put("ICCID", simCardNo);
        inparam.put("IMSI", imsi);

        IDataset dataset = dealInvokeUrl("BIP2B006_T2101006_0_0", "IBOSS", inparam);

        return dataset;
    }

    /**
     * 操作工单传递 客户信息查询
     *
     * @data 2013-8-23
     * @param idType
     * @param idValue
     * @param userPasswd
     * @param idCardType
     * @param idCardNum
     * @param routeType
     * @param mobileNum
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IData remoteBillTransferGetCustInfoIBOSS(String typeIdset, String idType, String idValue, String userPasswd, String idCardType, String idCardNum, String routeType, String mobileNum, String startDate, String endDate)
            throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP1A001_T1000002_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        inparam.put("IDTYPE", idType);
        inparam.put("IDVALUE", idValue);
        inparam.put("USER_PASSWD", userPasswd);
        inparam.put("IDCARDTYPE", idCardType);
        inparam.put("IDCARDNUM", idCardNum);
        inparam.put("TYPEIDSET", typeIdset); /*
         * 0 基本资料 1 实时话费 2 账户资料 3 账本资料 4 帐单资料 5 大客户资料 6 积分信息 8 业务开通资料
         */
        inparam.put("START_DATE", startDate);
        inparam.put("END_DATE", endDate);
        inparam.put("ROUTETYPE", routeType);// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE")))// 路由值
            inparam.put("ROUTEVALUE", mobileNum);
        else
            inparam.put("ROUTEVALUE", CSBizBean.getVisit().getProvinceCode());

        IDataset dataset = dealInvokeUrl("BIP1A001_T1000002_0_0", "IBOSS7", inparam);

        return dataset.getData(0);
    }

    /**
     * 操作工单传递 逻辑处理
     *
     * @data 2013-8-23
     * @param idType
     * @param idValue
     * @param userPasswd
     * @param idCardType
     * @param idCardNum
     * @param forwardTo
     * @param rvisitSvcDesc
     * @param hvisitSvcDesc
     * @param postCode
     * @param address
     * @param recipients
     * @param phoneNum
     * @param operFee
     * @param routeType
     * @param mobileNum
     * @return
     * @throws Exception
     */
    public static IData remoteBillTransferTransportBillIBOSS(String idType, String idValue, String userPasswd, String idCardType, String idCardNum, String forwardTo, String rvisitSvcDesc, String hvisitSvcDesc, String postCode, String address,
                                                             String recipients, String phoneNum, String operFee, String routeType, String mobileNum) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2B004_T2101004_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码
        /*
         * if ("01".equals(idType)) { inparam.put("SERIAL_NUMBER", idValue);// 手机号码 }
         */
        inparam.put("SERIAL_NUMBER", idValue);// 手机号码(按接口规范 必填)
        inparam.put("USER_PASSWD", userPasswd);// 客服密码

        inparam.put("IDCARDTYPE", idCardType);// 证件类型编码
        inparam.put("IDCARDNUM", idCardNum);// 证件号码

        inparam.put("FORWARDTO", forwardTo);// 无条件前转号码
        inparam.put("RVISITSVCDESC", rvisitSvcDesc);// 漫游地上门服务
        inparam.put("HVISITSVCDESC", hvisitSvcDesc);// 归属地上门服务
        inparam.put("POSTCODE", postCode);// 寄送地邮政编码
        inparam.put("ADDRESS", address);// 寄送地址
        inparam.put("RECIPIENTS", recipients);// 收件人姓名
        inparam.put("PHONENUM", phoneNum);// 联系电话
        inparam.put("OPER_FEE", operFee);// 手续费
        inparam.put("ROUTETYPE", routeType);// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE")))// 路由值
            inparam.put("ROUTEVALUE", mobileNum);
        else
            inparam.put("ROUTEVALUE", CSBizBean.getVisit().getProvinceCode());

        IDataset dataset = dealInvokeUrl("BIP1A001_T1000002_0_0", "IBOSS7", inparam);

        return dataset.getData(0);
    }

    /**
     * 跨区入网服务 查询客户信息
     *
     * @data 2013-8-21
     * @param serialNumber
     * @param name
     * @param idCardType
     * @param idCardNum
     * @param userPasswd
     * @param routeType
     * @param mobileNum
     * @return
     * @throws Exception
     */
    public static IData remoteCrossRegServiceGetCustInfoIBOSS(String serialNumber, String name, String idCardType, String idCardNum, String userPasswd, String routeType, String mobileNum) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2B009_T2040001_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("NAME", name);
        inparam.put("IDCARDTYPE", idCardType);
        inparam.put("IDCARDNUM", idCardNum);
        inparam.put("USER_PASSWD", userPasswd);
        inparam.put("ROUTETYPE", routeType);// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE")))// 路由值
            inparam.put("ROUTEVALUE", mobileNum);
        else
            inparam.put("ROUTEVALUE", CSBizBean.getVisit().getProvinceCode());

        IDataset dataset = dealInvokeUrl("BIP2B009_T2040001_0_0", "IBOSS7", inparam);

        return dataset.getData(0);
    }

    /**
     * 跨区入网服务 逻辑处理
     *
     * @data 2013-8-22
     * @param serialNumber
     * @param name
     * @param idCardType
     * @param idCardNum
     * @param userPasswd
     * @param routeType
     * @param mobileNum
     * @param allConScore
     * @param brandAwardScore
     * @param yearAwardScore
     * @param otherScore
     * @param useScore
     * @param ableScore
     * @param classLevel
     * @param levelDate
     * @param joinDate
     * @return
     * @throws Exception
     */
    public static IData remoteCrossRegServiceUpdateInfoIBOSS(String serialNumber, String name, String idCardType, String idCardNum, String userPasswd, String routeType, String mobileNum, String allConScore, String brandAwardScore,
                                                             String yearAwardScore, String otherScore, String useScore, String ableScore, String classLevel, String levelDate, String joinDate) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2B009_T2040004_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("NAME", name);
        inparam.put("IDCARDTYPE", idCardType);
        inparam.put("IDCARDNUM", idCardNum);
        inparam.put("USER_PASSWD", userPasswd);
        inparam.put("ROUTETYPE", routeType);// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE")))// 路由值
            inparam.put("ROUTEVALUE", mobileNum);
        else
            inparam.put("ROUTEVALUE", CSBizBean.getVisit().getProvinceCode());

        inparam.put("RSRV_STR9", allConScore);
        inparam.put("RSRV_STR10", brandAwardScore);
        inparam.put("RSRV_STR11", yearAwardScore);
        inparam.put("RSRV_STR12", otherScore);
        inparam.put("RSRV_STR13", useScore);
        inparam.put("RSRV_STR14", ableScore);
        inparam.put("RSRV_STR15", classLevel);
        inparam.put("RSRV_STR16", levelDate);
        inparam.put("RSRV_STR17", joinDate);

        IDataset dataset = dealInvokeUrl("BIP2B009_T2040004_0_0", "IBOSS7", inparam);

        return dataset.getData(0);
    }

    /**
     * 异地复机 复机操作
     *
     * @data 2013-8-19
     * @param idType
     * @param idValue
     * @param userPasswd
     * @param idCardType
     * @param idCardNum
     * @param reason
     * @param operFee
     * @param routeType
     * @param mobileNum
     * @return
     * @throws Exception
     */
    public static IData remoteOpenMobileOpenMobileIBOSS(String idType, String idValue, String userPasswd, String idCardType, String idCardNum, String reason, String operFee, String routeType, String mobileNum) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2B002_T2001002_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码
        /*
         * if ("01".equals(idType)) { inparam.put("SERIAL_NUMBER", idValue);// 手机号码 }
         */
        inparam.put("SERIAL_NUMBER", idValue);// 手机号码(按接口规范 必填)
        inparam.put("USER_PASSWD", userPasswd);// 客服密码
        inparam.put("IDCARDTYPE", idCardType);// 证件类型编码
        inparam.put("IDCARDNUM", idCardNum);// 证件号码

        inparam.put("REASON", reason);// 复机原因
        inparam.put("OPER_FEE", operFee);// 手续费

        inparam.put("ROUTETYPE", routeType);// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE")))// 路由值
            inparam.put("ROUTEVALUE", mobileNum);
        else
            inparam.put("ROUTEVALUE", CSBizBean.getVisit().getProvinceCode());

        IDataset dataset = dealInvokeUrl("BIP2B002_T2001002_0_0", "IBOSS7", inparam);

        return dataset.getData(0);
    }

    /**
     * 异地复机停机
     *
     * @data 2013-8-19
     * @param idType
     * @param idValue
     * @param userPasswd
     * @param idCardType
     * @param idCardNum
     * @param reason
     * @param operFee
     * @param routeType
     * @param mobileNum
     * @return
     * @throws Exception
     */
    public static IData remoteStopMobileStopMobileIBOSS(String idType, String idValue, String userPasswd, String idCardType, String idCardNum, String reason, String operFee, String routeType, String mobileNum) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2B001_T2001001_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        /*
         * if ("01".equals(idType)) { inparam.put("SERIAL_NUMBER", idValue);// 手机号码 }
         */
        inparam.put("SERIAL_NUMBER", idValue);// 手机号码(按接口规范 必填)
        inparam.put("USER_PASSWD", userPasswd);// 客服密码
        inparam.put("IDCARDTYPE", idCardType);// 证件类型编码
        inparam.put("IDCARDNUM", idCardNum);// 证件号码

        inparam.put("REASON", reason);// 复机原因
        inparam.put("OPER_FEE", operFee);// 手续费

        inparam.put("ROUTETYPE", routeType);// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE")))// 路由值
            inparam.put("ROUTEVALUE", mobileNum);
        else
            inparam.put("ROUTEVALUE", CSBizBean.getVisit().getProvinceCode());

        IDataset dataset = dealInvokeUrl("BIP2B001_T2001001_0_0", "IBOSS7", inparam);

        return dataset.getData(0);
    }

    /**
     * 重置手机支付密码
     *
     * @param serialNumber
     *            手机号
     * @param reqNum
     *            交易码
     * @param transId
     *            交易流水号
     * @return
     * @throws Exception
     */
    public static IDataset resetMobilePayPassword(String serialNumber, String reqNum, String transId) throws Exception
    {
        IData data = new DataMap();
        data.put("KIND_ID", "BIP2B087_T2040017_0_0");// 交易唯一标识
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");

        data.put("REQNUM", reqNum);// 交易码
        data.put("BOSSSEQ", transId);// BOSS流水号
        data.put("IDVALUE", serialNumber);// 手机号码

        return callHttpIBOSS("IBOSS", data);
    }

    /**
     * 调用一级BOSS接口,发送卡号及密码到用户手机
     */
    public static IDataset returnElecCardNum(String serialNumber, String wlanSeq, String xResultcode, String xResultInfo, String fee) throws Exception
    {
        IData param = new DataMap();
        param.put("ROUTETYPE", "01");
        param.put("KIND_ID", "BIP2B280_T2001224_0_0");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("WLAN_SEQ", wlanSeq);
        param.put("X_RESULTCODE", xResultcode);
        param.put("X_RESULTINFO", xResultInfo);// by zengzb 20110125
        param.put("FEE", fee);// by zengzb 20110123 增加财务确认功能
        param.put("ROUTEVALUE", "000");

        IDataset dataset = dealInvokeUrl("BIP2B280_T2001224_0_0", "IBOSS", param);

        return dataset;

    }

    /**
     * 异地手机支付冲正
     *
     * @param reqNum
     * @param transId
     * @param exSeq
     * @param serialNumber
     * @param payMoney
     * @return
     * @throws Exception
     */
    public static IDataset reverseMobilePay(String reqNum, String transId, String exSeq, String serialNumber, String payMoney) throws Exception
    {
        IData data = new DataMap();

        String tradeId = SeqMgr.getTradeId();
        data.put("KIND_ID", "BIP2B096_T2040013_0_0");// 交易唯一标识
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");

        data.put("REQ_NUM", tradeId);
        data.put("BOSS_SEQ", transId);
        data.put("EX_SEQ", exSeq);
        data.put("ACTION_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        data.put("ACTION_ID", CSBizBean.getVisit().getDepartId());
        data.put("ACTION_USERID", CSBizBean.getVisit().getStaffId());
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("PAYED", Integer.parseInt(payMoney) * 10);// 单位：厘 直接传正值，不是负数
        return callHttpIBOSS("IBOSS", data);
    }

    /**
     * 国漫一卡多号通知接口
     *
     * @return
     * @throws Exception
     */
    public static IData roamOCNCNotifyIBOSS(String provinceCode, String oldImsi, String newImsi, String sn, String snA, String provinceCodeA, String tradeCityCode, String mobNumber, String efftt) throws Exception
    {
        IData ip = new DataMap();

        IData b1 = new DataMap();
        b1.put("PROVINCE_CODE", provinceCode);
        b1.put("KIND_ID", "BIP2B158_T2001128_0_0");
        b1.put("ROUTETYPE", "00");
        b1.put("ROUTEVALUE", "000");
        b1.put("OPR", "01");
        b1.put("IMSI_CODE", oldImsi);
        b1.put("IMSI", newImsi);
        b1.put("SERIAL_NUMBER", sn);
        b1.put("SERIAL_NUMBER_A", snA);
        b1.put("PROVINCE_A", provinceCodeA);
        b1.put("TRADECITYCODE", tradeCityCode);
        b1.put("MOB_NUMBER", mobNumber);
        b1.put("EFFTT", efftt);
        return callHttpIBOSS("TCS_CrmToPlat", b1).getData(0);
    }

    public static IDataset sendCardInfo(String operTime, String serialNumber, String vipCardNo, String GR, String vaildate) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ORIG", "898");// 发起省代码
        inparam.put("OPR_TIME", operTime);// 操作时间
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("CARDNUM", vipCardNo);
        inparam.put("GR", GR);
        inparam.put("VAILDATE", vaildate);

        inparam.put("KIND_ID", "BIP2B013_T2101011_0_0");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("ROUTETYPE", "00");
        inparam.put("IN_MODE_CODE", "6");

        IDataset dataset = dealInvokeUrl("BIP2B008_T2000002_0_0", "IBOSS", inparam);

        return dataset;
    }

    /**
     * 四元素采集请求 发送平台指令
     *
     * @data 2013-8-9
     * @param imei
     * @param phone
     * @param provCode
     * @param operId
     * @return
     * @throws Exception
     */
    public static IData sendElementGatherIBOSS(String imei, String phone, String provCode, String operId) throws Exception
    {

        IData inparam = new DataMap();
        if (imei.length() > 0)
            inparam.put("IMEINUM", imei);
        else
            inparam.put("PHONENUM", phone);

        inparam.put("KIND_ID", "BIP2C020_T2002020_0_0");
        inparam.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());
        inparam.put("PROV_CODE", provCode);
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("OPERATEID", operId);

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 发送信息收集请求
     *
     * @data 2013-8-10
     * @param phone
     * @param funcids
     * @param provCode
     * @param operId
     * @return
     * @throws Exception
     */
    public static IData sendInfoGatherIBOSS(String phone, String funcids, String provCode, String operId) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2C022_T2002022_0_0");
        inparam.put("PHONENUM", phone);
        inparam.put("FUNCTIONID", funcids);
        inparam.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());
        inparam.put("PROV_CODE", provCode);
        inparam.put("OPERATEID", operId);

        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 发送参数配置请求
     *
     * @data 2013-8-14
     * @param phone
     * @param funcids
     * @param provCode
     * @param operId
     * @param termStyle
     * @return
     * @throws Exception
     */
    public static IData sendParamRequestIBOSS(String phone, String funcids, String provCode, String operId, String termStyle) throws Exception
    {

        IData inparam = new DataMap();

        inparam.put("KIND_ID", "BIP2C024_T2002024_0_0");
        inparam.put("PHONENUM", phone);
        inparam.put("FUNCTIONID", funcids);
        inparam.put("TERMINALID", termStyle);
        inparam.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());
        inparam.put("PROV_CODE", provCode);
        inparam.put("OPERATEID", operId);
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    public static IDataset sendSecCheckInfo(String serialNumber, String idType, String transId, String rspCode, String areaInfo, String operateDate, String brandCode, String payType, String avgPayed, String foreignFlag, String groupLevel,
                                            String level, String status, String userName, String realFlag, String idCardType, String idCardNum) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2B082_T2040025_0_0");// 小额授信资料查询结果
        param.put("ID_TYPE", idType);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRANS_ID", transId);
        param.put("RSP_CODE", rspCode);
        param.put("AREA_INFO", areaInfo);
        param.put("OPERATE_DATE", operateDate);
        param.put("BRAND_CODE", brandCode);
        param.put("PAY_TYPE", payType);
        param.put("AVG_PAYED", avgPayed);
        param.put("FOREIGN_FLAG", foreignFlag);
        param.put("GROUP_LEVEL", groupLevel);
        param.put("LEVEL", level);
        param.put("STATUS", status);
        param.put("USER_NAME", userName);
        param.put("REAL_FLAG", realFlag);
        param.put("ID_CARD_TYPE", idCardType);
        param.put("ID_CARD_NUM", idCardNum);

        IDataset dataset = dealInvokeUrl("BIP2B008_T2000002_0_0", "IBOSS", param);

        return dataset;
    }

    /**
     * 调一级BOSS接口往实体卡平台发送消息
     */
    public static IDataset sendSmsFromEntityCardPlat(String serialNumber, String wlanSeq, String strOprNum) throws Exception
    {
        IData param = new DataMap();
        param.put("ROUTETYPE", "00");
        param.put("KIND_ID", "BIP2B280_T2001125_0_0");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("WLAN_SEQ", wlanSeq);
        param.put("OPR_NUMB", strOprNum);
        param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        IDataset dataset = dealInvokeUrl("BIP2B280_T2001125_0_0", "IBOSS", param);

        return dataset;

    }



    /**
     * 银行账号校验
     *
     * @data 2013-12-18
     * @return
     * @throws Exception
     */
    public static IDataset SignBankCheck(IData inData) throws Exception
    {
        inData.put("KIND_ID", "BIP1A151_T1000152_0_0");
        inData.put("X_TRANS_CODE", "T1000152");

        IDataset dataset = dealInvokeUrl("BIP1A151_T1000152_0_0", "IBOSS2", inData);

        return dataset;
    }

    /**
     * 副号码业务变更
     *
     * @param inModeCode
     * @param serialNumberF
     * @param serialNumber
     * @param imisF
     * @param routeType
     * @param routeValue
     * @param routeEparchycode
     * @param provinceCode
     * @param acceptDate
     * @param kingId
     * @param operCode
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IData subCardChangeOrder(String inModeCode, String serialNumberF, String serialNumber, String imisF, String routeType, String routeValue, String routeEparchycode, String provinceCode, String acceptDate, String kingId,
                                           String operCode) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("IN_MODE_CODE", inModeCode);
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_CITY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getCityCode());
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeEparchycode);
        inparam.put("ROUTETYPE", routeType);
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("OPER_CODE", operCode);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("SERIAL_NUMBER_F", serialNumberF);
        inparam.put("IMIS_F", imisF);
        inparam.put("PROVINCE_CODE", provinceCode);
        inparam.put("ACCEPT_DATE", acceptDate);
        inparam.put("KIND_ID", kingId);

        return callHttpIBOSS("TCS_CrmToPlat", inparam).getData(0);
    }

    /**
     * 提交可用应用软件包
     *
     * @data 2013-8-8
     * @param serialNumber
     * @param softwareBagid
     * @param provCode
     * @param operateId
     * @return
     * @throws Exception
     */
    public static IData submitApplicationSoftwareIBOSS(String serialNumber, String softwareBagid, String provCode, String operateId) throws Exception
    {

        IData inparam = new DataMap();

        inparam.put("KIND_ID", "BIP2C030_T2002030_0_0");
        inparam.put("PHONENUM", serialNumber);
        inparam.put("SOFTWAREBAGID", softwareBagid);
        inparam.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());
        inparam.put("PROV_CODE", provCode);
        inparam.put("OPERATEID", operateId);
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("TRADE_DEPART_PASSWD", "");
        inparam.put("IN_MODE_CODE", "0");
        inparam.put("APPLY_TYPE", "16");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 提交 DM业务查询、操作取消 业务
     *
     * @data 2013-8-8
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IData submitDMBusiSelIBOSS(String operateId) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2C038_T2002038_0_0");
        inparam.put("OPERATEID", operateId);
        inparam.put("TRADE_DEPART_PASSWD", "");
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("X_RSPTYPE", "");
        inparam.put("X_RSPCODE", "");
        inparam.put("X_RSPDESC", "");
        inparam.put("X_TRANS_CODE", "ITF_IBOT_INSDMDEAL");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 提交可用固件回退包
     *
     * @data 2013-8-8
     * @param serialNumber
     * @param rollbackbagid
     * @param provCode
     * @param operateId
     * @param executemode
     * @return
     * @throws Exception
     */
    public static IData submitFirmwareReturnIBOSS(String serialNumber, String rollbackbagid, String provCode, String operateId, String executemode) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2C028_T2002028_0_0");
        inparam.put("PHONENUM", serialNumber);
        inparam.put("ROLLBACKBAGID", rollbackbagid);// 固件升级包ID
        inparam.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());// 客服账号名称
        inparam.put("PROV_CODE", provCode);// 省代码
        inparam.put("OPERATEID", operateId);// 操作ID
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("EXECUTEMODE", executemode);// ExecuteMode 任务执行方式 1立即执行、
        // 2闲时执行
        inparam.put("RSRV_STR1", executemode);// 插入表TI_B_DM_BUSI,cod_code:INS_DM_SUB_DATA;
        inparam.put("TRADE_DEPART_PASSWD", "");
        inparam.put("IN_MODE_CODE", "0");
        inparam.put("APPLY_TYPE", "15");
        inparam.put("X_TRANS_CODE", "ITF_IBOQ_DMDEAL");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 提交固件升级处理
     *
     * @data 2013-8-1
     * @param serialNumber
     * @param upgradebagid
     * @param provCode
     * @param operateId
     * @param executemode
     * @return
     * @throws Exception
     */
    public static IData submitFirmwareUpgradeIBOSS(String serialNumber, String upgradebagid, String provCode, String operateId, String executemode) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("KIND_ID", "BIP2C026_T2002026_0_0");
        inparam.put("PHONENUM", serialNumber);
        inparam.put("UPGRADEBAGID", upgradebagid);// 固件升级包ID
        inparam.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());// 客服账号名称
        inparam.put("PROV_CODE", provCode);// 省代码
        inparam.put("OPERATEID", operateId);// 操作ID
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("EXECUTEMODE", executemode);// ExecuteMode 任务执行方式 1立即执行、
        // 2闲时执行
        inparam.put("RSRV_STR1", executemode);// 插入表TI_B_DM_BUSI,cod_code:INS_DM_SUB_DATA;
        inparam.put("TRADE_DEPART_PASSWD", "");
        inparam.put("IN_MODE_CODE", "0");
        inparam.put("APPLY_TYPE", "14");
        inparam.put("X_TRANS_CODE", "ITF_IBOQ_DMDEAL");

        return callHttpIBOSS("ITF_IBOQ_DMDEAL", inparam).getData(0);
    }

    /**
     * 副号解约信息同步
     *
     * @data 2013-12-18
     * @return
     * @throws Exception
     */
    public static IDataset SubsignCancelSync(IData inData) throws Exception
    {
        inData.put("KIND_ID", "BIP1A159_T1000154_0_0");
        inData.put("X_TRANS_CODE", "T1000154");
        IDataset dataset = dealInvokeUrl("BIP1A159_T1000154_0_0", "IBOSS2", inData);

        return dataset;
    }

    /**
     * 关联副号码同步
     *
     * @data 2013-12-18
     * @return
     * @throws Exception
     */
    public static IDataset SubsignSync(IData inData) throws Exception
    {
        inData.put("KIND_ID", "BIP1A158_T1000154_0_0");
        inData.put("X_TRANS_CODE", "T1000154");

        IDataset dataset = dealInvokeUrl("BIP1A158_T1000154_0_0", "IBOSS2", inData);

        return dataset;
    }

    /**
     * 同步给行车卫士平台信息
     */
    public static IDataset synPlatInfo(String phone, String serialNumber, String oldImsi, String newImsi, String tradeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ROUTETYPE", "00");
        inparams.put("ROUTEVALUE", "000");
        inparams.put("KIND_ID", "BIP6B902_T9898989_0_0");
        inparams.put("MSG_TYPE", "ServiceChangeReq");// 业务标识
        inparams.put("TRANSACTION_ID", tradeId);// 操作流水
        inparams.put("CHANGE_TYPE", "3");// 变更类型
        inparams.put("OLD_FEEUSER_ID", phone);// 主号码
        inparams.put("SERIAL_NUMBER", phone);// 主号码
        inparams.put("OLD_DESTUSER_ID", serialNumber);// 从号码
        inparams.put("OLD_DESTUSER_IMSI", oldImsi);// 从号码旧IMSI
        inparams.put("NEW_FEEUSER_ID", "");// 新主号码
        inparams.put("NEW_DESTUSER_ID", "");// 新从号码
        inparams.put("NEW_DESTUSER_IMSI", newImsi);// 新从号码IMSI

        IDataset dataset = dealInvokeUrl("BIP6B902_T9898989_0_0", "IBOSS7", inparams);
        return dataset;
    }

    /**
     * @param imsi
     * @param serialNumberA
     * @param serialNumber
     * @param opr
     * @param routeType
     * @param routeValue
     * @param resTradeType
     * @param provinceCode
     * @param latencyFeeSum
     * @param kindId
     * @param fee
     * @param provinceA
     * @param tradeCityCode
     * @param outGroupId
     * @param outNetType
     * @param efftt
     * @param validTo
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IData TAIWOneCardMuilNumber(String imsi, String serialNumberA, String serialNumber, String opr, String routeType, String routeValue, String resTradeType, String provinceCode, String latencyFeeSum, String kindId, String fee,
                                              String provinceA, String tradeCityCode, String outGroupId, String outNetType, String efftt, String validTo) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("ROUTETYPE", routeType);
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("SERIAL_NUMBER_A", serialNumberA);
        inparam.put("IMSI", imsi);
        inparam.put("PROVINCE_CODE", provinceCode);
        inparam.put("KIND_ID", kindId);
        inparam.put("OPR", opr);
        inparam.put("RES_TRADE_TYPE", resTradeType);
        inparam.put("FEE", fee);
        inparam.put("LATENCY_FEE_SUM", latencyFeeSum);
        inparam.put("PROVINCE_A", provinceA);
        inparam.put("TRADECITYCODE", tradeCityCode);
        inparam.put("OUT_GROUP_ID", outGroupId);
        inparam.put("OUT_NET_TYPE", outNetType);
        inparam.put("EFFTT", efftt);
        inparam.put("VALID_TO", validTo);

        return callHttpIBOSS("TCS_CrmToPlat", inparam).getData(0);
    }

    public static IData tcsOneCardmuilNumber(String resValueCode, String inModeCode, String serialNumber, String idCardType, String idCardNum, String routeType, String routeValue, String routeEparchycode, String provinceCode, String acceptDate,
                                             String kingId, String userPassWd, String oprCode, String servType, String dailyConsumeLimit, String TotalConsumeLimit, String sn) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("IN_MODE_CODE", inModeCode);
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_CITY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getCityCode());
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeEparchycode);
        inparam.put("ROUTETYPE", routeType);
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("SERIAL_NUMBER_F", serialNumber);
        inparam.put("USER_PASSWD", userPassWd);
        inparam.put("IDCARDTYPE", idCardType);
        inparam.put("IDCARDNUM", idCardNum);
        inparam.put("PROVINCE_CODE", provinceCode);
        inparam.put("ACCEPT_DATE", acceptDate);
        inparam.put("IDVALUE", serialNumber);
        inparam.put("RSRV_VALUE_CODE", resValueCode);
        inparam.put("KIND_ID", kingId);
        inparam.put("SecCountry", "852");
        inparam.put("OprCode", oprCode);
        inparam.put("ServType", servType);
        inparam.put("DailyConsumeLimit", dailyConsumeLimit);
        inparam.put("TotalConsumeLimit", TotalConsumeLimit);
        inparam.put("PriMSISDN", sn);

        return callHttpIBOSS("TCS_CrmToPlat", inparam).getData(0);
    }

    public static IDataset wrCrdResBack(String imsi, String resultCode, String serialNumber, String tradeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode()); // 省别编码
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 交易地州编码
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 交易城市代码
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 员工部门编码
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 员工工号
        inparam.put("RES_TYPE_CODE", "I"); // 资源类型[实体卡]
        inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode()); // 路由地州编码
        inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId()); // 部门编号

        inparam.put("ROUTETYPE", "01");// "00"); 2013-11-22
        inparam.put("ROUTEVALUE", serialNumber);// "000"); 2013-11-22
        inparam.put("ID_TYPE", "01");

        inparam.put("KIND_ID", "BIP2B006_T2000002_0_0");
        inparam.put("X_TRANS_CODE", "IBOSS");
        inparam.put("ORIGDOMAIN", "BOSS");
        inparam.put("HOMEDOMAIN", "BOSS");
        inparam.put("RSRV_STR1", imsi);
        inparam.put("RESULTCODE", resultCode); // 写卡结果 00:写卡成功 11:写卡失败
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PROCID", tradeId);// td.getTradeId());

        IDataset dataset = dealInvokeUrl("BIP2B006_T2000002_0_0", "IBOSS", inparam);

        return dataset;
    }

    public static IDataset writeCardActiveM(String serialNumber, String procId, String provCodes, String routeValue, String simcardNo, String imsi) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ROUTETYPE", "01");
        inparam.put("ROUTEVALUE", serialNumber);
        inparam.put("ID_TYPE", "01");
        inparam.put("ID_VALUE", serialNumber);// SERIAL_NUMBER1
        inparam.put("KIND_ID", "BIP2B008_T2101008_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "IBOSS");// 交易编码
        inparam.put("PROCID", procId);
        inparam.put("EXE_RESULT", "00");
        inparam.put("ROUTETYPE", "00");
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PROVINCE_NO", provCodes);
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("IMSI_NUMBER", simcardNo);// today 2013-8-29 改成 imsi，不知道
        // imsiNumbers是个什么东西
        // imsiNumbers[i] !!
        // simcardNo);
        inparam.put("IMSI", imsi);

        IDataset dataset = dealInvokeUrl("BIP2B008_T2101008_0_0", "IBOSS", inparam);

        return dataset;
    }

    public static IDataset writeCardBackM(String procId, String writeCardResult, String routeValue, String proviceNo, String imsi, String sn) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ROUTETYPE", "01"); // 2013-11-25 00
        inparam.put("ROUTEVALUE", sn); // "000");
        inparam.put("ID_TYPE", "01");
        inparam.put("KIND_ID", "BIP2B008_T2000002_0_0");
        inparam.put("X_TRANS_CODE", "IBOSS");
        inparam.put("PROCID", procId);
        inparam.put("EXE_RESULT", writeCardResult); // 写卡结果 00:写卡成功 11:写卡失败 暂时
        // 固定值 模拟
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("PROVINCE_NO", proviceNo);
        inparam.put("IMSI", imsi);

        IDataset dataset = dealInvokeUrl("BIP2B008_T2000002_0_0", "IBOSS", inparam);

        return dataset;
    }

    public static IDataset scoreExchange4Plat(String serialNumber,String ruleId,String count,String tradeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("RULE_ID", ruleId);
        inparam.put("COUNT", count);
        inparam.put("TRADE_ID", tradeId);
        inparam.put("KIND_ID", "BIPAH001_TA311001_0_0");
        inparam.put("X_TRANS_CODE", "IBOSS");
        IDataset dataset = dealInvokeUrl("BIPAH001_TA311001_0_0", "IBOSS", inparam);

        return dataset;
    }

    /**
     * 修改用户其他资料信息--物联网行车卫士功能
     */
    public static IDataset callIbossUserOtherInfo(IData paramData) throws Exception{
        String sysdate = SysDateMgr.getSysTime();
        String mm = sysdate.substring(0,4)+sysdate.substring(5,7)+sysdate.substring(8,10)+sysdate.substring(11,13)+sysdate.substring(14,16)+sysdate.substring(17,19);
        String tradeId = SeqMgr.getTradeId();
        String tempId = tradeId.substring(tradeId.length()-6);
        String id = "7311BIP2B978" + mm + tempId;
        paramData.put("OPR_SEQ", id);
        paramData.put("ROUTETYPE", "00");
        paramData.put("ROUTEVALUE", "000");
        paramData.put("KIND_ID", "BIP2B978_T2140978_0_0");
        paramData.put("MOB_NUM", paramData.getString("SERIAL_NUMBER"));
        paramData.put("SUBS_ID",paramData.getString("USER_ID"));
        paramData.put("PROD_ID", "731");
        paramData.put("OPR_TIME",SysDateMgr.getSysTime().replace("-", "").replace(":", "").replace(" ", ""));
        paramData.put("OWNER_NAME", paramData.getString("CUST_NAME"));//车主姓名
        paramData.put("OWNER_ID", paramData.getString("PSPT_ID"));//车主身份证号
        paramData.put("OWNER_MOBILE",paramData.getString("RSRV_STR9"));//车主绑定的手机号码
        paramData.put("MODULE_SERIALNUMBER", paramData.getString("RSRV_STR1"));//终端模组序列号
        paramData.put("INSTALLATION_POINTS", paramData.getString("RSRV_STR2"));//安装点
        paramData.put("INSTALLATION_PERSON", paramData.getString("RSRV_STR3"));//安装人员
        paramData.put("INSTALLATION_PERSON_CONTACT_INFO", paramData.getString("RSRV_STR4"));//安装人员联系方式
        paramData.put("FRAME_NUMBER", paramData.getString("RSRV_STR5"));//车架号
        paramData.put("PLATE_NUMBER", paramData.getString("RSRV_STR6"));//车牌号
        return dealInvokeUrl("BIP2B978_T2140978_0_0","IBOSS", paramData);
    }

    /**
     * 下发采集验证工单
     * @param paramData
     * @return
     * @throws Exception
     */
    public static IDataset callIbossRealNameCreateTrade(IData inparam) throws Exception
    {

        /**请求方式开关 modify by zouyi 20150525**/
        String para_code1 = inparam.getString("para_code1");//PARA_CODE1业务开关1打开，其他关闭
        String para_code2 = inparam.getString("para_code2");//互联网 KIND_ID
        String para_code3 = inparam.getString("para_code3");//网状网 KIND_ID

        String kind_id = "";
        String svcName = "";

        if ("1".equals(para_code1)) {
            kind_id = para_code2;
            svcName = "JsonSerialReceiver";
        }else{
            kind_id = para_code3;
            svcName = "IBOSS";
        }
        /****/

        String sysdate = SysDateMgr.getSysTime();
        String date = sysdate.replace("-", "").replace(" ", "").replace(":", "");
        String seqRealId = SeqMgr.getRealId();

        inparam.put("TRANSACTION_ID", "898" + date + seqRealId);
        inparam.put("ROUTETYPE", "00");
        inparam.put("ROUTEVALUE", "000");
        inparam.put("KIND_ID", kind_id);//和一级boss约定的值

        IDataset dataset = callHttpIBOSS4(svcName, inparam);
        //dealInvokeUrl("sendBill_BOSS_0_0", "IBOSS3", inparam);

        return dataset;
    }


    /**
     * 	客户端密钥更新
     * @param paramData
     * @return
     * @throws Exception
     */
    public static IDataset callIbossKeyFresh(IData inparam) throws Exception
    {
        /**请求方式开关 modify by zouyi 20150525**/
        String para_code1 = inparam.getString("para_code1");//PARA_CODE1业务开关1打开，其他关闭
        String para_code2 = inparam.getString("para_code2");//互联网 KIND_ID
        String para_code3 = inparam.getString("para_code3");//网状网 KIND_ID

        String kind_id = "";
        String svcName = "";

        if ("1".equals(para_code1)) {
            kind_id = para_code2;
            svcName = "JsonSerialReceiver";
        }else{
            kind_id = para_code3;
            svcName = "IBOSS";
        }

        inparam.put("PROVINCE_CODE", inparam.getString("PROVINCE_CODE"));//和一级boss约定的值
        inparam.put("KIND_ID", kind_id);//和一级boss约定的值

        IDataset dataset =  callHttpIBOSS4(svcName, inparam);
        //dealInvokeUrl("sendBill_BOSS_0_0", "IBOSS3", inparam);

        return dataset;
    }

    public static IDataset cancelScoreExchange(String merId, String merDate, String actId, String mobileId, String amount, String ttDate, String ttNum) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("FUNCODE", "5003");//接口功能码
        inparam.put("MERID", merId);//商户号
        inparam.put("MERDATE", merDate);//商户日期 YYYYMMDD
        inparam.put("ACTID", actId);//活动编号 建立电子卷活动的活动号
        inparam.put("MOBILEID", mobileId);//手机号
        inparam.put("AMOUNT", amount);//电子卷金额
        inparam.put("TTDATE", ttDate);//发放日期 电子卷发放日期YYYYMMDD
        inparam.put("TTNUM", ttNum);//电子券编号
        inparam.put("VERSION", "1.0");//版本  定值1.0
        inparam.put("KIND_ID", "BIPMD051_T1000053_0_0");
        inparam.put("X_TRANS_CODE", "IBOSS");
        IDataset dataset = dealInvokeUrl("BIPMD051_T1000053_0_0", "IBOSS", inparam);

        return dataset;
    }

    /**
     * 绑定关系解除通知
     * @param paramData
     * @return
     * @throws Exception
     */
    public static IDataset callIbossunBindMobileInform(IData inparam) throws Exception
    {
        /******************************合版本 duhj 2017/5/3 start********************************/
        String channelId =  inparam.getString("CHANNEL_ID","");
        if (StaticUtil.getStaticValue("CUSTSERVICE_CHANNEL_ID", channelId)!=null)//12月批次
        {
            inparam.put("KIND_ID", "releaseNotice_BOSS_0_01");
        }else{
            inparam.put("KIND_ID", "releaseNotice_BOSS_0_0");//和一级boss约定的值
        }
        /******************************合版本 duhj 2017/5/3 start********************************/
        inparam.put("PROV_CODE", "898");
        String provinceCode=CSBizBean.getVisit().getProvinceCode();
        if(provinceCode==null || "".equals(provinceCode)){
            provinceCode="HAIN";
        }
        inparam.put("PROVINCE_CODE", provinceCode);
        IDataset dataset = callHttpIBOSS4("IBOSS", inparam);
        return dataset;
    }
    public static IDataset callHttpIBOSS4SynBossStaffId(String svcName, IData data) throws Exception
    {

        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        if(data.containsKey("syn_SysDataRight4BBOSS")){
            data.put("TRADE_EPARCHY_CODE","0898");//两级界面的跨省集团处理的用户信息自动同步 处理标志，由于是AEE调过来，默认会为空所以写死
            data.remove("syn_SysDataRight4BBOSS");
        }else{
            data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        }
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));
        String inparams = Wade3DataTran.toWadeString(data);

        String url = BizEnv.getEnvString("crm.IBOSSUrl");

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, "IBOSS", inparams, "GBK");
        // out = new String(out.getBytes("UTF-8"));

        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);

        return dataset;
    }

    /**
     * 用户轨迹核查
     * @return
     * @throws Exception
     */
    public static IData queryBussQureySeriveIBOSS1(String kindId, String indictSeq, String svcTypeId, String provinceID, String svcCity, String originTime, String contactChannel, String operateConditions, String serviceTypeId,
                                                   String operateTypeId, String currPage) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("KIND_ID", kindId);
        inparam.put("INDICTSEQ", indictSeq);
        //inparam.put("CALLERNO", serNumber);
        inparam.put("SVCTYPEID", svcTypeId);
        inparam.put("HOMEPROV", provinceID);
        inparam.put("SVCCITY", svcCity);
        inparam.put("QUERYPAGENUM", currPage);
        inparam.put("ORIGINTIME", originTime);
        inparam.put("ACCEPTTIME", originTime);
        inparam.put("CONTACTCHANNEL", contactChannel);
        inparam.put("SERVICETYPEID", serviceTypeId);
        inparam.put("OPERATETYPEID", operateTypeId);
        inparam.put("OPERATECONDITIONS", operateConditions);
        // inparam.put("SUBSLEVEL", subsLevel);

        IDataset dataset  = dealInvokeUrl(kindId,"IBOSS",inparam);
        return dataset.getData(0);
    	/*//测试用
		//String str = "{INDICTSEQ=[\"20120418CSVC2200000001\"], KIND_ID=[\"BIP2C092_T2002092_0_0\"], ORIGDOMAIN=[\"BOSS\"], HOMEDOMAIN=[\"CSCV\"], BIPCODE=[\"BIP2C092\"], ACTIVITYCODE=[\"T2002092\"], ROUTETYPE=[\"00\"], ROUTEVALUE=[\"000\"], BIPVER=[\"0100\"], ACTIONCODE=[\"0\"], SVCCONTVER=[\"0100\"], TESTFLAG=[\"0\"], BUSI_SIGN=[\"BIP2C092_T2002092_0_0\"], UIPBUSIID=[\"312041816543982955373\"], TRANSIDO=[\"T1111165870\"], PROCID=[\"P1111165870\"], TRANSIDH=[\"C1050121449847668\"], PROCESSTIME=[\"20060224165055\"], TRANSIDC=[\"T21012031108395825937332202TOBOSS\"], CUTOFFDAY=[\"20060225\"], OSNDUNS=[\"9990\"], HSNDUNS=[\"8980\"], CONVID=[\"T21012031108395825937332202TOBOSS1108395826554_SCR6\"], MSGSENDER=[\"8981\"], MSGRECEIVER=[\"8980\"], X_RSPTYPE=[\"0\"], X_RSPCODE=[\"0000\"], X_RSPDESC=[\"success\"], X_RESULTINFO=[\"success\"], X_RESULTCODE=[\"0\"], OPERRTNSTAID=[\"1\"], RSLTTOTALCNT=[\"500\"], RSLTPAGEMAXCNT=[\"150\"], RSLTPAGECURRCNT=[\"200\"], RSLTPAGENUM=[\"1\"], RSLTTOTALPAGES=[\"100\"], RSRV_STR0=[\"1\", \"2\", \"3\"], RSRV_STR1=[\"业务名称\", \"业务名称\", \"业务名称\"], RSRV_STR2=[\"13412312311\", \"13412312311\", \"13412312311\"], RSRV_STR3=[\"100\", \"100\", \"100\"], RSRV_STR4=[\"13512312311\", \"13512312311\", \"13512312311\"], RSRV_STR5=[\"SP名称\", \"SP名称\", \"SP名称\"], RSRV_STR6=[\"SP联系方式\", \"SP联系方式\", \"SP联系方式\"], RSRV_STR7=[\"2011-11-0119:10:10\", \"2011-11-0119:10:10\", \"2011-11-0119:10:10\"], RSRV_STR8=[\"2011-11-0119:10:30\", \"2011-11-0119:10:30\", \"2011-11-0119:10:30\"], RSRV_STR9=[\"20\", \"20\", \"20\"], RSRV_STR10=[\"1\", \"1\", \"1\"], RSRV_STR11=[\"资费\", \"资费\", \"资费\"], RSRV_STR12=[\"信息费\", \"信息费\", \"信息费\"], QRYRSLTLIST=[\"1|业务名称|13412312311|100|13512312311|SP名称|SP联系方式|2011-11-0119:10:10|2011-11-0119:10:30|20|1|资费|信息费[REC_SPLIT]2|业务名称|13412312311|100|13512312311|SP名称|SP联系方式|2011-11-0119:10:10|2011-11-0119:10:30|20|1|资费|信息费[REC_SPLIT]3|业务名称|13412312311|100|13512312311|SP名称|SP联系方式|2011-11-0119:10:10|2011-11-0119:10:30|20|1|资费|信息费\"]}";
		IData data =new DataMap();
		data.put("QRYRSLTLIST","[\"1|业务名称|13412312311|100|13512312311|SP名称|SP联系方式|2011-11-0119:10:10|2011-11-0119:10:30|20|1|资费|信息费[REC_SPLIT]2|业务名称|13412312311|100|13512312311|SP名称|SP联系方式|2011-11-0119:10:10|2011-11-0119:10:30|20|1|资费|信息费[REC_SPLIT]3|业务名称|13412312311|100|13512312311|SP名称|SP联系方式|2011-11-0119:10:10|2011-11-0119:10:30|20|1|资费|信息费\"]");
    	return data;*/

    }

    /**
     * 正向学护卡成员业务办理校验接口
     *
     * @data 2014-2-19
     * @return
     * @throws Exception
     */
    public static IDataset QryXfkNumIsExists(IData data) throws Exception
    {
        data.put("KIND_ID", "BIPXHK02_TX000001_0_0");
        return dealInvokeUrl("BIPXHK02_TX000001_0_0", "IBOSS", data);
    }

    /**
     *  融合总机上传欢迎词
     *
     * @return
     * @throws Exception
     */
    public static IDataset sendGrpWordMessage(IData inData) throws Exception
    {
        IData temp = new DataMap();
        temp.put("KIND_ID", "CTX1A018_T1000001_0_0");
        temp.put("BG_NAME", inData.getString("BG_NAME"));
        temp.put("WORDSID", inData.getString("WORDSID"));
        temp.put("WORDSDES",inData.getString("WORDSDES"));

        IDataset dataset = dealInvokeUrl("CTX1A018_T1000001_0_0", "IBOSS8", temp);

        return dataset;
    }

    /**
     *  融合总机激活欢迎词
     *
     * @return
     * @throws Exception
     */
    public static IDataset activeGrpWordMessage(IData inData) throws Exception
    {
        IData temp = new DataMap();
        temp.put("KIND_ID", "CTX1A019_T1000001_0_0");
        temp.put("BG_NAME", inData.getString("BG_NAME"));
        temp.put("WORDSID", inData.getString("WORDSID"));
        temp.put("CHECK_STATUS", inData.getString("CHECK_STATUS"));

        IDataset dataset = dealInvokeUrl("CTX1A019_T1000001_0_0", "IBOSS8", temp);

        return dataset;
    }

    /**
     * 国漫日套餐上报国漫集中平台
     *
     * @param imsi
     * @param serialNumber
     * @param oprCode
     * @param routeType
     * @param routeValue
     * @param kindId
     * @param provinceA
     * @param outGroupId
     * @param outNetType
     * @param efftt
     * @return
     * @throws Exception
     * @author wangww
     */
    public static IData InterRoamDayforIboss(String elemid,String serialNumber,String modifytag,String routeValue,String routeType) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("ROUTETYPE", routeType);
        inparam.put("MSISDN", serialNumber);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PROV_CODE", "898");
        inparam.put("KIND_ID", "BIP3A305_T3000308_0_0");
        inparam.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        inparam.put("PROD_ID", elemid);
        if("0".equals(modifytag)){
            inparam.put("OPR_CODE","01");
        }else if("1".equals(modifytag)){
            inparam.put("OPR_CODE","02");
        }else if("2".equals(modifytag)){
            inparam.put("OPR_CODE","03");
        }
        IDataset dataset = dealInvokeUrl("BIP3A305_T3000308_0_0","IBOSS",inparam);

        return dataset.getData(0);
    }
    
    /**
     * add by huangyq
     * 国漫日套餐上报国漫集中平台  02-取消时携带 订购流水号
     * @param prodInstId
     * @param elemid
     * @param serialNumber
     * @param modifytag
     * @param routeValue
     * @param routeType
     * @return
     * @throws Exception
     */
    public static IData InterRoamDayforIbossTakeProdInstId(String prodInstId, String elemid,String serialNumber,String modifytag,String routeValue,String routeType) throws Exception
    {
    	IData inparam = new DataMap();

        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("ROUTEVALUE", routeValue);
        inparam.put("ROUTETYPE", routeType);
        inparam.put("MSISDN", serialNumber);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PROV_CODE", "898");
        inparam.put("KIND_ID", "BIP3A305_T3000308_0_0");
        inparam.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        inparam.put("PROD_ID", elemid);
        
        //REQ202004290023关于进一步优化国际漫游业务资费和产品体验支撑系统改造的通知		BOSS_PARA2 -  销售人员标识信息;BOSS_PARA3 – 引流渠道编码
        IDataset paraDataset = new DatasetList();
        IData data = new DataMap();
        int i = 0;
        data.put("PARA_NAME", "BOSS_PARA2");
        data.put("PARA_VALUE", CSBizBean.getVisit().getStaffId());
        paraDataset.add(data);i++;
        data = new DataMap();
        data.put("PARA_NAME", "BOSS_PARA3");
        data.put("PARA_VALUE", CSBizBean.getVisit().getDepartId());
        paraDataset.add(data);i++;
        
        if("0".equals(modifytag)){
            inparam.put("OPR_CODE","01");  
        }else if("1".equals(modifytag)){
            inparam.put("OPR_CODE","02");
            // 02-取消操作  携带 订购流水号  扩展信息对应的1个参数为:BOSS_PARA1 – 产品订购流水号
        	data = new DataMap();
            data.put("PARA_NAME", "BOSS_PARA1");
            data.put("PARA_VALUE", prodInstId);
            paraDataset.add(data);i++;
            
        	inparam.put("PROD_INST_ID",prodInstId); 
        	inparam.put("PARA_NAME", "BOSS_PARA1");
        	inparam.put("PARA_VALUE", prodInstId);
            
        }else{
            inparam.put("OPR_CODE","03");  
        }
        inparam.put("PARA_NUM", i);
        inparam.put("PARA_INFO",paraDataset);  
        
        IDataset dataset = dealInvokeUrl("BIP3A305_T3000308_0_0","IBOSS",inparam);

        return dataset.getData(0);
    }
    

    /**
     * 国漫订购关系查询
     * @param elemid
     * @param serialNumber
     * @param modifytag
     * @param routeValue
     * @param routeType
     * @return
     * @throws Exception
     */
    public static IDataset interRoamQuery(String serialNumber) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PROV_CODE", "898");
        inparam.put("KIND_ID", "BIP3A309_T3000312_0_0");
        inparam.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

        IDataset dataset = dealInvokeUrl("BIP3A309_T3000312_0_0","IBOSS",inparam);

        return dataset;
    }

    public static IDataset callHttpIBOSS6(String svcName, IData data) throws Exception
    {

        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url =BizEnv.getEnvString("crm.IBOSSUrl6");

        if (logger.isDebugEnabled())
        {
            logger.debug("send iboss url:" + url);
            logger.debug("send iboss inparams:" + inparams);
            logger.debug("send iboss svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        // out = new String(out.getBytes("UTF-8"));

        if (logger.isDebugEnabled())
        {
            logger.debug("receive iboss result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);

        return dataset;
    }

    public static IDataset applyRemoteWrite(IData param) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode()); // 省别编码
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 交易地州编码
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 交易城市代码
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 员工部门编码
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 员工工号
        inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode()); // 路由地州编码
        inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId()); // 部门编号
        inparam.put("ROUTETYPE", "01"); // 00 2013-11-22
        inparam.put("ROUTEVALUE", param.getString("ID_VALUE"));// "000"); 2013-11-22
        inparam.put("ID_TYPE", "01");
        inparam.put("ID_VALUE", param.getString("ID_VALUE"));
        inparam.put("KIND_ID", "BIP2B021_T2000021_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "IBOSS");// 交易编码
        inparam.put("SERIAL_NUMBER", param.getString("ID_VALUE"));
        inparam.put("SEQ", param.getString("SEQ"));
        inparam.put("CARDSN", param.getString("CARDSN"));
        //inparam.put("ICCID", iccid);
        inparam.put("ORIGDOMAIN", "BOSS");
        inparam.put("HOMEDOMAIN", "BOSS");
	    IDataset extensionReqs = new DatasetList();
	    IData extensionReq1=new DataMap();
	    IData extensionReq2=new DataMap();
	    extensionReq1.put("NAME", "01");//01:异地写卡原因
	    extensionReq1.put("VALUE", param.getString("BIZ_TYPE"));
	    extensionReq2.put("NAME", "02");//02:用户身份凭证
	    extensionReq2.put("VALUE", param.getString("IDENT_CODE"));//身份凭证
	    extensionReqs.add(extensionReq1);
	    extensionReqs.add(extensionReq2);
	    inparam.put("EXTENSION_REQ", extensionReqs);
        IData ChlInfo = ChnlInfoQry.getGlobalChlId(CSBizBean.getVisit().getDepartId()).getData(0);
        String channelId = "";
        if (IDataUtil.isNotEmpty(ChlInfo)){
            channelId =  ChlInfo.getString("GLOBAL_CHNL_ID");
            if(StringUtils.isEmpty(channelId)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "全网渠道统一编码配置缺失");
            }
        }
        inparam.put("CHANNEL_ID",channelId);
        inparam.put("BUSINESS_NAME",CSBizBean.getVisit().getDepartName());

        IDataset dataset = dealInvokeUrl("BIP2B021_T2000021_0_0", "IBOSS", inparam);
        return dataset;
    }
    public static IData querySingleRemoteCust(String idType, String idValue, String userPasswd, String idCardType, String idCardNum, IDataset typeset) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode()); // 省别编码
        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 交易地州编码
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 交易城市代码
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 员工部门编码
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 员工工号
        inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode()); // 路由地州编码
        inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId()); // 部门编号
        inparam.put("ROUTETYPE", "01");
        inparam.put("ROUTEVALUE", idValue);
        inparam.put("KIND_ID", "BIP1A010_T1000008_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "IBOSS");// 交易编码
        inparam.put("ID_TYPE", idType);
        inparam.put("SERIAL_NUMBER", idValue);
        inparam.put("CCPASSWD", userPasswd);
        inparam.put("IDCARD_TYPE", idCardType);
        inparam.put("IDCARD_NUM", idCardNum);
        inparam.put("TYPE_ID", typeset);//0:基本资料   1:个性化资料

        IData ChlInfo = ChnlInfoQry.getGlobalChlId(CSBizBean.getVisit().getDepartId()).getData(0);
        String channelId = "";
        if (IDataUtil.isNotEmpty(ChlInfo)){
            channelId =  ChlInfo.getString("GLOBAL_CHNL_ID");
            if(StringUtils.isEmpty(channelId)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "全网渠道统一编码配置缺失");
            }
        }
        inparam.put("CHANNEL_ID",channelId);
        inparam.put("BUSINESS_NAME",CSBizBean.getVisit().getDepartName());

        IDataset dataset = dealInvokeUrl("BIP1A010_T1000008_0_0", "IBOSS", inparam);
        return dataset.getData(0);
    }
    public static IDataset applyResultActive(String serialNumber, String seq, String imsi, String result, String encKi, String encOpc, String signature) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ID_TYPE", "01");
        inparam.put("ID_VALUE", serialNumber);// SERIAL_NUMBER1
        inparam.put("ROUTETYPE", "01");
        inparam.put("ROUTEVALUE", serialNumber);
        inparam.put("KIND_ID", "BIP2B021_T2000022_0_0");
        inparam.put("X_TRANS_CODE", "IBOSS");
        inparam.put("ORIGDOMAIN", "BOSS");
        inparam.put("HOMEDOMAIN", "BOSS");
        inparam.put("LOCAL_PROVCODE", "898");// input.getString("ROUTETYPE"));
        inparam.put("SERIAL_NUMBER", serialNumber);//手机号
        inparam.put("SEQ", seq);//流水号
        inparam.put("IMSI", imsi);//新imsi号
        inparam.put("ENCK", encKi);
        inparam.put("ENCOPC", encOpc);
        inparam.put("SIGNATURE", signature);
        inparam.put("RESULT", result);//写卡结果0成功1失败

        IData ChlInfo = ChnlInfoQry.getGlobalChlId(CSBizBean.getVisit().getDepartId()).getData(0);
        String channelId = "";
        if (IDataUtil.isNotEmpty(ChlInfo)){
            channelId =  ChlInfo.getString("GLOBAL_CHNL_ID");
            if(StringUtils.isEmpty(channelId)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "全网渠道统一编码配置缺失");
            }
        }
        inparam.put("CHANNEL_ID",channelId);
        inparam.put("BUSINESS_NAME",CSBizBean.getVisit().getDepartName());

        IDataset dataset = dealInvokeUrl("BIP2B021_T2000022_0_0", "IBOSS", inparam);
        return dataset;
    }
    /**
     * 车联网——提交审批接口
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset addCarGroupRateInfo(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP3B622_T3000622_0_0");
        param.put("OPR_SEQ", input.getString("OPR_SEQ", ""));
        param.put("EC_SUBS_ID", input.getString("EC_ID", ""));
        param.put("DISCOUNT_RATE", input.getString("DISCNT_RATE", ""));
        param.put("MACHINE_CARD_BIND", input.getString("CARD_BIND", ""));
        param.put("PROVE", input.getString("PROV_DOC", ""));

        param.put("APPLY_TYPE", input.getString("APPLY_TYPE", ""));//折扣审批类型
        param.put("PROVINCE_ID", "898");//省代码
        param.put("APPLICANT", input.getString("APPLICANT", ""));
        param.put("APPLICANT_PHONE", input.getString("APPLICANT_PHONE", ""));
        param.put("APPLY_DATE", input.getString("APPLY_DATE", ""));
        param.put("APPLY_REASON", input.getString("APPLY_REASON", ""));
        return  dealInvokeUrl("BIP3B622_T3000622_0_0","IBOSS",param);
    }

    /**
     * 短信炸弹保护名单同步接口
     * @param indictSeq 全网唯一编码
     * @param serialNumber 被保护号码
     * @param accessNumber 受理号码
     * @param staff 员工编码
     * @param channelCode 渠道编码
     * @param acceptTime  受理时间格式为：YYYYMMDDHH（24）MMSS
     * @param endTime 生效截止时间  格式为：YYYYMMDDHH（24）MMS
     * @param updateType 更新方式
     * @param Province 被保护号码归属省ID
     */
    public static IData syncProtectedSMSBombList(String indictSeq, String serialNumber, String accessNumber,
                                                 String staff,  String channelCode, String province, String acceptTime,String endTime, String updateType) throws Exception
    {
        IData param = new DataMap();
        param.put("KIND_ID", "BIP2C117_T2002117_0_0");
        param.put("INDICT_SEQ", indictSeq);
        param.put("SUBNUMBER", serialNumber);
        param.put("ACCEPTNUMBER", accessNumber);
        param.put("ACCEPTSTAFF", staff);
        param.put("CONTACTCHANNEL", channelCode);
        param.put("PROVINCE", province);
        param.put("ACCEPTTIME", acceptTime);
        param.put("EFFECTIVETIME", endTime);
        param.put("UPDATETYPE", updateType);

        IDataset dataset = dealInvokeUrl("BIP2C117_T2002117_0_0", "IBOSS6", param);

        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * 跨区密码重置查询卡类型
     * @param input
     * @return
     * @throws Exception
     */
    public static IData queryCardTypeInfo(IData input) throws Exception{
        IData param = new DataMap();
        param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));
        param.put("KIND_ID", "cardTypequery_query_0_0");
        param.put("ID_TYPE", input.getString("ID_TYPE"));
        param.put("ID_VALUE", input.getString("ID_VALUE"));
        param.put("OPR_NUMB", input.getString("OPR_NUMB"));
        param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
        param.put("ROUTEVALUE", input.getString("ID_VALUE"));
        IDataset dataset = dealInvokeUrl("cardTypequery_query_0_0","IBOSS",param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * 跨区密码重置鉴权
     * @param input
     * @return
     * @throws Exception
     */
    public static IData userAuth(IData input) throws Exception{
        IData param = new DataMap();
        param.put("KIND_ID", "openResultAuth_auth_0_0");
        param.put("ID_TYPE", input.getString("ID_TYPE"));
        param.put("ID_VALUE", input.getString("ID_VALUE"));
        param.put("OPR_NUMB", input.getString("OPR_NUMB"));
        param.put("OPR_TIME", input.getString("OPR_TIME"));
        param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));//跨区密码重置
        param.put("ID_CARD_TYPE", input.getString("ID_CARD_TYPE"));//身份证
        param.put("ID_CARD_NUM", input.getString("ID_CARD_NUM"));
        param.put("USER_NAME", input.getString("USER_NAME"));
        param.put("NUMBER_CHECK", input.getString("NUMBER_CHECK"));
        param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
        param.put("ROUTEVALUE", input.getString("ID_VALUE"));
        param.put("CCPASSWD", input.getString("CCPASSWD"));
        if(StringUtils.isNotBlank(input.getString("MESSAGE_CHECK"))){
        	
        	param.put("MESSAGE_CHECK", input.getString("MESSAGE_CHECK"));//验证码
        	param.put("ICC_ID", input.getString("ICC_ID"));
        }
        
        IDataset dataset = dealInvokeUrl("openResultAuth_auth_0_0","IBOSS",param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * 跨区密码重置
     * @param input
     * @return
     * @throws Exception
     */
    public static IData passwordCZ(IData input) throws Exception{
        IData param = new DataMap();
        param.put("KIND_ID", "passwordCZ_bizOrder_0_0");
        param.put("ID_TYPE", input.getString("ID_TYPE"));
        param.put("ID_VALUE", input.getString("ID_VALUE"));
        param.put("OPR_NUMB", input.getString("OPR_NUMB"));
        param.put("OPR_TIME", input.getString("OPR_TIME"));
        param.put("IDENT_CODE", input.getString("IDENT_CODE"));//跨区密码重置
        param.put("CCPASSWD", input.getString("CCPASSWD"));
        param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
        param.put("ROUTEVALUE", input.getString("ID_VALUE"));
        param.put("CHANNEL", input.getString("CHANNEL"));
        IDataset dataset = dealInvokeUrl("passwordCZ_bizOrder_0_0","IBOSS",param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }
	    
	    /**
	 	 * 异地凭证延时
	 	 * @param input
	 	 * @return
	 	 * @throws Exception
	 	 */
	    public static IData authDelayIBOSS(IData input) throws Exception
	    {
	        IData inparam = new DataMap();
	        inparam.put("KIND_ID", "authdelay_auth_0_0");// 交易唯一标识
	        inparam.put("ID_VALUE", input.getString("ID_VALUE"));// 手机号码(按接口规范 必填)
	        inparam.put("ID_TYPE", "01");
	        inparam.put("OPR_NUMB", input.getString("OPR_NUMB"));
	        inparam.put("BIZ_VERSION", "1.0.0");
	        inparam.put("EFFECTIVE_TIME", input.getString("EFFECTIVE_TIME"));
	        inparam.put("CHANNEL", input.getString("CHANNEL","01"));
	        inparam.put("IDENT_CODE", input.getString("IDENT_CODE"));
	        inparam.put("ROUTEVALUE", input.getString("ID_VALUE"));
			inparam.put("ROUTETYPE", "01");
	        IDataset dataset = dealInvokeUrl("authdelay_auth_0_0", "IBOSS", inparam);
	        return dataset.getData(0);
	    }
	    
	    /**
	 	 * 异地鉴权登出
	 	 * @param input
	 	 * @return
	 	 * @throws Exception
	 	 */
	    public static IData authLogoutIBOSS(IData input) throws Exception
	    {
	        IData inparam = new DataMap();
	        inparam.put("KIND_ID", "logout_auth_0_0");// 交易唯一标识
	        inparam.put("ID_VALUE", input.getString("ID_VALUE"));// 手机号码(按接口规范 必填)
	        inparam.put("ID_TYPE", "01");
	        inparam.put("OPR_NUMB", input.getString("OPR_NUMB"));
	        inparam.put("BIZ_VERSION", "1.0.0");
	        inparam.put("CHANNEL", input.getString("CHANNEL","01"));
	        inparam.put("IDENT_CODE", input.getString("IDENT_CODE"));
	        inparam.put("ROUTEVALUE", input.getString("ID_VALUE"));
			inparam.put("ROUTETYPE", "01");
	        IDataset dataset = dealInvokeUrl("logout_auth_0_0", "IBOSS", inparam);
	        return dataset.getData(0);
	    }


    /**
     * 漫游省发起接口-发起销户
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset applyCancelAccount(IData input) throws Exception {
        IData param = new DataMap();
        param.put("KIND_ID", "cancelAccount_bizOrder_0_0");
        param.put("ID_TYPE", "01");
        param.put("ID_VALUE", input.getString("ID_VALUE"));
        param.put("ID_VALUE_NAME", input.getString("ID_VALUE_NAME"));
        param.put("ROUTEVALUE", input.getString("ID_VALUE"));
        param.put("OPR_CODE", input.getString("OPR_CODE"));
        param.put("OPR_NUMB", input.getString("OPR_NUMB"));
        param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        param.put("UD1", input.getDataset("UD1"));
        param.put("IDENT_CODE", input.getString("IDENT_CODE"));
        param.put("BIZ_VERSION", "1.0.0");
        param.put("XH_MARK", input.getString("XH_MARK"));
        param.put("OR_OPR_NUMB", input.getString("OR_OPR_NUMB"));
        param.put("LATE_TIME", input.getString("LATE_TIME"));
        return dealInvokeUrl("cancelAccount_bizOrder_0_0", "IBOSS", param);
    }

    public static IDataset applyCancelAccountSyn(IData input) throws Exception {
        IData param = new DataMap();
        param.put("KIND_ID", "cancelAccountSyn_bizOrder_0_0");
        param.put("ID_TYPE", "01");
        param.put("ID_VALUE", input.getString("ID_VALUE"));
        param.put("OPR_CODE", input.getString("OPR_CODE"));
        param.put("OPR_NUMB", input.getString("OPR_NUMB"));
        param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        if(IDataUtil.isNotEmpty(input.getDataset("UD1"))){
        	param.put("UD1", input.getDataset("UD1"));
        }
        param.put("BIZ_VERSION", "1.0.0");
        param.put("BIZ_ORDER_RESULT", input.getString("BIZ_ORDER_RESULT"));
        param.put("TIP_INFO", input.getString("TIP_INFO"));
        param.put("RESULT_DESC", input.getString("RESULT_DESC"));
        param.put("ROUTEVALUE", input.getString("ROUTEVALUE"));
        param.put("CHECK_RESULT", input.getString("CHECK_RESULT"));
        param.put("XHGD_STATUS", input.getString("XHGD_STATUS"));
        param.put("RETURN_TIME", input.getString("RETURN_TIME",""));
        return dealInvokeUrl("cancelAccountSyn_bizOrder_0_0", "IBOSS", param);
    }
    
    /**
     * 停复机发起接口
     * @param input
     * @return
     * @throws Exception
     */
     public static IData remoteStopOpenMobileIBOSS(IData input) throws Exception
     {
         IData inparam = new DataMap();
         inparam.put("KIND_ID", "stopOpen_bizOrder_0_0");// 交易唯一标识
         inparam.put("ID_VALUE", input.getString("MOBILENUM"));// 手机号码(按接口规范 必填)
         inparam.put("ID_TYPE", "01");
         inparam.put("OPR_NUMB", input.getString("OPR_NUMB"));
         inparam.put("BIZ_VERSION", "1.0.0");
		 inparam.put("ROUTEVALUE", input.getString("MOBILENUM"));
         inparam.put("TKJ_TYPE", input.getString("TKJ_TYPE",""));
         inparam.put("OPR_CODE", input.getString("OPR_CODE",""));
         inparam.put("CHANNEL", input.getString("CHANNEL","01"));
         inparam.put("IDENT_CODE", input.getString("IDENT_CODE"));
         IDataset dataset = dealInvokeUrl("stopOpen_bizOrder_0_0", "IBOSS", inparam);
         return dataset.getData(0);
     }
     
     /**
      * 智能组网一级BOSS工单信息同步接口
      * @param oprNumb
      * @param bizType
      * @param newListNo
      * @param workListNo
      * @param staffName
      * @param staffPhone
      * @param workListUpdateTime
      * @param oprTime
      * @param listAccTime
      * @param goHomeTime
      * @param listState
      * @param bizVersion
      * @return
      * @throws Exception
      */
     public static IDataset listInfoSyn(String oprNumb,String bizType,String newListNo,String workListNo,
    		 String staffName,String staffPhone,String workListUpdateTime,String oprTime,
    		 String listAccTime,String goHomeTime,String listState,String bizVersion) throws Exception
     {
    	 IData inparam = new DataMap();
    	 inparam.put("OPR_NUMB", oprNumb);
    	 inparam.put("BUSI_TYPE", bizType);
    	 inparam.put("PROV_ID", "898");
    	 inparam.put("NEW_LIST_NO", newListNo);
    	 inparam.put("WORK_LIST_NO", workListNo);
    	 inparam.put("STAFF_NAME", staffName);
    	 inparam.put("STAFF_PHONE", staffPhone);
    	 inparam.put("WORK_LIST_UPDATE_TIME", workListUpdateTime);
    	 inparam.put("OPR_TIME", oprTime);
    	 inparam.put("LIST_ACCTIME", listAccTime);
    	 inparam.put("GO_HOME_TIME", goHomeTime);
    	 inparam.put("LIST_STATE", listState);
    	 inparam.put("BIZ_VERSION", bizVersion);
	 
    	 inparam.put("KIND_ID", "ListInfoSyn_bizOrder_0_0");
         IDataset dataset = dealInvokeUrl("ListInfoSyn_bizOrder_0_0", "IBOSS6", inparam);
         return dataset;
     }
     
     /**
      * 智能组网一级BOSS支付信息同步接口
      * @param oprNumb
      * @param bizType
      * @param oprTime
      * @param newListNo
      * @param listState
      * @param workListNo
      * @param payState
      * @param payType
      * @param payChannel
      * @param payNo
      * @param payTime
      * @param totalPrice
      * @param totalDiscount
      * @param realTotalPrice
      * @param payAmount
      * @param remark
      * @param SPID
      * @param bizCode
      * @param campaignId
      * @param bizVersion
      * @return
      * @throws Exception
      */
     public static IDataset payInfoSyn(String oprNumb, String bizType, String oprTime, String newListNo, String listState, String workListNo, String payState, String payType, String payChannel, String payNo, String payTime, String totalPrice, String totalDiscount, String realTotalPrice, String payAmount,
    		 String remark, String SPID, String bizCode, String campaignId, String bizVersion,IDataset payDetails) throws Exception
     {
    	 IData inparam = new DataMap();
    	 inparam.put("OPR_NUMB", oprNumb);
    	 inparam.put("BUSI_TYPE", bizType);
         inparam.put("OPR_TIME", oprTime);//发起方操作时间
    	 inparam.put("NEW_LIST_NO", newListNo);
    	 inparam.put("LIST_STATE", listState);
    	 inparam.put("WORK_LIST_NO", workListNo);
    	 inparam.put("PAY_STATE", payState);//支付状态
    	 inparam.put("PAY_TYPE", payType);//支付类型
    	 inparam.put("PAY_CHANNEL", payChannel);//支付渠道
    	 inparam.put("PAY_NO", payNo);//支付流水号
    	 inparam.put("PAY_TIME", payTime);//支付时间
    	 inparam.put("TOTAL_PRICE", totalPrice);//订购目录总价
    	 inparam.put("TOTAL_DISCOUNT", totalDiscount);//总体折扣率
    	 inparam.put("REAL_TOTAL_PRICE", realTotalPrice);//订购实付总价
    	 inparam.put("PAY_AMOUNT", payAmount);//本次待支付金额
    	 inparam.put("REMARK", remark);//订购备注
    	 inparam.put("SPID", SPID);
    	 inparam.put("BIZ_CODE", bizCode);
    	 inparam.put("CAMPAIGN_ID", campaignId);
    	 inparam.put("BIZ_VERSION", bizVersion);
    	 inparam.put("PAY_DETAILS", payDetails);
    	 inparam.put("KIND_ID", "PayInfoSyn_bizOrder_0_0");
         IDataset dataset = dealInvokeUrl("PayInfoSyn_bizOrder_0_0", "IBOSS6", inparam);
         return dataset;
     }
     
     /**
	  * 智能组网一级BOSS订单信息同步接口
	  * @return
	  * @throws Exception
	  */
     public static IDataset orderInfoSyn(String oprNumb,String bizType,String oprTime,String oprCode,String newListNo,String listState,
    		 String createDate,String bizVersion,String orderId,
    		 String IDV,String city,String cityCode,String country,String countryCode,String districtAddr,
    		 String SPID,String bizCode,String campaignId,String customerName,
    		 String reserveDate,String houseType,String houseTypeCode,String areaSize,String channel,String wbandAccount) throws Exception
     {
         IData inparam = new DataMap();
         inparam.put("OPR_NUMB", oprNumb);//发起方操作流水号
         inparam.put("BUSI_TYPE", bizType);//业务类型代码
         inparam.put("OPR_TIME", oprTime);//发起方操作时间
         inparam.put("OPR_CODE", oprCode);//操作代码 01 订购 02取消
         inparam.put("NEW_LIST_NO", newListNo);//订单编号
         inparam.put("LIST_STATE", listState);//订单交易状态 1：预装订单信息 2：订单信息同步  4：报结信息同步 
         inparam.put("CREATE_DATE",createDate);//订单创建时间
         inparam.put("BIZ_VERSION", bizVersion);//业务版本号
         inparam.put("ORDER_ID",orderId);//和家亲预装下单生成的订单编号
         if(StringUtils.isNotBlank(orderId))
         {
	         inparam.put("BIZ_ORDER_RESULT","0000");//返回码
	         inparam.put("RESULT_DESC", "");//错误描述
         }
         inparam.put("SPID", SPID);//SP企业代码
         inparam.put("BIZ_CODE",bizCode);//业务代码
         inparam.put("CAMPAIGN_ID", campaignId);//营销案ID
         
         IDataset orderDetails = new DatasetList();
         IData orderDetail = new DataMap();    		
         orderDetail.put("PROVINCE_NAME", "海南");//省份名称
         orderDetail.put("PROV_ID", "898");//省公司代码
         orderDetail.put("CUSTOMER_NAME", customerName);//客户姓名
         orderDetail.put("SERIAL_NUMBER", IDV);//使用服务的手机号
         orderDetail.put("CITY",city);//城市名称
         orderDetail.put("CITY_CODE", cityCode);//城市编码
         orderDetail.put("COUNTRY", country);//区/县
         orderDetail.put("COUNTRY_CODE", countryCode);//区域编码
         orderDetail.put("DISTRICT_ADDR", districtAddr);//用户详细地址
         orderDetail.put("RESERVE_DATE", reserveDate);//期望上门时间
         orderDetail.put("HOUSE_TYPE", houseType);//户型描述
         orderDetail.put("HOUSE_TYPE_CODE", houseTypeCode);//户型编码
         orderDetail.put("AREA_SIZE", areaSize);//房屋面积
         orderDetail.put("CHANNEL", channel);//操作渠道
         orderDetail.put("WBAND_ACCOUNT", wbandAccount);//宽带账号
         orderDetails.add(orderDetail);
         inparam.put("ORDER_DETAILS", orderDetails);//预装订单详
         inparam.put("KIND_ID", "OrderInfoSyn_bizOrder_0_0");
         IDataset dataset = dealInvokeUrl("OrderInfoSyn_bizOrder_0_0", "IBOSS6", inparam);
         return dataset;
     }
     /**
      * 智能组网一级BOSS报结信息同步接口
      * @param oprNumb
      * @param oprTime
      * @param bizType
      * @param newListNo
      * @param workListNo
      * @param provID
      * @param listState
      * @param installEndTime
      * @param networkingId
      * @param preCoveragePercent
      * @param coveragePercent
      * @param bizVersion
      * @param positionListBefore
      * @param equipmentListBefore
      * @param positionListAfter
      * @param equipmentListAfter
      * @param zwImageInfo
      * @param evascoreInfo
      * @return
      * @throws Exception
      */
     public static IDataset evaInfoSyn(String oprNumb, String oprTime, String bizType, String newListNo, String workListNo, 
    		 String provID, String listState, String installEndTime, String networkingId, String preCoveragePercent,
    		 String coveragePercent, String bizVersion, IDataset positionListBefore, IDataset equipmentListBefore, 
    		 IDataset positionListAfter, IDataset equipmentListAfter, IData zwImageInfo, IData evascoreInfo) throws Exception
     {
    	 IData inparam = new DataMap();
    	 inparam.put("OPR_NUMB", oprNumb);
    	 inparam.put("OPR_TIME", oprTime);
    	 inparam.put("BUSI_TYPE", bizType);
    	 inparam.put("NEW_LIST_NO", newListNo);
    	 inparam.put("WORK_LIST_NO", workListNo);
    	 inparam.put("PROV_ID", provID);
    	 inparam.put("LIST_STATE", listState);
    	 inparam.put("INSTALL_END_TIME", installEndTime);
    	 inparam.put("NET_WORKING_ID", networkingId);
    	 inparam.put("PRE_COVERAGE_PERCENT", preCoveragePercent);
    	 inparam.put("COVERAGE_PERCENT", coveragePercent);
    	 inparam.put("BIZ_VERSION", bizVersion);
    	 inparam.put("POSITION_LISTBEFORE", positionListBefore);
    	 inparam.put("EQUIPMENT_LIST_BEFORE", equipmentListBefore);
    	 inparam.put("POSITION_LIST_AFTER", positionListAfter);
    	 inparam.put("EQUIPMENT_LIST_AFTER", equipmentListAfter);
    	 inparam.put("ZW_IMAGE_INFO", zwImageInfo);
    	 inparam.put("EVA_SCORE_INFO", evascoreInfo);

    	 inparam.put("KIND_ID", "EvaInfoSyn_HOPT_0_0");
         IDataset dataset = dealInvokeUrl("EvaInfoSyn_HOPT_0_0", "IBOSS6", inparam);
         return dataset;
     }
     /**
      * 订单退订取消同步
      * @param oprNumb
      * @param bizType
      * @param oprTime
      * @param oprCode
      * @param provID
      * @param newListNo
      * @param listState
      * @param cancelType
      * @param refundAmount
      * @param SPID
      * @param bizCode
      * @param campaignId
      * @param bizVersion
      * @return
      * @throws Exception
      */
     public static IDataset tdCancelSyn(String oprNumb, String bizType, String oprTime, String oprCode, String provID,
    		 String newListNo, String listState, String cancelType, String refundAmount,String SPID, String bizCode, 
    		 String campaignId, String bizVersion) throws Exception
     {
    	 IData inparam = new DataMap();
    	 inparam.put("OPR_NUMB", oprNumb);
    	 inparam.put("BIZ_TYPE", bizType);
    	 inparam.put("OPR_TIME", oprTime);
    	 inparam.put("OPR_CODE", oprCode);
    	 inparam.put("PROV_ID", provID);
    	 inparam.put("NEW_LIST_NO", newListNo);
    	 inparam.put("LIST_STATE", listState);
    	 inparam.put("CANCEL_TYPE", cancelType);
    	 inparam.put("REFUND_AMOUNT", refundAmount);
    	 inparam.put("SPID", SPID);
         inparam.put("BIZ_CODE", bizCode);
    	 inparam.put("CAMPAIGN_ID", campaignId);
    	 inparam.put("BIZ_VERSION",bizVersion);

    	 inparam.put("KIND_ID", "TdCancelSyn_bizOrder_0_0");
         IDataset dataset = dealInvokeUrl("TdCancelSyn_bizOrder_0_0", "IBOSS6", inparam);
         return dataset;
     }
     
     /**
	 	 * BOSS主动下单信用购机接口
	 	 * @param input
	 	 * @return
	 	 * @throws Exception
	 	 */
	 	public static IDataset applyCreditPurchases(IData input) throws Exception
	 	{
	 		 IData param = new DataMap();
	 		 param.put("KIND_ID", "BIP2B191_T2040171_0_0");
	 		 param.put("CUS_MBL_NO", input.getString("CUS_MBL_NO", ""));
	 		 param.put("SEQ", input.getString("SEQ", ""));
	 		 param.put("PROV_NO", "898");
	 		 param.put("BUSINESS_TYPE", input.getString("BUSINESS_TYPE", ""));
	 		 param.put("PRODUCT_ID", input.getString("PRODUCT_ID", ""));
	 		 param.put("PRODUCT_NM", input.getString("PRODUCT_NM", ""));
	 		 param.put("PRODUCT_AMT", input.getString("PRODUCT_AMT", ""));
	 		 param.put("PKG_MONTH", input.getString("PKG_MONTH", ""));
	 		 param.put("BONUS_AMT", input.getString("BONUS_AMT", ""));
	 		 param.put("BONUS_MONTH", input.getString("BONUS_MONTH", ""));
	 		 param.put("GOODS_TYPE_ID", input.getString("GOODS_TYPE_ID", ""));
	 		 param.put("GOODS_BRAND", input.getString("GOODS_BRAND", ""));
	 		 param.put("GOODS_NM", input.getString("GOODS_NM", ""));
	 		 param.put("GOODS_PRICE", input.getString("GOODS_PRICE", ""));
	 		 param.put("GOODS_CODE", input.getString("GOODS_CODE", ""));
	 		 param.put("GOODS_DESC", input.getString("GOODS_DESC", ""));	 		 
	 		 param.put("OPR_ID", input.getString("OPR_ID", ""));
	 		 param.put("OPR_MBL_NO", input.getString("OPR_MBL_NO", ""));
	 		 param.put("DEP_ID", input.getString("DEP_ID", ""));
	 		 param.put("DEP_NM", input.getString("DEP_NM", ""));
	 		 return  dealInvokeUrl("BIP2B191_T2040171_0_0","IBOSS6",param);		
	 	}
	 	
	 	
	 	  /**
	 	 * 套餐办理结果通知
	 	 * @param input
	 	 * @return
	 	 * @throws Exception
	 	 */
	 	public static IDataset replyCreditPurchases(IData input) throws Exception
	 	{
	 		 IData param = new DataMap();
	 		 param.put("KIND_ID", "BIP2B193_T2040173_0_0");
	 		 param.put("CUS_MBL_NO", input.getString("CUS_MBL_NO", ""));
	 		 param.put("SEQ", input.getString("SEQ", ""));
	 		 param.put("PROV_NO", "898");
	 		 param.put("MPL_ORD_NO", input.getString("MPL_ORD_NO", ""));
	 		 param.put("MPL_ORD_DT", input.getString("MPL_ORD_DT", ""));
	 		 param.put("ACP_TM", input.getString("ACP_TM", ""));
	 		 param.put("ACP_TYP", input.getString("ACP_TYP", ""));
	 		 param.put("GOODS_CODE", input.getString("GOODS_CODE", ""));	 		
	 		 return  dealInvokeUrl("BIP2B193_T2040173_0_0","IBOSS6",param);		
	 	}

	 	/**
	 	 * 信用购机退货/撤单查询
	 	 * @param input
	 	 * @return
	 	 * @throws Exception
	 	 */
	 	
	 	public static IDataset getCancelWholeNetCreditPurchasesInfo(IData input) throws Exception{
	    	 IData inparam= new DataMap();
	    	 inparam.put("KIND_ID", "BIP2B194_T2040174_0_0");
	    	 inparam.put("PROC_TYP", input.getString("procTyp"));
	    	 inparam.put("CUS_MBL_NO", input.getString("cusMblNo"));
	    	 inparam.put("EX_SEQ", input.getString("ExSeq"));
	    	 inparam.put("MPL_ORD_NO", input.getString("mplOrdNo"));
	    	 inparam.put("MPL_ORD_DT", input.getString("mplOrdDt"));
	    	 IDataset dataset = dealInvokeUrl("BIP2B194_T2040174_0_0", "IBOSS6", inparam);
	    	 return dataset;
	     }
	     
	     public static IDataset getCancelRequestInfo(IData input) throws Exception{
	    	 IData inparam= new DataMap();
	    	 inparam.put("KIND_ID", "BIP2B195_T2040175_0_0");
	    	 inparam.put("PROC_TYP", input.getString("procTyp"));
	    	 inparam.put("REJ_SEQ",input.getString("REJ_SEQ"));
	    	 inparam.put("SEQ", input.getString("ExSeq"));
	    	 inparam.put("CUS_MBL_NO", input.getString("cusMblNo"));
	    	 inparam.put("MPL_ORD_NO", input.getString("mplOrdNo"));
	    	 inparam.put("MPL_ORD_DT", input.getString("mplOrdDt"));
	    	 IDataset dataset = dealInvokeUrl("BIP2B195_T2040175_0_0", "IBOSS6", inparam);
	    	 return dataset;
	     }
	     /**
	      * 国漫赠送关系查询
	      * @param oprType
	      * @param serialNumber
	      * @return
	      * @throws Exception
	      */
	     public static IDataset interRoamGiftQuery(String serialNumber,String operType) throws Exception
	     {
	    	 String sysTime = SysDateMgr.getSysDate("yyyyMMddHHmmss");
	     	IData inparam = new DataMap();
	     	
	     	IData salesInfo = new DataMap();
	     	salesInfo.put("OPER_TYPE", operType);
	     	salesInfo.put("SERIAL_NUMBER", serialNumber);
	     	salesInfo.put("PROV_CODE", "898");
	     	salesInfo.put("OPR_TIME", sysTime);
	     	
	     	IData salesReq = new DataMap();
	     	String seqRealId = SeqMgr.getRealId();
	     	salesReq.put("OPR_NUM", "898"+sysTime+seqRealId);
	     	salesReq.put("USER_SALES_RELATION_INFO", new DatasetList(salesInfo));
	     	
	     	inparam.put("USER_SALES_RELATION_QRY_REQ", new DatasetList(salesReq));        
	         inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
	         inparam.put("KIND_ID", "BIP3A323_T3000326_0_0");
	     	
	         if (logger.isDebugEnabled())
	         {
	         	logger.debug("=====interRoamGiveforIboss=====inparam="+inparam);
	         }
	     	IDataset dataset = dealInvokeUrl("BIP3A323_T3000326_0_0","IBOSS",inparam);
	     	if (logger.isDebugEnabled())
	         {
	         	logger.debug("=====interRoamGiveforIboss=====dataset="+dataset);
	         }
	     	IData result = dataset.getData(0);
	     	if (!result.getString("X_RSPCODE").equals("0000"))
	     	{//查询返回错误直接返回界面
	     		CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getString("X_RSPDESC"));
	     	}
	     	
	     	IData saleInfoRsp = new DatasetList(result.getString("USER_SALES_RELATION_QRY_RSP")).getData(0);
	     	if(IDataUtil.isEmpty(saleInfoRsp))
	     	{
	     		return new DatasetList();
	     	}
	     	IDataset saleInfo = new DatasetList(saleInfoRsp.getString("USER_SALES_RELATION_INFO"));
	     	return saleInfo;
	     }
	     
	     /**
	      * 国漫专属叠加包领取
	      * @param oprType
	      * @param serialNumber
	      * @return
	      * @throws Exception
	      */
	     public static IDataset getInterRoamPackage(String msisdn,String actionType, String prodId,String tmsisdn) throws Exception
	     {
	    	 String sysTime = SysDateMgr.getSysDate("yyyyMMddHHmmss");
	         IData inparam = new DataMap();

	         IData promoReq = new DataMap();
	         promoReq.put("TMSISDN", tmsisdn);
	         promoReq.put("PROD_ID", prodId);
	 		
	 		IData roamInfo = new DataMap();
	 		roamInfo.put("PROMO_REQ", new DatasetList(promoReq));
	 		roamInfo.put("ACTION_TYPE", actionType);
	 		roamInfo.put("OPR_TIMSI", sysTime);
	 		roamInfo.put("PROV_CODE", "898");
	 		roamInfo.put("MSISDN", msisdn);
	 		
	 		IData roamReq = new DataMap();
	 		roamReq.put("INTER_ROAM_BENEFITS_INFO", new DatasetList(roamInfo));
	 		
	 		String seqRealId = SeqMgr.getRealId();
	 		roamReq.put("MSG_TRANSACTION_ID", "898"+sysTime+seqRealId);
	 		
	 		inparam.put("INTER_ROAM_BENEFITS_REQ", new DatasetList(roamReq));        
	         inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
	         inparam.put("KIND_ID", "BIP3A322_T3000325_0_0");

	         if (logger.isDebugEnabled())
	         {
	         	logger.debug("=====interRoamPackageforIboss=====inparam="+inparam);
	         }
	         IDataset dataset = dealInvokeUrl("BIP3A322_T3000325_0_0","IBOSS",inparam);
	         if (logger.isDebugEnabled())
	         {
	         	logger.debug("=====interRoamPackageforIboss=====dataset="+dataset);
	         }
	         
	         //这里返回参数需要转换
	         IData result = dataset.getData(0);
	     	String resultCode = result.getString("CFM_RESULT_CODE","");
	     	if(StringUtils.isNotBlank(resultCode))
	     	{
	     		result.put("X_RSPCODE", result.getString("CFM_RESULT_CODE"));
	     		result.put("X_RSPDESC", result.getString("CFM_RESULT_CODE_DESC"));
	     	}
	     	IDataset results = new DatasetList();
	     	results.add(result);
	        return results;
	     }

	     /**
	      * 订购关系历史优先级查询
	      * @param serialNumber 手机号码
	      * @param beginTimsi 开始时间,endTimsi 结束时间
	      * @return
	      * @throws Exception
	      */
	     public static IDataset interRoamPriorityHisQry(String serialNumber,String beginTimsi,String endTimsi) throws Exception
	     {
	    	 IData inparam3 = new DataMap();
	    	 inparam3.put("PARA_NUM", "0");
	    	 IDataset inputParam3 = new DatasetList();
	    	 inputParam3.add(inparam3);

	    	 IData inparam2 = new DataMap();
	    	 inparam2.put("SERV_PARAM_INFO", inputParam3);
	    	 inparam2.put("END_TIMSI", endTimsi);
	    	 inparam2.put("BEGIN_TIMSI", beginTimsi);
	    	 inparam2.put("OPR_TIMSI", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	    	 inparam2.put("PROV_CODE", "898");
	    	 inparam2.put("MSISDN", serialNumber);
	    	 IDataset inputParam2 = new DatasetList();
	    	 inputParam2.add(inparam2);

	    	 IData inparam = new DataMap();
	    	 inparam.put("MSG_TRANSACTION_ID", "898"+SysDateMgr.getSysDate("yyyyMMddHHmmss")+(int)(Math.random() * 1000000));//发起方的操作流水号
	    	 inparam.put("BILLING_PRIORITY_HIS_QRY_INFO", inputParam2);
	    	 inparam.put("KIND_ID", "BIP3A324_T3000327_0_0");
	    	 IDataset dataset = IBossCall.dealInvokeUrl("BIP3A324_T3000327_0_0","IBOSS6",inparam);
	    	 if (logger.isDebugEnabled())
	    	 {
	    		 logger.debug("=====interRoamPriorityHisQry=====dataset="+dataset);
	    	 }
	    	 IData result = dataset.getData(0);
	    	 if (!result.getString("X_RSPCODE").equals("0000"))
	    	 {//查询返回错误直接返回界面
	    		 CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getString("X_RSPDESC"));
	    	 }

	    	 IData prioInfoRsp = new DatasetList(result.getString("BILLING_PRIORITY_HIS_QRY_RSP")).getData(0);
	    	 if(IDataUtil.isEmpty(prioInfoRsp))
	    	 {
	    		 return new DatasetList();
	    	 }
	    	 IDataset prioInfo = new DatasetList(prioInfoRsp.getString("BILLING_PRIORITY_HIS_INFO"));
	    	 return prioInfo;
	     }
    /**
     * 自有业务-单项能力查询接口调用iboss
     *
     * @return
     * @throws Exception
     */
    public static IData singleAbilityQureySeriveIBOSS(String kindId, String id, String ServiceTypeId, String indictSeq) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("KIND_ID", kindId);
        inparam.put("ID", id);
        inparam.put("SERVICETYPEID", ServiceTypeId);
        inparam.put("INDICTSEQ", indictSeq);

        IDataset dataset = dealInvokeUrl(kindId, "IBOSS", inparam);
        return dataset.getData(0);
    }
    /**
     * 好友号码查询
     * @param input
     * @return
     * @throws Exception
     */
    public static IData numCheckQuery(IData input)throws Exception{
    	IData param = new DataMap();
        param.put("KIND_ID", "NumCheckQuery_query_0_0");
        param.put("ID_TYPE", input.getString("ID_TYPE"));
        param.put("ID_VALUE", input.getString("ID_VALUE"));
        param.put("ROUTETYPE", input.getString("ID_TYPE"));
        param.put("ROUTEVALUE", input.getString("ID_VALUE"));
        param.put("OPR_NUMB", input.getString("OPR_NUMB"));
        param.put("OPR_TIME", input.getString("OPR_TIME"));
        param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));//跨区密码重置
        param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
        IDataset dataset = dealInvokeUrl("NumCheckQuery_query_0_0","IBOSS",param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }
    /**
     * 跨区换卡下发短信 k3
     * @param input
     * @return
     * @throws Exception
     */
    public static IData remoteSendSms(IData input) throws Exception{
        IData param = new DataMap();
        param.put("KIND_ID", "SimpleCardNotice_bizOrder_0_0");
        param.put("ID_TYPE", input.getString("ID_TYPE"));
        param.put("ID_VALUE", input.getString("ID_VALUE"));
        param.put("OPR_NUMB", input.getString("OPR_NUMB"));
        param.put("OPR_TIME", input.getString("OPR_TIME"));
        param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));//跨区换卡
        param.put("SEND_MARK", input.getString("SEND_MARK"));
        param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
        param.put("ROUTEVALUE", input.getString("ID_VALUE"));
        if("1".equals(input.getString("SEND_MARK"))){
        	param.put("MESS_TYPE", input.getString("MESS_TYPE"));
        	param.put("MESS_CHANGE", input.getDataset("MESS_CHANGE"));
        }
        IDataset dataset = dealInvokeUrl("SimpleCardNotice_bizOrder_0_0","IBOSS",param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

	public static IData queryRemindOrder(IData input)throws Exception {
		IData param = new DataMap();
        param.put("KIND_ID", "busRemind_bizOrder_0_0");
        param.put("ID_TYPE", input.getString("ID_TYPE"));
        param.put("ID_VALUE", input.getString("ID_VALUE"));
        param.put("ROUTEVALUE", input.getString("ID_VALUE"));
        param.put("OPR_NUMB", input.getString("OPR_NUMB"));
        param.put("OPR_TIME", input.getString("OPR_TIME"));
        param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));
        param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
        param.put("OPR_TYPE", input.getString("OPR_TYPE"));
        param.put("CD_REASON", input.getString("CD_REASON",""));
        param.put("CX_INFO", input.getDataset("CX_INFO", new DatasetList()));
        param.put("XH_INFO", input.getDataset("XH_INFO", new DatasetList()));
        IDataset dataset = dealInvokeUrl("busRemind_bizOrder_0_0","IBOSS",param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
	}
	
	/**
    * 短信炸弹保护名单同步接口-新
    * @param param
    * @return
    * @throws Exception
    */
   public static IData syncProtectSmsBombInfo(IData param) throws Exception
   {
	   IData paramIn = new DataMap();
	   paramIn.put("KIND_ID", "BIP2C136_T2002136_0_0");
	   paramIn.put("IndictSeq", param.getString("RECV_ID"));
	   paramIn.put("Province", param.getString("PROV_ID"));
	   paramIn.put("SubNumber", param.getString("PROTECT_NUM"));
	   
	   String expireDate = param.getString("EXPIRE_DATE");
	   if(StringUtils.isNotBlank(expireDate))
	   {
		   paramIn.put("EffectiveTime", expireDate);
	   }
	   paramIn.put("UpdateType", param.getString("UPDATE_TYPE"));
	   
	   String whiteList = param.getString("WHITE_LIST","");
	   if(StringUtils.isNotBlank(whiteList))
	   {
		   paramIn.put("WhiteList", param.getString("WHITE_LIST"));
	   }
	   
	   if(logger.isDebugEnabled())
	   {
		   logger.debug("短信炸弹保护名单同步接口参数=ProtectParam=" + paramIn);
	   }
	   
       IDataset dataset = dealInvokeUrl("BIP2C136_T2002136_0_0", "IBOSS6", paramIn);
       
       if(logger.isDebugEnabled())
	   {
		   logger.debug("短信炸弹保护名单同步接口参数=ProtectResult=" + dataset);
	   }
       
       IData result = new DataMap();
       if (IDataUtil.isNotEmpty(dataset))
       {
           result = dataset.getData(0);
       }
       return result;
   }
   
   
   /**
    * 调用一级boss公共接口
    * @param input
    * @return
    * @throws Exception
    * xuzh5 2020-4-18 17:54:47
    */
   public static IData callIbossTool(IData input)throws Exception{
   		IData param = new DataMap();
   		String paramValue="";
   		IDataset paramdata = CommparaInfoQry.getCommparaByCodeCode1("CSM", "420", "IBOSS","666");
        if (IDataUtil.isNotEmpty(paramdata)) 
        	paramValue=paramdata.getData(0).getString("PARA_CODE20","0");
        else 
        	throw new Exception("未配置调用一级boss的参数,需要配置comparam表！");
        int  length=paramValue.split(",").length;
        String value[]=paramValue.split(",");
        for(int i=0;i<length;i++){
        	   param.put(value[i].split("\\|")[0], value[i].split("\\|")[1]);
        }
      /* param.put("KIND_ID", "busRemind_bizOrder_0_0");
       param.put("ID_TYPE", input.getString("ID_TYPE"));
       param.put("ID_VALUE", input.getString("ID_VALUE"));
       param.put("ROUTEVALUE", input.getString("ID_VALUE"));
       param.put("OPR_NUMB", input.getString("OPR_NUMB"));
       param.put("OPR_TIME", input.getString("OPR_TIME"));
       param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));
       param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
       param.put("OPR_TYPE", input.getString("OPR_TYPE"));
       param.put("CD_REASON", input.getString("CD_REASON",""));
       param.put("CX_INFO", input.getDataset("CX_INFO", new DatasetList()));
       param.put("XH_INFO", input.getDataset("XH_INFO", new DatasetList()));*/
       //IDataset dataset = dealInvokeUrl("NumCheckQuery_query_0_0","IBOSS",param);
        /**TransferNum_bizOrder_0_0
         * KIND_ID|TransferNum_bizOrder_0_0,CHANNEL|01,BIZ_TYPE|0601,OPR_SEQ|76q5433as213452d5234w1321a1a1dd22,PROVINCE_ID|898,NUM_FILE_NAME|TransferNum8980220200418090112,ORG_TYPE|02,CURRENT_ORG|02,APPLICANT|SUPERUSR,APPLY_DATE|20200418090112,OPERATOR_ID|TEST
         */
        
        /**TransferSIMCard_bizOrder_0_0
         * KIND_ID|TransferSIMCard_bizOrder_0_0,CHANNEL|01,BIZ_TYPE|0601,OPR_SEQ|76q5433as213452d5234w1321a1a1dd22,PROVINCE_ID|898,SIMCARD_FILE_NAME|TransferSIMCard8980220200418090112,ORG_TYPE|02,CURRENT_ORG|02,APPLICANT|SUPERUSR,APPLY_DATE|20200418090112,OPERATOR_ID|TEST
         */
        
        /**OrgInfoSyn_bizOrder_0_0
         * KIND_ID|OrgInfoSyn_bizOrder_0_0,Be_ID|898,CHANNEL|01,OPR_SEQ|76q5433as213452d5234w1321ad22,MODIFY_MODE|add,ORG_ID|d898021,ORG_NAME|测试组织1,ORG_REGION_CODE|1898,ORG_LEVEL|2,ALL_CHANNEL_CODE|20200418090112,ORG_REGION_INFO|101,ROUTEVALUE|998
         */
        
        /**ModOrgRelationSyn_bizOrder_0_0
         * KIND_ID|ModOrgRelationSyn_bizOrder_0_0,Be_ID|898,CHANNEL|01,OPR_SEQ|76q5433as213452d5234w1321ad22,ORGI_D|d898021,ORG_NAME|测试组织1,NEW_PARENT_ORG_ID|d898022,NEW_PARENT_ORG_NAME|测试组织2,ROUTEVALUE|998
         */
        
       IDataset dataset = dealInvokeUrl(value[0].split("\\|")[0],"IBOSS",param);
       IData result = new DataMap();
       if (IDataUtil.isNotEmpty(dataset))
       {
           result = dataset.getData(0);
       }
       return result;
   }

   
   /**
	 * 物联网资费折扣审批
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static IDataset addCarGroupRateApproveInfo(IData input,IDataset disInfo) throws Exception
	{
       IData param = new DataMap();
       param.put("KIND_ID", "BIP3B622_T3000622_0_0");
       param.put("OPR_SEQ", input.getString("OPR_SEQ", ""));
       param.put("PROVINCE_ID", input.getString("PROVINCE_ID", ""));
       param.put("ECSUBS_ID", input.getString("EC_ID", ""));
       param.put("MAIN_PRODUCTS", input.getString("MAIN_PRODUCT", ""));
       param.put("APPLICATION_ATTR", input.getString("APPLICATION_ATTR", ""));
       param.put("REPORT_NUM", input.getString("REPORT_NUM", ""));
       param.put("CARDS_NO", input.getString("CARDS_NUM", ""));
       param.put("CONTRACT_AWARD_DATE", input.getString("CONTRACT_AWARD_DATE", ""));
       param.put("CONTRACT_EFFECTIVE_DATE", input.getString("CONTRACT_EFFEC_DATE", ""));
       param.put("CONTRACT_EXPIRATION_DATE", input.getString("CONTRACT_EXPIRE_DATE", ""));
       //param.put("DIS_PRODUCTS_INFO", input.getString("DIS_PRODUCTS_INFO", ""));
       param.put("DIS_PRODUCTS_INFO", disInfo);
       param.put("PROVE", input.getString("PROV_DOC", ""));
       param.put("APPLICANT", input.getString("APPLICANT", ""));
       param.put("APPLICANT_PHONE", input.getString("APPLICANT_PHONE", ""));
       param.put("APPLY_DATE", input.getString("APPLY_DATE", ""));
       param.put("APPLY_REASON", input.getString("APPLY_REASON", ""));
       param.put("OPERATION_TYPE", input.getString("OPERATION_TYPE", ""));
       param.put("PBOSS_APPROVAL_NUM", input.getString("PBOSS_APPROVAL_NUM", ""));
       return  dealInvokeUrl("BIP3B622_T3000622_0_0","IBOSS",param);
   }
	
}
