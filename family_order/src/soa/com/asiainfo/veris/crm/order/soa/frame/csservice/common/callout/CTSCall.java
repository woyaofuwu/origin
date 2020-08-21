
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.bcc.ConcurrentKeeper;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class CTSCall
{

    public static Logger logger = Logger.getLogger(CTSCall.class);

    /**
     * 客服接口
     * 
     * @param svcName
     * @param inparams
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static IDataset callCTS(String svcName, IData data) throws Exception
    {
        // 业务并发控制
        ConcurrentKeeper.protect("SVC_crm.call.CTSUrl");

        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url = BizEnv.getEnvString("crm.call.CTSUrl");

        if (logger.isDebugEnabled())
        {
            logger.debug("send   url:" + url);
            logger.debug("send   inparams:" + inparams);
            logger.debug("send   svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");

        if (StringUtils.isBlank(out) || out.equals("null"))
        {
            CSAppException.apperr(BizException.CRM_BIZ_169, out);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("receive  callHttpSvc result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);
        String resultCode = dataset.getData(0).getString("X_RESULTCODE", "-1");
        if (!"0".equals(resultCode))
        {
            String resultInfoString = dataset.getData(0).getString("X_RESULTINFO", "");
            CSAppException.apperr(BizException.CRM_BIZ_169, resultInfoString);
        }

        return dataset;
    }

    /**
     * 投诉信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryFinishedWorkform(String serialnumber, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCEPT_PHONE_CODE", serialnumber);// 查询输入ID值类型 0-USER_ID 1-CUST_ID
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("X_TRANS_CODE", "CRM_Query_FinishedWorkform");
        return callCTS("CRM_Query_FinishedWorkform", param);
    }
}
