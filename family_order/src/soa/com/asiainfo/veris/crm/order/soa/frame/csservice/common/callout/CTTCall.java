
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.bcc.ConcurrentKeeper;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class CTTCall
{
    public static Logger logger = Logger.getLogger(CTTCall.class);

    // 铁通
    public static IDataset callCTT(String svcName, IData inparams) throws Exception
    {
        String url = BizEnv.getEnvString("crm.call.CTTUrl");

        IDataset result = null;

        if (StringUtils.isBlank(url))
        {
            result = CSAppCall.call(svcName, inparams, false);
        }
        else
        {
            // 业务并发控制
            ConcurrentKeeper.protect("SVC_crm.call.CTTUrl");

            result = CSAppCall.call(url, svcName, inparams, false, true);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("cttsvc result: " + result);
        }

        return result;
    }

    /**
     * 关联铁通停开机
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-6
     */
    public static IDataset changeCttState(String tradeTypeCode, String sourceSerialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带退单
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param productType
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-10-18
     */
    public static IDataset ChargebackTradeReg(String tradeTypeCode, String channelTradeId, String sourceSerialNumber, String recvFee, String busiFee, String resCost, String logId) throws Exception
    {
        IData data = new DataMap();
        data.put("CHANNEL_TRADE_ID", channelTradeId);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("RECV_FEE", recvFee);
        data.put("BUSI_FEE", busiFee);
        data.put("RES_COST", resCost);
        data.put("LOG_ID", logId);
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 校验号码是否可用
     * 
     * @param tradeTypeCode
     * @param servNum
     * @param chargeId
     * @param tradeId
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-9-4
     */
    public static IDataset checkPhone(String tradeTypeCode, String servNum, String chargeId, String tradeId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", servNum);
        data.put("CHARGE_ID", chargeId);
        data.put("TRADE_ID", tradeId);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 后付费话费缴纳
     * 
     * @param visit
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param saleType
     * @param recvFee
     * @param presentMoney
     * @param validDate
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-9-27
     */
    public static IDataset CTTBACKFULLMONEY(String tradeTypeCode, String ServNum, String recvFee, String logId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", ServNum);
        data.put("RECV_FEE", recvFee);
        data.put("LOG_ID", logId);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 后付费话费缴纳返销
     * 
     * @param visit
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param saleType
     * @param recvFee
     * @param presentMoney
     * @param validDate
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-9-27
     */
    public static IDataset CTTBACKPAYMENTRESTROY(String tradeTypeCode, String ServNum, String recvFee, String pricePlanId, String chargeId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", ServNum);
        data.put("PRODUCT_PRICE", pricePlanId);
        data.put("CHANNEL_TRADE_ID", chargeId);
        data.put("RECV_FEE", recvFee);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 铁通通用接口(数据太多，如开户等接口走此接口)
     * 
     * @param data
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-19
     */
    public static IDataset CTTCommonCall(IData data) throws Exception
    {
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带绑定
     * 
     * @param tradeTypeCode
     * @param tradeId
     * @param sourceSerialNumber
     * @param mobileSerialNumber
     * @param usrName
     * @param productPrice
     * @param endDate
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-10-17
     */
    public static IDataset cttNetBundle(String tradeTypeCode, String tradeId, String sourceSerialNumber, String mobileSerialNumber, String usrName, String productPrice, String endDate) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("CHARGE_ID", tradeId);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("SERIAL_NUMBER", mobileSerialNumber);
        data.put("USR_NAME", usrName);
        data.put("PRODUCT_PRICE", productPrice);
        data.put("END_DATE", endDate);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带销户
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param productType
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-10-18
     */
    public static IDataset cttNetDestroy(String tradeTypeCode, String sourceSerialNumber, String productType) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("PRODUCT_TYPE", productType);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带续费
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param productType
     * @param productExplain
     * @param productPrice
     * @param userTypeCode
     * @param wwwCapacity
     * @param billFlag
     * @param recvFee
     * @param inModeCode
     * @param userGroupId
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-11-17
     */
    public static IDataset cttNetRecharge(String tradeTypeCode, String sourceSerialNumber, String productType, String productExplain, String productPrice, String userTypeCode, String wwwCapacity, String billFlag, String recvFee, String inModeCode,
            String userGroupId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("PRODUCT_TYPE", productType);
        data.put("PRODUCT_EXPLAIN", productExplain);
        data.put("PRODUCT_PRICE", productPrice);
        data.put("USER_TYPE_CODE", userTypeCode);
        data.put("WWW_CAPACITY", wwwCapacity);
        data.put("BILLFLG", billFlag);
        data.put("RECV_FEE", recvFee);
        data.put("IN_MODE_CODE", inModeCode);
        data.put("USER_GROUP_ID", userGroupId);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带冲正
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param productPrice
     * @param recvFee
     * @param logId
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-10-18
     */
    public static IDataset cttNetReversal(String tradeTypeCode, String sourceSerialNumber, String productPrice, String recvFee, String logId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("PRODUCT_PRICE", productPrice);
        data.put("RECV_FEE", recvFee);
        data.put("LOG_ID", logId);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 后付费话费信息
     * 
     * @param visit
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param saleType
     * @param recvFee
     * @param presentMoney
     * @param validDate
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-9-27
     */
    public static IDataset CTTQUERYENTRYREVERSAL(String tradeTypeCode, String ServNum, String startDate, String endDate, String limitTradeTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SOURCE_SERIALNUMBER", ServNum);
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        data.put("LIMIT_TRADE_TYPE_CODE", limitTradeTypeCode);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 无线座机充值
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param saleType
     * @param recvFee
     * @param presentMoney
     * @param validDate
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-21
     */
    public static IDataset cttRecharge(String tradeTypeCode, String sourceSerialNumber, String saleType, String recvFee, String presentMoney, String validDate) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("SALE_TYPE", saleType);
        data.put("RECV_FEE", recvFee);
        data.put("PRESENTMONEY", presentMoney);
        data.put("VALID_DATE", validDate);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 无线座机冲正
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param saleType
     * @param recvFee
     * @param presentMoney
     * @param logId
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-22
     */
    public static IDataset cttReversal(String tradeTypeCode, String sourceSerialNumber, String saleType, String recvFee, String presentMoney, String logId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("SALE_TYPE", saleType);
        data.put("RECV_FEE", recvFee);
        data.put("PRESENTMONEY", presentMoney);
        data.put("LOG_ID", logId);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 状态变更类通用接口
     * 
     * @param tradeTypeCode
     * @param servNum
     * @param productType
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-20
     */
    public static IDataset cttStateChange(String tradeTypeCode, String servNum, String productType) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", servNum);
        data.put("PRODUCT_TYPE", productType);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 根据产品分类信息从参数表中取出产品和产品销售包
     * 
     * @data 2013-9-29
     * @param product_type
     * @return
     * @throws Exception
     */
    public static IDataset getProductAndPackage(String tradeTypeCode, String productType) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_TYPE", productType);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);// 1594
        setPublicParam(param);

        return callCTT("ITF_CCHT_TIETSOC", param);
    }

    /**
     * 查询铁通用户修改密码
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-6
     */
    public static IDataset ModifyPasswd(String tradeTypeCode, String sourceSerialNumber, String NPWD) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("NPWD", NPWD);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带价格计划修改
     * 
     * @param tradeTypeCode
     * @param servNum
     * @param productType
     * @param productExplain
     * @param productPrice
     * @param userTypeCode
     * @param wwwCapaCity
     * @param billFlg
     * @param recvFee
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-9-4
     */
    public static IDataset modiNetProductPrice(String tradeTypeCode, String servNum, String productType, String productExplain, String productPrice, String userTypeCode, String wwwCapaCity, String billFlg, String recvFee) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", servNum);
        data.put("PRODUCT_TYPE", productType);
        data.put("PRODUCT_EXPLAIN", productExplain);
        data.put("PRODUCT_PRICE", productPrice);
        data.put("USER_TYPE_CODE", userTypeCode);
        data.put("WWW_CAPACITY", wwwCapaCity);
        data.put("BILLFLG", billFlg);
        data.put("RECV_FEE", recvFee);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 无线座机价格计划修改
     * 
     * @param tradeTypeCode
     * @param servNum
     * @param productType
     * @param productExplain
     * @param productPrice
     * @param effectTimeTag
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-22
     */
    public static IDataset modiProductPrice(String tradeTypeCode, String servNum, String productType, String productExplain, String productPrice, String effectTimeTag) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", servNum);
        data.put("PRODUCT_TYPE", productType);
        data.put("PRODUCT_EXPLAIN", productExplain);
        data.put("PRODUCT_PRICE", productPrice);
        data.put("EFFECT_TIME_TAG", effectTimeTag);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 计费区查询
     * 
     * @param tradeTypeCode
     * @param sourceSn
     * @param productType
     * @param productExplain
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-16
     */
    public static IDataset qryBillingTypeInfos(String tradeTypeCode, String sourceSn, String productType, String productExplain) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSn);
        data.put("PRODUCT_TYPE", productType);
        data.put("PRODUCT_EXPLAIN", productExplain);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 查询促销活动标识
     * 
     * @param tradeTypeCode
     * @param productType
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-8
     */
    public static IDataset qryChargeDiscnt(String tradeTypeCode, String productType) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("PRODUCT_TYPE", productType);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 促销换算
     * 
     * @param tradeTypeCode
     * @param saleType
     * @param recvFee
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-13
     */
    public static IDataset qryChargeDiscntChange(String tradeTypeCode, String saleType, String recvFee) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SALE_TYPE", saleType);
        data.put("RECV_FEE", recvFee);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带冲正查询
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @param startDate
     * @param endDate
     * @param limitTradeTypeCode
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-10-17
     */
    public static IDataset qryCTTNetReversal(String tradeTypeCode, String sourceSerialNumber, String startDate, String endDate, String limitTradeTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        data.put("LIMIT_TRADE_TYPE_CODE", limitTradeTypeCode);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 查询铁通宽带用户信息
     * 
     * @param tradeTypeCode
     * @param queryType
     * @param para1
     * @param para2
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-9-3
     */
    public static IDataset qryCTTNETUserInfo(String tradeTypeCode, String queryTypeCode, String para1, String para2) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("QUERY_TYPE_CODE", queryTypeCode);
        if ("1".equals(queryTypeCode))
        {
            data.put("SOURCE_SERIALNUMBER", para1);
        }
        else if ("2".equals(queryTypeCode))
        {
            data.put("PSPT_TYPE", para1);
            data.put("PSPT_ID", para2);
        }
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 查询宽带用户开通进度
     * 
     * @param tradeTypeCode
     * @param serialNumber
     * @param sourceSerialNumber
     * @param chargeId
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-7
     */
    public static IDataset qryCTTNetUserOpenInfo(String tradeTypeCode, String serialNumber, String sourceSerialNumber, String chargeId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("CHARGE_ID", chargeId);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 查询铁通用户信息
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-6
     */
    public static IDataset qryCTTUserInfo(String tradeTypeCode, String sourceSerialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 查询设备信息
     * 
     * @param tradeTypeCode
     * @param deviceCode
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-8
     */
    public static IDataset qryDeviceInfos(String tradeTypeCode, String deviceCode) throws Exception
    {
        IData data = new DataMap();
        setPublicParam(data);
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("EPARCHY_CODE", deviceCode);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 铁通局向数据查询
     * 
     * @param tradeTypeCode
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-8
     */
    public static IDataset qryMofficeInfo(String tradeTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 查询铁通用户信息
     * 
     * @param tradeTypeCode
     * @param sourceSerialNumber
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-6
     */
    public static IDataset qryNetUserInfo(String tradeTypeCode, String queryTypeCode, String sourceSerialNumber, String PSPT_ID, String PSPT_TYPE) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("QUERY_TYPE_CODE", "1");
        data.put("SOURCE_SERIALNUMBER", sourceSerialNumber);
        data.put("PSPT_ID", sourceSerialNumber);
        data.put("PSPT_TYPE", "1");
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 铁通用户上网记录查询
     * 
     * @param tradeTypeCode
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-8
     */
    public static IDataset qryOnlineRecordInfo(String tradeTypeCode, String sn, String startDate, String endDate) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sn);
        data.put("START_TIME", SysDateMgr.decodeTimestamp(startDate, "yyyyMMddHH"));
        data.put("END_TIME", SysDateMgr.decodeTimestamp(endDate, "yyyyMMddHH"));
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 状态变更类通用接口
     * 
     * @param tradeTypeCode
     * @param servNum
     * @param productType
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-20
     */
    public static IDataset qryPostCttUserInfo(String tradeTypeCode, String servNum, String productType) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", servNum);
        data.put("PRODUCT_TYPE", productType);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 查询宽带产品及销售包
     * 
     * @param tradeTypeCode
     * @param productType
     * @return
     * @throws Exception
     *             wangjx 2013-9-22
     */
    public static IDataset qryProductExplain(String tradeTypeCode, String productType) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("PRODUCT_TYPE", productType);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 价格计划标识查询
     * 
     * @param tradeTypeCode
     * @param productType
     * @param productExplain
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-16
     */
    public static IDataset qryProductPriceInfos(String tradeTypeCode, String productType, String productExplain) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("PRODUCT_TYPE", productType);
        data.put("PRODUCT_EXPLAIN", productExplain);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 铁通用户充值、冲正信息查询，宽带、无线座机等用户用LIMIT_TRADE_TYPE_CODE区分
     * 
     * @param tradeTypeCode
     * @param limitTradeTypeCode
     * @param servNum
     * @param startDate
     * @param endDate
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-22
     */
    public static IDataset qryRechargeInfo(String tradeTypeCode, String limitTradeTypeCode, String servNum, String startDate, String endDate) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("LIMIT_TRADE_TYPE_CODE", limitTradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", servNum);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 铁通选号接口
     * 
     * @param tradeTypeCode
     * @param productType
     * @param switchIdr
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-8-12
     */
    public static IDataset qryServNum(String tradeTypeCode, String productType, String switchIdr) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("PRODUCT_TYPE", productType);
        data.put("SWITCH_IDR", switchIdr);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 查询局向
     * 
     * @param tradeTypeCode
     * @return IDataset
     * @throws Exception
     * @author wangww
     */
    public static IDataset queryMoffices(String tradeTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带查询价格计划
     * 
     * @param tradeTypeCode
     * @param productType
     * @param productExplain
     * @param userTypeCode
     * @param wwwCapacity
     * @param userGroupId
     * @param inModeCode
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-11-17
     */
    public static IDataset queryProductPriceInfos(String tradeTypeCode, String productType, String productExplain, String userTypeCode, String wwwCapacity, String userGroupId, String inModeCode) throws Exception
    {
        IData data = new DataMap();
        setPublicParam(data);
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("PRODUCT_TYPE", productType);
        data.put("PRODUCT_EXPLAIN", productExplain);
        data.put("USER_TYPE_CODE", userTypeCode);
        data.put("WWW_CAPACITY", wwwCapacity);
        data.put("USER_GROUP_ID", userGroupId);
        data.put("IN_MODE_CODE", inModeCode);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带捆绑
     * 
     * @param tradeTypeCode
     * @param serialNumber
     * @param sourceSerialnumber
     * @param chargeId
     * @param custName
     * @param productPrice
     * @param startDate
     * @param endDate
     * @return IDataset
     * @throws Exception
     * @author wangww
     */
    public static IDataset queryTietBindInfos(String tradeTypeCode, String serialNumber, String sourceSerialnumber, String chargeId, String custName, String productPrice, String startDate, String endDate) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialnumber);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("CHARGE_ID", chargeId);
        data.put("CUST_NAME", custName);
        data.put("PRODUCT_PRICE", productPrice);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带信息查询
     * 
     * @param tradeTypeCode
     * @param sourceSerialnumber
     * @param queryTypeCode
     * @param psptId
     * @param psptType
     * @return IDataset
     * @throws Exception
     * @author wangww
     */
    public static IDataset queryTietBusyUserInfos(String tradeTypeCode, String sourceSerialnumber, String queryTypeCode, String psptId, String psptType) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialnumber);
        data.put("QUERY_TYPE_CODE", queryTypeCode);
        data.put("PSPT_ID", psptId);
        data.put("PSPT_TYPE", psptType);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带解绑
     * 
     * @param tradeTypeCode
     * @param serialNumber
     * @param sourceSerialnumber
     * @param chargeId
     * @return IDataset
     * @throws Exception
     * @author wangww
     */
    public static IDataset queryTietCanCelInfos(String tradeTypeCode, String serialNumber, String sourceSerialnumber, String chargeId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialnumber);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("CHARGE_ID", chargeId);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带验证
     * 
     * @param tradeTypeCode
     * @param sourceSerialnumber
     * @return IDataset
     * @throws Exception
     * @author wangww
     */
    public static IDataset queryTietSerialNumberCheck(String tradeTypeCode, String sourceSerialnumber) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("SOURCE_SERIALNUMBER", sourceSerialnumber);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 宽带查询用户群
     * 
     * @param tradeTypeCode
     * @return IDataset
     * @throws Exception
     * @author wangww
     */
    public static IDataset queryUserGroupInfos(String tradeTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 用户群查询
     * 
     * @param tradeTypeCode
     * @return IDataset
     * @throws Exception
     * @author wangww
     */
    public static IDataset queryUserGroups(String tradeTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

    /**
     * 公共信息参数设置
     * 
     * @param data
     * @throws Exception
     *             wangjx 2013-8-8
     */
    private static void setPublicParam(IData data) throws Exception
    {
        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());// 省编码
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入渠道
        String routeId = CSBizBean.getVisit().getStaffEparchyCode();
        // 客服工号，HNAN, 07XX 则默认地州编码
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
     * 根据产品分类信息从参数表中取出产品和产品销售包
     * 
     * @data 2013-9-29
     * @param product_type
     * @return
     * @throws Exception
     */
    public static IDataset tradeChangeCustReg(String tradeTypeCode, String serialNumber, String custName, String contact, String contactPhone, String address, String eparchycode, String contactPostCode, String userPasswd, String productType)
            throws Exception
    {
        IData data = new DataMap();
        data.put("SOURCE_SERIALNUMBER", serialNumber);
        data.put("CUST_NAME", custName);
        data.put("CONTACT", contact);
        data.put("CONTACT_PHONE", contactPhone);
        data.put("ADDRESS", address);
        data.put("EPARCHY_CODE", eparchycode);
        data.put("CONTACT_POSTCODE", contactPostCode);
        data.put("USER_PASSWD", userPasswd);
        data.put("PRODUCT_TYPE", productType);
        data.put("TRADE_TYPE_CODE", tradeTypeCode);// 1594
        setPublicParam(data);

        return callCTT("ITF_CCHT_TIETSOC", data);
    }

}
