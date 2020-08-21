
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.acctday;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.AcctDayData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: CreateAccountAcctDayAction.java
 * @Description: 针对用户台账【TF_B_TRADE_ACCOUNT】为新增的用户,新增用户账期台账【TF_B_TRADE_ACCOUNT_ACCTDAY】
 * @version: v1.0.0
 * @author: maoke
 * @date: 下午15:36:22 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-03-14 maoke v1.0.0 修改原因
 */

public class CreateAccountAcctDayAction implements ITradeAction
{
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<AccountTradeData> accountTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_ACCOUNT);

        if (accountTradeData != null && accountTradeData.size() > 0)
        {
            for (int i = 0; i < accountTradeData.size(); i++)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(accountTradeData.get(i).getModifyTag()))
                {
                    String acctId = accountTradeData.get(i).getAcctId();// 用户ACCT_ID

                    List<AcctDayData> accountAcctDayOpenList = btd.getAccountAcctDayOpenList();

                    if (accountAcctDayOpenList.isEmpty())
                    {
                        CSAppException.apperr(AcctDayException.CRM_ACCTDAY_24);
                    }

                    String acctDay = accountAcctDayOpenList.get(0).getAcctDay();// 新的账期结账日

                    IDataset newAccountAcctDayDataset = this.getAccountAcctDayByUserOpen(acctId, acctDay);

                    this.stringTableTradeAccountAcctDay(btd, newAccountAcctDayDataset);
                }
            }
        }
    }

    /**
     * 根据ACCT_ID获取开户用户用户账期数据
     * 
     * @param acctId
     * @param acctDay
     * @return
     * @throws Exception
     */
    public IDataset getAccountAcctDayByUserOpen(String acctId, String acctDay) throws Exception
    {
        IData params = new DataMap();
        params.clear();

        params.put("ACCT_DAY", acctDay);
        params.put("ACCT_ID", acctId);

        return new DiversifyAcctUtil().getNewAccountAcctDayByOpenUser(params);
    }

    /**
     * 设置账户结账日台帐表的数据
     * 
     * @param btd
     * @param newUserAcctDayDataset
     * @param acctId
     * @throws Exception
     */
    public void stringTableTradeAccountAcctDay(BusiTradeData btd, IDataset newAccountAcctDayDataset) throws Exception
    {
        for (int i = 0; i < newAccountAcctDayDataset.size(); i++)
        {
            AccountAcctDayTradeData accountAcctDayTradeData = new AccountAcctDayTradeData();

            accountAcctDayTradeData.setAcctId(newAccountAcctDayDataset.getData(i).getString("ACCT_ID"));
            accountAcctDayTradeData.setAcctDay(newAccountAcctDayDataset.getData(i).getString("ACCT_DAY"));
            accountAcctDayTradeData.setChgDate(newAccountAcctDayDataset.getData(i).getString("CHG_DATE"));
            accountAcctDayTradeData.setChgMode(newAccountAcctDayDataset.getData(i).getString("CHG_MODE"));
            accountAcctDayTradeData.setChgType(newAccountAcctDayDataset.getData(i).getString("CHG_TYPE"));
            accountAcctDayTradeData.setEndDate(newAccountAcctDayDataset.getData(i).getString("END_DATE"));
            accountAcctDayTradeData.setFirstDate(newAccountAcctDayDataset.getData(i).getString("FIRST_DATE"));
            accountAcctDayTradeData.setInstId(newAccountAcctDayDataset.getData(i).getString("INST_ID"));
            accountAcctDayTradeData.setModifyTag(newAccountAcctDayDataset.getData(i).getString("MODIFY_TAG"));
            accountAcctDayTradeData.setRemark(newAccountAcctDayDataset.getData(i).getString("REMARK"));
            accountAcctDayTradeData.setRsrvDate1(newAccountAcctDayDataset.getData(i).getString("RSRV_DATE1"));
            accountAcctDayTradeData.setRsrvDate2(newAccountAcctDayDataset.getData(i).getString("RSRV_DATE2"));
            accountAcctDayTradeData.setRsrvDate3(newAccountAcctDayDataset.getData(i).getString("RSRV_DATE3"));
            accountAcctDayTradeData.setRsrvNum1(newAccountAcctDayDataset.getData(i).getString("RSRV_NUM1"));
            accountAcctDayTradeData.setRsrvNum2(newAccountAcctDayDataset.getData(i).getString("RSRV_NUM2"));
            accountAcctDayTradeData.setRsrvNum3(newAccountAcctDayDataset.getData(i).getString("RSRV_NUM3"));
            accountAcctDayTradeData.setRsrvNum4(newAccountAcctDayDataset.getData(i).getString("RSRV_NUM4"));
            accountAcctDayTradeData.setRsrvNum5(newAccountAcctDayDataset.getData(i).getString("RSRV_NUM5"));
            accountAcctDayTradeData.setRsrvStr1(newAccountAcctDayDataset.getData(i).getString("RSRV_STR1"));
            accountAcctDayTradeData.setRsrvStr2(newAccountAcctDayDataset.getData(i).getString("RSRV_STR2"));
            accountAcctDayTradeData.setRsrvStr3(newAccountAcctDayDataset.getData(i).getString("RSRV_STR3"));
            accountAcctDayTradeData.setRsrvStr4(newAccountAcctDayDataset.getData(i).getString("RSRV_STR4"));
            accountAcctDayTradeData.setRsrvStr5(newAccountAcctDayDataset.getData(i).getString("RSRV_STR5"));
            accountAcctDayTradeData.setRsrvTag1(newAccountAcctDayDataset.getData(i).getString("RSRV_TAG1"));
            accountAcctDayTradeData.setRsrvTag2(newAccountAcctDayDataset.getData(i).getString("RSRV_TAG2"));
            accountAcctDayTradeData.setRsrvTag3(newAccountAcctDayDataset.getData(i).getString("RSRV_TAG3"));
            accountAcctDayTradeData.setStartDate(newAccountAcctDayDataset.getData(i).getString("START_DATE"));

            btd.add(btd.getRD().getUca().getSerialNumber(), accountAcctDayTradeData);
        }
    }
}
