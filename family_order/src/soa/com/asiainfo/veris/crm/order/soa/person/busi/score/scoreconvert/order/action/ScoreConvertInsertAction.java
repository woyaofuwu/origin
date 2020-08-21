
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.requestdata.ScoreConvertRequestData;

public class ScoreConvertInsertAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ScoreConvertRequestData reqData = (ScoreConvertRequestData) btd.getRD();
        IData upmsParam = new DataMap();
        String seq = SeqMgr.getOrdReqId();
        upmsParam.put("ORDER_SEQ", seq);
        upmsParam.put("ORDER_ID", "");// 置空，等待IBOSS返回值
        upmsParam.put("USER_ID", reqData.getUca().getUser().getUserId());
        upmsParam.put("ITEM_ID", reqData.getItemId());
        upmsParam.put("ITEM_NUM", reqData.getItemNum());
        upmsParam.put("DELIV_PROVINCE", reqData.getDelivProvince());
        upmsParam.put("CITY", reqData.getCity());
        upmsParam.put("DISTRICT", reqData.getDistrict());
        upmsParam.put("CUS_ADD", reqData.getCusAdd());
        upmsParam.put("CUS_TEL", reqData.getCusTel());
        upmsParam.put("CUS_ADDCODE", reqData.getCusAddcode());
        upmsParam.put("TIME_REQUEST", reqData.getDelivTimeReq());
        upmsParam.put("RSRV_STR4", seq);
        upmsParam.put("RSRV_STR5", reqData.getTradeId());
        upmsParam.put("RSRV_STR6", reqData.getUca().getSerialNumber());
        upmsParam.put("REG_DATE", reqData.getAcceptTime());
        upmsParam.put("UPDATE_TIME", reqData.getAcceptTime());
        upmsParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        upmsParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        upmsParam.put("REMARK", reqData.getRemark());
        Dao.insert("TF_F_USER_UPMS_ORDER", upmsParam);

        MainTradeData mainTrade = btd.getMainTradeData();
        mainTrade.setRsrvStr4(seq);// 关联UPMS订单表的流水号到主台帐
    }

}
