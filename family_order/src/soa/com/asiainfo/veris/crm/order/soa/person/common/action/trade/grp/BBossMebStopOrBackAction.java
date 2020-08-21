
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.grp;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BBossMebStopOrBackAction.java
 * @Description: 集团成员服务状态变更，对于老系统TCS_BBossMebStopOrBack
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-7-25 下午10:05:29
 */
public class BBossMebStopOrBackAction implements ITradeAction
{
    /**
     * 集团接口返回值 <IDataset> <IData> <key>GRP_TRADE_DATA</key> <value> List </value> ... .... </IData> </IDataset>
     * 如果没有需要处理的iTrade请直接new IDatasetList()对象出来。
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String tradeTypeCode = btd.getTradeTypeCode();
        String eparchyCode = btd.getRD().getUca().getUser().getEparchyCode();

        IData params = new DataMap();
        params.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        params.put("USER_EPARCHY_CODE", eparchyCode);
        params.put("USER_ID", userId);
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("TRADE_TYPE_CODE", tradeTypeCode);
        params.put("ACCEPT_TIME", btd.getRD().getAcceptTime());// 工单受理时间，传给集团，尽量保证他们工单的执行时间和我们的一致
        IDataset grpTrade = CSAppCall.call("SS.GrpCreditSVC.bbossMemberStopOrBack", params);
        if (IDataUtil.isNotEmpty(grpTrade))
        {
            IData data = grpTrade.getData(0);

            List rtTradelList = (List) data.get("GRP_TRADE_DATA");
            if (null != rtTradelList && rtTradelList.size() > 0)
            {
                for (int i = 0; i < rtTradelList.size(); i++)
                {
                    BaseTradeData tData = (BaseTradeData) rtTradelList.get(i);
                    btd.add(serialNumber, tData);// 将集团返回的数据添加到busiTradeData中
                }
            }
        }

    }

}
