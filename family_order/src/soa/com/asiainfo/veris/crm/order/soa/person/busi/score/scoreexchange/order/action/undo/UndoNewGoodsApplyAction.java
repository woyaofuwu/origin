
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

public class UndoNewGoodsApplyAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId); 
        param.put("USER_ID", userId); 
        IDataset InvalidGoods = Dao.qryByCode("TL_B_USER_SCORE_GOODS", "SEL_USER_SCORE_GOODS_BY_TRADEID", param);
        if(InvalidGoods!=null &&InvalidGoods.size()>0){
        	param.put("STATE", "3"); 
            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId()); 
            param.put("REMARK", "已返销"); 
        	Dao.executeUpdateByCodeCode("TL_B_USER_SCORE_GOODS", "UPD_USER_SCORE_GOODS_STATE_UNDO", param);
        } 
    }

}
