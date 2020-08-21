
package com.asiainfo.veris.crm.order.web.frame.csview.group.common.bboss;

import java.util.List;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BBossTestPage extends GroupBasePage
{
    /**
     * 集团反向接口调用Tcsitf
     */
    public void bbossDataToCrm(IRequestCycle cycle) throws Exception
    {

        IData param = getData("cond", true);
        IData result = new DataMap();
        String BusiSign = param.getString("INTFTYPE", "");
        String requestString = param.getString("parm_Request");
        String erroinfomation = "";
        List list = null;

        try
        {
            list = Wade3DataTran.strToList(requestString);
            IDataset dataset = Wade3DataTran.wade3To4Dataset(list);
            IData paramrequest = new DataMap();
            paramrequest = dataset.getData(0);
            String BbossSvc = "";

            if (BusiSign.equals("onePayMem"))
            {
                paramrequest.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CEN);
                BbossSvc = "SS.TcsGrpIntfSVC.onePayMem";
            }
            else if (BusiSign.equals("dealBbossGroupBiz")||BusiSign.equals("dealBbossMemBiz") || BusiSign.equals("dealBbossOrderStateBiz") || BusiSign.equals("dealBbossOrderDealFaildBiz") || BusiSign.equals("dealBbossOrderOpenBiz") || BusiSign.equals("synBBossGrpMgrBiz"))
            {
                paramrequest.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
                BbossSvc = "CS.TcsGrpIntfSVC." + BusiSign;
            }
            else if (BusiSign.equals("synBBossPoInfo"))
            {
                paramrequest.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CEN);
                BbossSvc = "CS.TcsGrpIntfSVC." + BusiSign;
            }
            IDataset request = CSViewCall.call(this, BbossSvc, paramrequest);

            result.put("pamresult", request);
            setCondition(result);

        }
        catch (Exception e)
        {

            String err = "";
            if (e.getMessage() != null)
            {
                err = e.getMessage();
                int start = err.indexOf("character") + "character".length() + 1;
                int end = err.indexOf("of") - 1;
                String erro = err.substring(0, end + 20);
                String index = err.substring(start, end);
                int st = Integer.valueOf(index);
                erroinfomation = "这个地方的串有问题，自己去检查>>>>>>> " + requestString.substring(st - 10, st + 50) + "<<<<<<<<<<<<<<< " + "\r" + "问题原因：" + erro;
                result.put("pamresult", erroinfomation);
                setCondition(result);
            }
        }

    }

    public abstract IData getCondition();

    public abstract IDataset getInfos();

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    // 订单完工调度
    public void tradeFinish(IRequestCycle cycle) throws Exception
    {

        IData param = getData();
        String orderId = param.getString("ORDER_ID");

        // 服务入参
        IData input = new DataMap();
        // 记录tradeinfo信息
        IDataset tradeInfoList = null;

        input.put("ORDER_ID", orderId);
        input.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);

        tradeInfoList = CSViewCall.call(this, "CS.TradeInfoQrySVC.qryTradeInfoByOrderId", input);

        if (IDataUtil.isEmpty(tradeInfoList))
        {

            for (String routeId : Route.getAllCrmDb())
            {
                input.put("EPARCHY_CODE", routeId);
                input.put(Route.ROUTE_EPARCHY_CODE, routeId);
                tradeInfoList = CSViewCall.call(this, "CS.TradeInfoQrySVC.qryTradeInfoByOrderId", input);
                if (IDataUtil.isNotEmpty(tradeInfoList))
                {
                    break;
                }
            }
        }

        // 无订单信息则直接抛异常
        if (IDataUtil.isEmpty(tradeInfoList))
        {
            CSViewException.apperr(TradeException.CRM_TRADE_225);
        }

        // 服务开通
        input.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(orderId));
        input.put("CANCEL_TAG", "0");
        CSViewCall.call(this, "CS.TradePfSVC.sendPf", input);

        for (int size = 0; size < tradeInfoList.size(); size++)
        {

            IData tradeinfo = tradeInfoList.getData(size);

            tradeinfo.put(Route.ROUTE_EPARCHY_CODE, input.getString(Route.ROUTE_EPARCHY_CODE));
            tradeinfo.put(Route.USER_EPARCHY_CODE, getVisit().getStaffEparchyCode());

            // 订单完工
            CSViewCall.call(this, "CS.TradeFinishSVC.finish", tradeinfo);
        }
    }

}
