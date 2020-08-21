
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductExpireAction.java
 * @Description: 产品变更完工后处理到期提醒工单
 * @version: v1.0.0
 * @author: maoke
 * @date: Aug 22, 2014 3:27:21 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Aug 22, 2014 maoke v1.0.0 修改原因
 */
public class ChangeProductExpireAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String execTime = mainTrade.getString("ACCEPT_DATE");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        //返销操作
        String strCancelTag = mainTrade.getString("CANCEL_TAG");
        //订单返销时，将未处理的到期处理记录失效
        if (strCancelTag.equals("2"))
        {
            cancelExpireDeal(tradeId);
            return;
        }

        IDataset productTrade = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
        if (IDataUtil.isNotEmpty(productTrade))
        {
            IData product = productTrade.getData(0);

            if ("1".equals(product.getString("MAIN_TAG", "")))
            {
                execTime = product.getString("START_DATE", "");
            }
        }

        IData param = new DataMap();

        param.put("DEAL_ID", SeqMgr.getTradeId());
        param.put("USER_ID", userId);
        param.put("PARTITION_ID", userId.substring(userId.length() - 4));
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("IN_TIME", SysDateMgr.getSysTime());
        param.put("DEAL_STATE", "0");
        param.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_PRODUCT);
        param.put("EXEC_TIME", execTime);
        param.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
        param.put("TRADE_ID", tradeId);

        Dao.insert("TF_F_EXPIRE_DEAL", param);

        if (IDataUtil.isNotEmpty(productTrade))// 证明有产品变更
        {
            IData paramProduct = new DataMap();

            paramProduct.put("DEAL_ID", SeqMgr.getTradeId());
            paramProduct.put("USER_ID", userId);
            paramProduct.put("PARTITION_ID", userId.substring(userId.length() - 4));
            paramProduct.put("SERIAL_NUMBER", serialNumber);
            paramProduct.put("EPARCHY_CODE", eparchyCode);
            paramProduct.put("IN_TIME", SysDateMgr.getSysTime());
            paramProduct.put("DEAL_STATE", "0");
            paramProduct.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_SYNC_USER);
            paramProduct.put("EXEC_TIME", execTime);
            paramProduct.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
            paramProduct.put("TRADE_ID", tradeId);

            Dao.insert("TF_F_EXPIRE_DEAL", paramProduct);
        }
    }

    /***
     * 订单返销时，将未处理的到期处理记录失效
     * @param tradeId
     * @throws Exception
     */
    public void cancelExpireDeal(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("NEW_DEAL_STATE", "2");
        param.put("REMARK", "TD二代固话预约业务取消");
        param.put("TRADE_ID", tradeId);
        param.put("DEAL_STATE", "0");
        StringBuilder sql = new StringBuilder(1000);

        sql.append(" update TF_F_EXPIRE_DEAL t ");
        sql.append(" set t.DEAL_STATE=:NEW_DEAL_STATE,t.REMARK=:REMARK ");
        sql.append(" where t.TRADE_ID = :TRADE_ID and t.DEAL_STATE=:DEAL_STATE  ");
        Dao.executeUpdate(sql, param);
    }
}
