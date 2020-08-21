package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.undo;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

/**
 * REQ201907010036产品、优惠预约办理在生效时触发提醒
 * 取消预约产品变更    取消AEE到期提醒
 *
 * @author liangdg3
 */
public class UndoCancelBookProduct implements ITradeFinishAction {
    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        boolean isCancelProductBook = false;
        //判断取消产品预约变更
        IDataset productTrades = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
        if (IDataUtil.isNotEmpty(productTrades)) {
            for (Iterator pit = productTrades.iterator(); pit.hasNext(); ) {
                IData productTrade = (IData) pit.next();
                String startDate = productTrade.getString("START_DATE");
                isCancelProductBook = SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)
                        .compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD)) > 0;
                if (isCancelProductBook) {
                    IData inparams = new DataMap();
                    inparams.put("TRADE_ID", tradeId);
                    inparams.put("DEAL_STATE", "0");
                    inparams.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_PRODUCT_BOOK);
                    IDataset expireDeals = BofQuery.queryExpireDealByTradeId(inparams);
                    if (IDataUtil.isNotEmpty(expireDeals)) {
                        for (Iterator eit = expireDeals.iterator(); eit.hasNext(); ) {
                            IData expireDeal = (IData) eit.next();
                            Dao.delete("TF_F_EXPIRE_DEAL", expireDeal);
                            expireDeal.put("DEAL_STATE", "2");
                            Dao.insert("TF_FH_EXPIRE_DEAL", expireDeal);
                        }
                    }
                }
            }
        }
    }
}
