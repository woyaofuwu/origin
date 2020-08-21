
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changeacctdiscnt.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctDiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changeacctdiscnt.ChangeAcctDiscntSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changeacctdiscnt.order.requestdata.ChangeAcctDisnctReqData;

public class changeAcctDisnctTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createTradeAcctDiscntData(btd);

        String nextMonthFirstDate = SysDateMgr.getFirstDayOfNextMonth4WEB();
        btd.getMainTradeData().setSubscribeType("300");
        btd.getMainTradeData().setPfType("300");
        btd.getMainTradeData().setOlcomTag("0");
        // btd.getMainTradeData().setExecTime(nextMonthFirstDate);

    }

    public void createTradeAcctDiscntData(BusiTradeData btd) throws Exception
    {
        ChangeAcctDisnctReqData changAccdRD = (ChangeAcctDisnctReqData) btd.getRD();
        String newDiscntCode = changAccdRD.getAcctDisnctCode();
        String userId = changAccdRD.getUca().getUserId();
        AcctDiscntTradeData accDisTrade = new AcctDiscntTradeData();
        if (newDiscntCode != null && !newDiscntCode.equals("-1"))
        {
            accDisTrade.setAcctId(changAccdRD.getUca().getAcctId());
            accDisTrade.setDiscntCode(newDiscntCode);
            accDisTrade.setStartDate(SysDateMgr.getFirstDayOfNextMonth4WEB());
            accDisTrade.setPartitionId(changAccdRD.getUca().getAcctId().substring(0, 4));
            accDisTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
            accDisTrade.setInstId(SeqMgr.getInstId());
            accDisTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            // accDisTrade.setUpdateTime(updateTime);
            accDisTrade.setUpdateStaffId(getVisit().getStaffId());
            accDisTrade.setUpdateDepartId(getVisit().getDepartId());
            accDisTrade.setRemark(btd.getRD().getRemark());
            accDisTrade.setRsrNum1("0");
            accDisTrade.setRsrvNum2("0");
            accDisTrade.setRsrvNum3("0");
            accDisTrade.setRsrvNum4("0");
            accDisTrade.setRsrvNum5("0");
            accDisTrade.setRsrvStr1("");
            accDisTrade.setRsrvStr2("");
            accDisTrade.setRsrvStr3("");
            accDisTrade.setRsrvStr4("");
            accDisTrade.setRsrvStr5("");
            accDisTrade.setRsrvDate1("");
            accDisTrade.setRsrvDate2("");
            accDisTrade.setRsrvDate3("");
            accDisTrade.setRsrvTag1("");
            accDisTrade.setRsrvTag2("");
            accDisTrade.setRsrvTag3("");
            btd.add(btd.getRD().getUca().getSerialNumber(), accDisTrade);
        }

        String oldCode = changAccdRD.getOldacctDisnctCode();
        if (oldCode != null && !oldCode.equals(""))
        {
            ChangeAcctDiscntSVC sv = new ChangeAcctDiscntSVC();
            IData input = new DataMap();
            input.put("ACCT_ID", changAccdRD.getUca().getAcctId());
            IDataset set = sv.getOldAcctDisnctInfo(input);
            if (IDataUtil.isNotEmpty(set))
            {
                IData data = set.getData(0);
                AcctDiscntTradeData oldInfos = new AcctDiscntTradeData(data);
                oldInfos.setModifyTag(BofConst.MODIFY_TAG_DEL);
                oldInfos.setEndDate(AcctDayDateUtil.getLastDayThisAcct(userId));
                btd.add(btd.getRD().getUca().getSerialNumber(), oldInfos);
            }

        }

    }

}
