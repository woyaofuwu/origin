
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 根据账户类型，设置开户信用度
 * 
 * @author sunxin
 */
public class DealCreditAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        IDataset commparaInfos = CommparaInfoQry.getCommparaAllCol("CSM", "889", btd.getRD().getUca().getAccount().getPayModeCode(), btd.getRD().getUca().getUserEparchyCode());
        if (IDataUtil.isEmpty(commparaInfos))
        {
            return;
        }
        String score = commparaInfos.getData(0).getString("PARA_CODE1");

        UcaData uca = btd.getRD().getUca();
        CreditTradeData CreditTradeData = new CreditTradeData();
        CreditTradeData.setUserId(uca.getUserId());
        CreditTradeData.setCreditValue(score);
        CreditTradeData.setCreditMode("addCredit");// 新系统修改
        CreditTradeData.setCreditGiftMonths("1");

        CreditTradeData.setInstId(SeqMgr.getInstId());
        CreditTradeData.setStartDate(SysDateMgr.getSysTime());
        CreditTradeData.setEndDate(SysDateMgr.getTheLastTime());
        CreditTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(btd.getRD().getUca().getSerialNumber(), CreditTradeData);

    }

}
