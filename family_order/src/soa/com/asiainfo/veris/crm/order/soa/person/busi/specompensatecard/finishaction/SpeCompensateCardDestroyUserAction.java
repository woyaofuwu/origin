/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.specompensatecard.finishaction;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * @CREATED by gongp@2014-7-21 修改历史 Revision 2014-7-21 上午10:58:05
 */
public class SpeCompensateCardDestroyUserAction implements ITradeFinishAction
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        String serialNumber = mainTrade.getString("SERIAL_NUMBER", "0");
        String olcomTag = mainTrade.getString("RSRV_STR10", "0");//后面改为取RSRV_STR10
        String eparchyCode = mainTrade.getString("EPARCHY_CODE", "0898");
        String tradeId = mainTrade.getString("TRADE_ID");
        String simCardNo = mainTrade.getString("RSRV_STR6");
        String userId = mainTrade.getString("USER_ID");

        IData param = new DataMap();
        if ("1".equals(olcomTag))
        {

            param.put("SERIAL_NUMBER", serialNumber);
            param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            param.put("TRADE_TYPE_CODE", "192");

            CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", param);
        }
        param.clear();

        param.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        param.put("USER_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("STOCK_ID", CSBizBean.getVisit().getDepartId());
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        param.put("IN_MODE_CODE", "0");

        param.put("SIM_CARD_NO", simCardNo);
        param.put("USER_ID", userId);
        param.put("TRADE_ID", tradeId);
        param.put("REMARK", "特殊补偿卡");
        ResCall.specCompCardDeal(param);//CSAppCall.call("RM.ResSimCardIntfSvc.specCompCardDeal", param);
    }

}
