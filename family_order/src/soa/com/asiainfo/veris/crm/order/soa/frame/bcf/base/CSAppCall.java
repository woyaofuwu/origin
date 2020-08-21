package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.bcc.ConcurrentKeeper;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.jlcu.Engine;
import com.ailk.jlcu.EngineFactory;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.ailk.service.client.http.HttpHelper;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public final class CSAppCall {

    public static Logger logger = Logger.getLogger(CSAppCall.class);

    public final static IDataset call(String svcName, IData params) throws Exception {
        IDataOutput output = svcFatCall(svcName, params);

        IDataset result = output.getData();

        return result;
    }

    public final static IDataset call(String svcName, IData data, boolean iscatch) throws Exception {
        IDataOutput output = svcFatCall(svcName, data, iscatch);

        IDataset result = output.getData();

        return result;
    }

    public final static IDataset call(String url, String svcName, IData params, boolean isremote) throws Exception {
        IDataOutput output = svcFatCall(url, svcName, params, false, isremote);

        IDataset result = output.getData();

        return result;
    }

    public final static IDataset call(String url, String svcName, IData params, boolean isremote, int soTimeout) throws Exception
    {
        IDataInput input = getDataInput(params);

        IDataOutput output = svcFatCall(url, svcName, input, false, isremote, soTimeout);

        IDataset result = output.getData();

        return result;
    }

    public final static IDataset call(String url, String svcName, IData data, boolean iscatch, boolean isremote) throws Exception
    {
        IDataOutput output = svcFatCall(url, svcName, data, iscatch, isremote);

        IDataset result = output.getData();

        return result;
    }

    /**
     * 服务调用,支持远程和本地调用
     * 
     * @param url
     * @param svcName
     * @param dataInput
     * @param isremote
     * @return
     * @throws Exception
     */
    public final static IDataset call(String url, String svcName, IDataInput dataInput, boolean isremote) throws Exception {
        IDataOutput output = svcFatCall(url, svcName, dataInput, false, isremote);

        IDataset result = output.getData();

        return result;
    }

    /**
     * 账务接口调用
     * 
     * @param svcName
     * @param params
     * @param iscatch
     * @return
     * @throws Exception
     */
    public final static IDataOutput callAcct(String svcName, IData params, boolean iscatch) throws Exception {
        boolean dev = BizEnv.getEnvBoolean("crm.call.acct.dev", false);

        IDataOutput dataOutput = null;

        if (dev == true) {
            // 开发模式下
            String url = BizEnv.getEnvString("crm.call.AcctUrl");

            if (logger.isDebugEnabled()) {
                logger.debug("send acct url:" + url);
                logger.debug("send acct svcName:" + svcName);
                logger.debug("send acct params:" + params.toString());
            }

            dataOutput = svcFatCall(url, svcName, params, iscatch, true);
        } else {
            // 正式环境下

            dataOutput = svcFatCall(svcName, params);
        }

        // 得到头
        IData head = dataOutput.getHead();

        String resultCode = head.getString("X_RESULTCODE", "-1");

        if (!"0".equals(resultCode)) {
            String resultInfo = head.getString("X_RESULTINFO");

            CSAppException.apperr(BizException.CRM_BIZ_167, svcName, resultInfo);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("receive acct result:" + dataOutput.getData().toString());
        }

        return dataOutput;
    }

    /**
     * <<政企订单中心>> 2018-03-14
     * 
     * @param svcName
     * @param params
     * @param iscatch
     * @return
     * @throws Exception
     */
    public final static IDataset callEop(String svcName, IData data) throws Exception {
        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        IDataset dataset = call(svcName, data);
        String resultCode = dataset.getData(0).getString("X_RESULTCODE", "-1");
        if (!"0".equals(resultCode)) {
            String resultInfoString = dataset.getData(0).getString("X_RESULTINFO", "");
            CSAppException.apperr(BizException.CRM_BIZ_168, resultInfoString);
        }

        return dataset;
    }

    public final static IDataOutput callPayment(String svcName, IData params, boolean iscatch) throws Exception {
        boolean dev = BizEnv.getEnvBoolean("crm.call.payment.dev", false);

        IDataOutput dataOutput = null;

        if (dev == true) {
            // 开发模式下
            String url = BizEnv.getEnvString("crm.PaymentUrl");

            if (logger.isDebugEnabled()) {
                logger.debug("send acct url:" + url);
                logger.debug("send acct svcName:" + svcName);
                logger.debug("send acct params:" + params.toString());
            }

            dataOutput = svcFatCall(url, svcName, params, iscatch, true);
        } else {
            // 正式环境下

            dataOutput = svcFatCall(svcName, params);
        }

        // 得到头
        IData head = dataOutput.getHead();

        String resultCode = head.getString("X_RESULTCODE", "-1");

        if (!"0".equals(resultCode)) {
            String resultInfo = head.getString("X_RESULTINFO");

            CSAppException.apperr(BizException.CRM_BIZ_167, svcName, resultInfo);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("receive payment result:" + dataOutput.getData().toString());
        }

        return dataOutput;
    }

    /**
     * 外围接口
     */
    public final static IDataset callCCHT(String svcName, IData params, boolean iscatch) throws Exception {
        params.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());

        if ("".equals(params.getString("TRADE_DEPART_ID", ""))) {
            params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        }

        if (StringUtils.isBlank(params.getString("TRADE_STAFF_ID"))) {
            params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        }

        if ("".equals(params.getString("TRADE_EPARCHY_CODE", ""))) {
            params.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        }

        if ("".equals(params.getString("TRADE_CITY_CODE", ""))) {
            params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        }

        String url = BizEnv.getEnvString("crm.call.CCHTUrl");

        if (logger.isDebugEnabled()) {
            logger.debug("send CCHT url:" + url);
            logger.debug("send CCHT svcName:" + svcName);
            logger.debug("send CCHT params:" + params.toString());
        }

        IDataOutput dataOutput = null;

        if (StringUtils.isBlank(url)) {
            dataOutput = svcFatCall(svcName, params, iscatch);
        } else {
            dataOutput = svcFatCall(url, svcName, params, iscatch, true);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("receive CCHT result:" + dataOutput.getData().toString());
        }

        IDataset result = dataOutput.getData();
        if (IDataUtil.isNotEmpty(result)) {
            IData tmpData = result.getData(0);
            String xResultCode = tmpData.getString("X_RESULTCODE", "0");
            if (!"0".equals(xResultCode)) {
                Utility.error(xResultCode, null, "调外围接口报错[" + tmpData.getString("X_RESULTINFO", "") + "]");
            }
        }

        return dataOutput.getData();
    }

    public static IDataset callHttpFlowPlat(String svcName, IData params, boolean iscatch) throws Exception {

        params.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());

        if ("".equals(params.getString("TRADE_DEPART_ID", ""))) {
            params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        }

        if (StringUtils.isBlank(params.getString("TRADE_STAFF_ID"))) {
            params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        }

        if ("".equals(params.getString("TRADE_EPARCHY_CODE", ""))) {
            params.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        }

        if ("".equals(params.getString("TRADE_CITY_CODE", ""))) {
            params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        }

        String url = BizEnv.getEnvString("crm.call.FlowPlatUrl");

        if (logger.isDebugEnabled()) {
            logger.debug("send CCHT url:" + url);
            logger.debug("send CCHT svcName:" + svcName);
            logger.debug("send CCHT params:" + params.toString());
        }

        IDataOutput dataOutput = null;

        if (StringUtils.isBlank(url)) {
            dataOutput = svcFatCall(svcName, params, iscatch);
        } else {
            dataOutput = svcFatCall(url, svcName, params, iscatch, true);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("receive CCHT result:" + dataOutput.getData().toString());
        }

        IDataset result = dataOutput.getData();
        if (IDataUtil.isNotEmpty(result)) {
            IData tmpData = result.getData(0);
            String xResultCode = tmpData.getString("X_RESULTCODE", "0");
            if (!"0".equals(xResultCode)) {
                Utility.error(xResultCode, null, "调流量平台接口报错[" + tmpData.getString("X_RESULTINFO", "") + "]");
            }
        }

        return dataOutput.getData();
    }

    public final static IData callHttp(String svcName, IData data) throws Exception {
        return callHttp(svcName, data, false);
    }

    public final static IData callHttp(String svcName, IData data, boolean iscatch) throws Exception {
        IDataInput input = getDataInput(data);

        IDataOutput output = HttpHelper.callHttpSvc(svcName, input, iscatch);

        IDataset result = output.getData();

        return result.getData(0);
    }

    public final static Object callLcu(String xTransCode, Object... data) throws Exception {
        Engine engine = EngineFactory.createEngine();

        return engine.executeLCU(xTransCode, data);
    }

    /**
     * pf接口
     */
    public final static IDataOutput callNGPf(String svcName, IData params) throws Exception {
        // 业务并发控制
        ConcurrentKeeper.protect("SVC_crm.call.NGPFUrl");

        String url = BizEnv.getEnvString("crm.call.NGPFUrl");

        IDataOutput dataOutput = svcFatCall(url, svcName, params, false, true);

        return dataOutput;
    }

    public final static IData callOne(String svcName, IData params) throws Exception {
        IDataOutput output = svcFatCall(svcName, params);

        IDataset resultList = output.getData();

        if (IDataUtil.isEmpty(resultList)) {
            return new DataMap();
        }

        return resultList.getData(0);
    }

    public final static IData callOne(String url, String svcName, IData params, boolean isremote) throws Exception {
        IDataOutput output = svcFatCall(url, svcName, params, false, isremote);

        IDataset resultList = output.getData();

        if (IDataUtil.isEmpty(resultList)) {
            return new DataMap();
        }

        return resultList.getData(0);
    }

    /**
     * 终端接口
     */
    public final static IDataOutput callTerminal(String svcName, IData params, boolean iscatch) throws Exception {
        boolean dev = BizEnv.getEnvBoolean("crm.call.terminal.dev", false);

        IDataOutput dataOutput = null;

        if (dev == true) {
            // 开发模式下
            String url = BizEnv.getEnvString("crm.call.TerminalUrl");

            if (logger.isDebugEnabled()) {
                logger.debug("send terminal url:" + url);
                logger.debug("send terminal svcName:" + svcName);
                logger.debug("send terminal params:" + params.toString());
            }

            dataOutput = svcFatCall(url, svcName, params, iscatch, true);
        } else {
            // 正式环境下

            dataOutput = svcFatCall(svcName, params);
        }

        // 得到头
        IData head = dataOutput.getHead();

        String resultCode = head.getString("X_RESULTCODE", "-1");

        if (!"0".equals(resultCode)) {
            String resultInfo = head.getString("X_RESULTINFO");

            CSAppException.apperr(BizException.CRM_BIZ_170, svcName, resultInfo);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("receive terminal result:" + dataOutput.getData().toString());
        }

        return dataOutput;
    }

    private final static IDataInput getDataInput(IData data) throws Exception {
        IVisit visit = CSBizBean.getVisit();

        IDataInput input = null;

        String inModeCode = data.getString("IN_MODE_CODE");

        if (StringUtils.isBlank(inModeCode)) {
            input = DataHelper.createDataInput(visit, data, null);
        } else {
            input = DataHelper.createDataInput(visit, data, null, new String[] { "IN_MODE_CODE" }, new String[] { inModeCode });
        }

        return input;
    }

    private final static IDataOutput svcFatCall(String svcName, IData data) throws Exception {
        IDataInput input = getDataInput(data);

        IDataOutput output = ServiceFactory.call(svcName, input);

        return output;
    }

    private final static IDataOutput svcFatCall(String svcName, IData data, boolean iscatch) throws Exception {
        IDataInput input = getDataInput(data);

        IDataOutput output = ServiceFactory.call(svcName, input, null, iscatch);

        return output;
    }

    private final static IDataOutput svcFatCall(String url, String svcName, IData data, boolean iscatch, boolean isremote) throws Exception {
        IDataInput input = getDataInput(data);

        IDataOutput output = ServiceFactory.call(url, svcName, input, null, iscatch, isremote);

        return output;
    }

    private final static IDataOutput svcFatCall(String url, String svcName, IDataInput input, boolean iscatch, boolean isremote) throws Exception {
        IDataOutput output = ServiceFactory.call(url, svcName, input, null, iscatch, isremote);

        return output;
    }

    private final static IDataOutput svcFatCall(String url, String svcName, IDataInput input, boolean iscatch, boolean isremote, int soTimeout) throws Exception
    {
        IDataOutput output = ServiceFactory.call(url, svcName, input, null, iscatch, isremote, soTimeout, 1000);

        return output;
    }
}
