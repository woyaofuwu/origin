
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;

public class AddUserProsecutionRegAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTrade = btd.getMainTradeData();
        IData param = new DataMap();
        param.put("PROSECUTION_ID", btd.getTradeId());
        param.put("PROSECUTION_TYPE", mainTrade.getRsrvStr5());
        param.put("PROSECUTION_WAY", mainTrade.getRsrvStr3());
        param.put("SERIAL_NUMBER", mainTrade.getRsrvStr2());
        param.put("PROSECUTEE_NUMBER", mainTrade.getRsrvStr1());
        param.put("PROSECUTION_INFO", mainTrade.getRsrvStr8());
        param.put("UPFILE_TAG", "0");
        param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("REMARK", "办理垃圾短信举报业务成功");

        Dao.insert("TF_F_USER_PROSECUTION", param);
    }

}
