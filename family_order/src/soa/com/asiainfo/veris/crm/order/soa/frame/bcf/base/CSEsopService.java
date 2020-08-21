
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizService;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.InModeCodeUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class CSEsopService extends BizService
{

    private static Logger log = Logger.getLogger(CSEsopService.class);

    public final void initialize(IData input) throws Exception
    {
        super.initialize(input);

        // 设置服务路由
        this.setRoute(input);
    }

    /**
     * 添加公共参数
     * 
     * @param commData
     * @return
     * @throws Exception
     */
    public IData setCommData(IData commData) throws Exception
    {
        commData.put("CITY_CODE", this.getVisit().getCityCode());
        commData.put("EPARCHY_CODE", this.getVisit().getLoginEparchyCode());
        commData.put("DEPART_ID", this.getVisit().getDepartId());
        commData.put("DEPART_NAME", this.getVisit().getDepartName());
        commData.put("STAFF_ID", this.getVisit().getStaffId());
        commData.put("STAFF_NAME", this.getVisit().getStaffName());
        commData.put("STAFF_PHONE", this.getVisit().getSerialNumber());
        commData.put("CLIENT_IP", this.getVisit().getRemoteAddr());
        return commData;
    }

    private void setRoute(IData input) throws Exception
    {
        // 路由类型
        String routeType = getAttribute("route");

        // 路由类型是否为空
        if (StringUtils.isBlank(routeType))
        {
            CSAppException.apperr(BizException.CRM_BIZ_651);
        }

        if (log.isDebugEnabled())
        {
            log.debug("routeType=" + routeType);
        }

        // 路由ID
        String routeId = "";

        if ("routeToEp".equals(routeType))
        {
            routeId = "esop";
        }
        else if ("routeToCen".equals(routeType))
        {
            routeId = "cen";
        }
        else if ("routeToUpc".equals(routeType))
        {
            routeId = Route.CONN_UPC;
        }
        else
        {
            CSAppException.apperr(BizException.CRM_BIZ_652);
        }
        
        // 用户归属地州
        String userEparchyCode = input.getString(Route.USER_EPARCHY_CODE, "");

        // 用户归属地州, 如果为空, 默认为路由地州
        if (StringUtils.isBlank(userEparchyCode) && routeId.length() == 4 && StringUtils.isNumeric(routeId))
        {
            userEparchyCode = routeId;
        }

        // 交易地州
        String tradeEparchyCode = CSBizBean.getVisit().getLoginEparchyCode();

        // 接入渠道
        String inModeCode = CSBizBean.getVisit().getInModeCode();

        // 当前工号
        String staffId = CSBizBean.getVisit().getStaffId();

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
            // 接口用户地州转化
            if (InModeCodeUtil.isIntf(inModeCode, input.getString("X_SUBTRANS_CODE"), input.getString("BATCH_ID")) && (StringUtils.isBlank(userEparchyCode) || !StringUtils.isNumeric(userEparchyCode)))
            {
                userEparchyCode = Route.getCrmDefaultDb();
            }

            if (!StringUtils.equals(inModeCode, "0"))
            {
                // 接口传的是TRADE_STAFF_ID,TRADE_DEPART_ID,...
                AsynDealVisitUtil.dealVisitInfo(input);
            }

            // 非营业厅，或有全省权限，交易地州=用户地州
            if ((StringUtils.isNotBlank(staffId) && (!"0".equals(inModeCode) || StaffPrivUtil.isSysProvince(staffId) == true)) && StringUtils.isNotBlank(userEparchyCode))
            {
                tradeEparchyCode = userEparchyCode;
            }
        }
        else
        {
            // 非营业厅，或有全省权限，交易地州=用户地州
            if ((!"0".equals(inModeCode) || StaffPrivUtil.isSysProvince(staffId) == true) && StringUtils.isNotBlank(userEparchyCode))
            {
                tradeEparchyCode = userEparchyCode;
            }
        }

        // HNAN 07XX 等非数字地州，都转换成用户归属地州
        if (!StringUtils.isNumeric(tradeEparchyCode) && StringUtils.isNotBlank(userEparchyCode))
        {
            tradeEparchyCode = userEparchyCode;
        }

        // 设置 服务路由ID
        setRouteId(routeId);
        
        // 设置 用户归属地州
        setUserEparchyCode(userEparchyCode);

        // 设置 交易地州
        setTradeEparchyCode(tradeEparchyCode);
    }
}
