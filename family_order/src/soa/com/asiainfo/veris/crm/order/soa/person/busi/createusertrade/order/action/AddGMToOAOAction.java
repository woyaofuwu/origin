
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

import java.util.List;

/**
 * REQ202004240028_关于国漫作为基础服务的开发需求
 * 
 * @author wuhao5
 */
public class AddGMToOAOAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        String inmodecode = btd.getMainTradeData().getInModeCode();
        if ("1".equals(inmodecode)) {
            // 在线渠道过来的如果没有一体机的标识,就不处理
            if(!"1".equals(btd.getRD().getPageRequestData().getString("ROAM_TAG",""))){
                return;
            }
        }
        List<SvcTradeData> svcTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        UcaData uca = btd.getRD().getUca();
        if(svcTradeData != null && svcTradeData.size() > 0)
        {
            for(SvcTradeData svc : svcTradeData)
            {
                // 如果用户已经选择了国内或国际漫游 则不处理
                if(BofConst.MODIFY_TAG_ADD.equals(svc.getModifyTag()))
                {
                    if ("14".equals(svc.getElementId()) || "18".equals(svc.getElementId())
                            || "15".equals(svc.getElementId()) || "19".equals(svc.getElementId())) {
                        return;
                    }
                }
            }
        }
        // 添加国际长途服务
        SvcTradeData newSvcTrade = new SvcTradeData();
        newSvcTrade.setUserId(uca.getUserId());
        newSvcTrade.setUserIdA("-1");
        newSvcTrade.setElementId("15");
        newSvcTrade.setProductId("-1");
        newSvcTrade.setPackageId("-1");
        newSvcTrade.setMainTag("0");
        newSvcTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newSvcTrade.setStartDate(SysDateMgr.getSysDate());
        newSvcTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
        newSvcTrade.setInstId(SeqMgr.getInstId());
        newSvcTrade.setRemark("接口开户默认开通国际长途");
        btd.add(btd.getRD().getUca().getSerialNumber(), newSvcTrade);
        // 添加国际漫游服务
        newSvcTrade.setElementId("19");
        newSvcTrade.setInstId(SeqMgr.getInstId());
        newSvcTrade.setRemark("接口开户默认开通国际漫游");
        btd.add(btd.getRD().getUca().getSerialNumber(), newSvcTrade);
    }
}
