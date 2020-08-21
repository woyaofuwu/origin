
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 用户密码变更：插入用户密码变更历史信息
 * 
 * @author liutt
 */
public class InsertPasswdChgHisFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        IData pwdChgHisParam = new DataMap();
        pwdChgHisParam.put("PARTITION_ID", Long.valueOf(userId) % 10000);
        pwdChgHisParam.put("USER_ID", userId);
        pwdChgHisParam.put("DEAL_TYPE", mainTrade.getString("RSRV_STR2", ""));
        pwdChgHisParam.put("DEAL_TIME", SysDateMgr.getSysTime());
        pwdChgHisParam.put("TRADE_ID", mainTrade.getString("TRADE_ID", ""));
        pwdChgHisParam.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE", ""));
        pwdChgHisParam.put("TRADE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID", ""));
        pwdChgHisParam.put("TRADE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID", ""));
        Dao.insert("TF_F_USER_PASSWD_CHGHIS", pwdChgHisParam);//
    }

}
