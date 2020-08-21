
package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.changeproduct.order.action;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: AddElementNameAction.java
 * @Description: 为程控服务或优惠添加元素名称
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-7-24 下午8:51:03 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-24 yxd v1.0.0 修改原因
 */
public class AddElementNameAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List svcList = btd.get(TradeTableEnum.TRADE_SVC.getValue());
        if (CollectionUtils.isNotEmpty(svcList))
        {
            for (Object obj : svcList)
            {
                SvcTradeData svcTD = (SvcTradeData) obj;
                // RsrvStr5-获取服务名称
                svcTD.setRsrvStr5(ElemInfoQry.getElemNameByElemTypeCodeId(BofConst.ELEMENT_TYPE_CODE_SVC, svcTD.getElementId()));
            }
        }
        List discntList = btd.get(TradeTableEnum.TRADE_DISCNT.getValue());
        if (CollectionUtils.isNotEmpty(discntList))
        {
            for (Object obj : discntList)
            {
                DiscntTradeData discntTD = (DiscntTradeData) obj;
                // RsrvStr5-获取优惠名称
                discntTD.setRsrvStr5(ElemInfoQry.getElemNameByElemTypeCodeId(BofConst.ELEMENT_TYPE_CODE_DISCNT, discntTD.getElementId()));
            }
        }
    }
}
