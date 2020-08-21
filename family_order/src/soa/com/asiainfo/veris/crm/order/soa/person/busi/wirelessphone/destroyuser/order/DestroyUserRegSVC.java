
package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.destroyuser.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyUserRegSVC.java
 * @Description: 商务电话销户登记服务
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-7-28 下午7:33:14 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-28 yxd v1.0.0 修改原因
 */
public class DestroyUserRegSVC extends OrderService
{
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "9723");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "9723");
    }

    /**
     * @Function: otherTradeDeal()
     * @Description: 处理157号码销户
     * @param: 9723：商务电话固话销户 9722：157号码销户
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-28 下午7:45:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-28 yxd v1.0.0 修改原因
     */
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        IDataset relaUUInfos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(btd.getRD().getUca().getUserId(), "T2", null);
        String serialNumberA = ""; // 157号码
        if (DataSetUtils.isNotBlank(relaUUInfos))
        {
            serialNumberA = relaUUInfos.first().getString("SERIAL_NUMBER_A");
        }
        // 调用手机销户登记流程
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumberA);
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        data.put("ORDER_TYPE_CODE", "9723");
        data.put("TRADE_TYPE_CODE", "9722");
        CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", data);
    }
}
