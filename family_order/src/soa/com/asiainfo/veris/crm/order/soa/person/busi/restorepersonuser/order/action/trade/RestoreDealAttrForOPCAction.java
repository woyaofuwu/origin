
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.requestdata.RestoreUserReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreDealAttrForOPCAction.java
 * @Description: 如果是物联网业务 有 OPC值 处理USIM卡的OPC值,拼ATTR表 处理USIM卡的OPC值
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-8-9 下午3:59:57
 */
public class RestoreDealAttrForOPCAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        RestoreUserReqData restoreUserReqData = (RestoreUserReqData) btd.getRD();
        // 取出与sim对应的instid
        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        String relaInstId = "";
        if (resTradeDatas == null || resTradeDatas.isEmpty())
        {
            return;
        }
        for (ResTradeData resTradeData : resTradeDatas)
        {
            if (StringUtils.equals("1", resTradeData.getResTypeCode()))
            {
                relaInstId = resTradeData.getInstId();
                break;
            }
        }

        String opc = restoreUserReqData.getOpcValue();
        if (StringUtils.isNotEmpty(opc))
        {
            AttrTradeData AttrTradeData = new AttrTradeData();
            AttrTradeData.setUserId(restoreUserReqData.getUca().getUserId());
            AttrTradeData.setInstType("R");
            AttrTradeData.setRelaInstId(relaInstId);
            AttrTradeData.setInstId(SeqMgr.getInstId());
            AttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            AttrTradeData.setAttrCode("OPC_VALUE");
            AttrTradeData.setAttrValue(opc);
            AttrTradeData.setStartDate(restoreUserReqData.getAcceptTime());
            AttrTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            btd.add(btd.getRD().getUca().getSerialNumber(), AttrTradeData);
        }
    }
}
