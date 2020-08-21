
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

/**
 * 用户新卡类型为Z的，需要记录SIM_TYPE_CODE 如果是物联网补换卡，记录OPC_VALUE
 * 
 * @author
 */
public class OPCInfoAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        SimCardReqData simCardRD = (SimCardReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        String instId = SeqMgr.getInstId();
        /*
         * List<ResTradeData> resTrades = btd.getTradeDatas(TradeTableEnum.TRADE_RES); boolean is4G = false;
         * for(ResTradeData resInfo : resTrades ){ if(BofConst.MODIFY_TAG_ADD.equals(resInfo.getModifyTag())){
         * if("1".equals(resInfo.getRsrvTag3())){//4G卡 is4G = true; break; } } }
         */
        if (!"".equals(simCardRD.getNewSimCardInfo().getOpc()))
        {
            AttrTradeData attrTD = new AttrTradeData();
            attrTD.setUserId(simCardRD.getUca().getUserId());
            attrTD.setInstType("R");
            attrTD.setInstId(instId);

            List<ResTradeData> userResInfos = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
            for (ResTradeData res : userResInfos)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(res.getModifyTag()))
                {
                    attrTD.setRelaInstId(res.getInstId());
                    attrTD.setAttrCode("OPC_VALUE");
                    attrTD.setAttrValue(simCardRD.getNewSimCardInfo().getOpc());
                    attrTD.setStartDate(btd.getRD().getAcceptTime());
                    attrTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
                    attrTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    btd.add(simCardRD.getUca().getSerialNumber(), attrTD);
                    break;
                }
            }
        }
        if (!"".equals(simCardRD.getOldSimCardInfo().getOpc()))
        {
            List<AttrTradeData> attrs = uca.getUserAttrsByAttrCode("OPC_VALUE");
            if (attrs != null && attrs.size() > 0)
            {
                for (AttrTradeData attr : attrs)
                {
                    if (BofConst.MODIFY_TAG_USER.equals(attr.getModifyTag()) && "R".equals(attr.getInstType()))
                    {
                        AttrTradeData attrTD = attr.clone();
                        attrTD.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
                        attrTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        btd.add(simCardRD.getUca().getSerialNumber(), attrTD);
                        break;
                    }
                }

            }
        }
    }

}
