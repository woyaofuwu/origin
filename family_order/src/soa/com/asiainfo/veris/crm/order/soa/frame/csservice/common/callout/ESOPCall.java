
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ESOPCall
{

    public static Logger logger = Logger.getLogger(ESOPCall.class);

    /**
     * esop接口
     * 
     * @param svcName
     * @param params
     * @param iscatch
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public final static IDataset callESOP(String svcName, IData data) throws Exception
    {
        data.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

        String inparams = Wade3DataTran.toWadeString(data);

        String url = BizEnv.getEnvString("crm.ESOPUrl");

        if (logger.isDebugEnabled())
        {
            logger.debug("send esop url:" + url);
            logger.debug("send esop inparams:" + inparams);
            logger.debug("send esop svcName:" + svcName);
        }

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");

        if (StringUtils.isBlank(out) || out.equals("null"))
        {
            CSAppException.apperr(BizException.CRM_BIZ_168, out);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("receive esop result:" + out);
        }

        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);
        String resultCode = dataset.getData(0).getString("X_RESULTCODE", "-1");
        if (!"0".equals(resultCode))
        {
            String resultInfoString = dataset.getData(0).getString("X_RESULTINFO", "");
            CSAppException.apperr(BizException.CRM_BIZ_168, resultInfoString);
        }

        return dataset;
    }

}
