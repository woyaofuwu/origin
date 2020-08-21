
package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IotGprsPondRegSVC extends CSBizService
{

    private static final String STR_TRADE_TYPE_CODE = "6111";

    /***
     ** 物联网流量池产品状态变更，给gtm调用
     **/
    public IDataset chgGprsPondState(IData data) throws Exception
    {
        String serviceId = data.getString("SERVICE_ID");
        // IDataset svcStateSet = UserSvcStateInfoQry.getAllBySvcIdStateCode(serviceId, Route.CONN_CRM_CG);

        String userId = data.getString("USER_ID");
        // 流量池状态: 0-正常， E-关闭
        String stateCode = data.getString("STATE_CODE");

        int memNum = IotGprsPondUtil.getMemNumAllCrm(userId);
        int validNum = IotGprsPondUtil.getValidNum();
        // int validNum = 1;

        String operCode = ""; // 操作码: 01-新增，02-删除
        if ("E".equals(stateCode))
        {
            if (memNum < validNum)
            { // 流量池状态为关闭，并且成员数达不到流量池额定人次，不做任何处理
                return null;
            }
            else
            {
                operCode = "01"; // 如果流量池满足条件,开通
            }
        }
        else if ("0".equals(stateCode))
        {
            if (memNum >= validNum)
            { // 流量池状态为开启，并且成员数达到流量池额定人次，流量池继续开启，不做任何处理
                return null;
            }
            else
            {
                operCode = "02"; // 删除
            }
        }
        else
        {
            return null; // 其他状态不处理
        }

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        param.put("TRADE_TYPE_CODE", STR_TRADE_TYPE_CODE);
        param.put("OPER_CODE", operCode);
        param.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        ChgIotGprsPondStateBean bean = new ChgIotGprsPondStateBean();
        return bean.crtTrade(param);
    }

}
