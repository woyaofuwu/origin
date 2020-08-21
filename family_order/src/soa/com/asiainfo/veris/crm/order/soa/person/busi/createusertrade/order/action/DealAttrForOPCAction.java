
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

/**
 * xiekl 物联网修改 如果是物联网业务 有 OPC值 处理USIM卡的OPC值,拼ATTR表 处理USIM卡的OPC值
 * 
 * @author sunxin
 */
public class DealAttrForOPCAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {

        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        // 取出与sim对应的instid
        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        String relaInstId = "";
        // 防止2次开户没有台账 故返回，防止有错 sunxin
        if (resTradeDatas.isEmpty())
        {
            return;
        }
        for (ResTradeData resTradeData : resTradeDatas)
        {
            if ("1".equals(resTradeData.getResTypeCode()))
                relaInstId = resTradeData.getInstId();
        }
        if (!createPersonUserRD.getOPC().equals(""))
        {
            AttrTradeData AttrTradeData = new AttrTradeData();

            AttrTradeData.setUserId(createPersonUserRD.getUca().getUserId());
            AttrTradeData.setInstType("R");
            AttrTradeData.setRelaInstId(relaInstId);
            AttrTradeData.setInstId(SeqMgr.getInstId());
            AttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            AttrTradeData.setAttrCode("OPC_VALUE");
            AttrTradeData.setAttrValue(createPersonUserRD.getOPC());
            AttrTradeData.setStartDate(SysDateMgr.getSysTime());
            AttrTradeData.setEndDate(SysDateMgr.getTheLastTime());
            btd.add(btd.getRD().getUca().getSerialNumber(), AttrTradeData);
        }

    }

}
