
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.acctday;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.AcctDayData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: CreateUserAcctDayAction.java
 * @Description: 针对用户台账【TF_B_TRADE_USER】为新增的用户,新增用户账期台账【TF_B_TRADE_USER_ACCTDAY】
 * @version: v1.0.0
 * @author: maoke
 * @date: 下午15:36:22 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-03-14 maoke v1.0.0 修改原因
 */

public class CreateUserAcctDayAction implements ITradeAction
{
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        //销户的时候未处理用户账期，复机的时候也不用处理 
        if (StringUtils.equals("310", tradeTypeCode) || StringUtils.equals("3813", tradeTypeCode))
        {
            return;
        }
        List<UserTradeData> userTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_USER);

        if (userTradeData != null && userTradeData.size() > 0)
        {
            for (int i = 0; i < userTradeData.size(); i++)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(userTradeData.get(i).getModifyTag()))
                {
                    String userId = userTradeData.get(i).getUserId();// 用户USER_ID

                    List<AcctDayData> userAcctDayOpenList = btd.getUserAcctDayOpenList();

                    if (userAcctDayOpenList.isEmpty())
                    {
                        CSAppException.apperr(AcctDayException.CRM_ACCTDAY_24);
                    }

                    String acctDay = userAcctDayOpenList.get(0).getAcctDay();// 新的账期结账日

                    IDataset userOpenAcctDayDataset = this.getUserAcctDayByOpen(userId, acctDay);

                    this.stringTableTradeUserAcctDay(btd, userOpenAcctDayDataset);
                }
            }
        }
    }

    /**
     * 根据USER_ID获取开户用户用户账期数据
     * 
     * @param pd
     * @param userId
     *            用户USER_ID
     * @param acctDay
     *            新的结账日
     * @return
     * @throws Exception
     */
    public IDataset getUserAcctDayByOpen(String userId, String acctDay) throws Exception
    {
        IData params = new DataMap();
        params.clear();

        params.put("ACCT_DAY", acctDay);
        params.put("USER_ID", userId);

        IDataset useracctdays = new DiversifyAcctUtil().getNewAcctDayByOpenUser(params);
        if (useracctdays != null && useracctdays.size() > 0)
        {
            for (int i = 0; i < useracctdays.size(); i++)
            {
                IData useracctday = useracctdays.getData(0);
                useracctday.put("INST_ID", SeqMgr.getInstId());
            }
        }
        return useracctdays;
    }

    /**
     * 设置用户结账日台帐表的数据
     * 
     * @param btd
     * @param newUserAcctDayDataset
     * @throws Exception
     */
    public void stringTableTradeUserAcctDay(BusiTradeData btd, IDataset newUserAcctDayDataset) throws Exception
    {
        for (int i = 0; i < newUserAcctDayDataset.size(); i++)
        {
            UserAcctDayTradeData userAcctDayTradeData = new UserAcctDayTradeData();

            userAcctDayTradeData.setAcctDay(newUserAcctDayDataset.getData(i).getString("ACCT_DAY"));
            userAcctDayTradeData.setChgDate(newUserAcctDayDataset.getData(i).getString("CHG_DATE"));
            userAcctDayTradeData.setChgMode(newUserAcctDayDataset.getData(i).getString("CHG_MODE"));
            userAcctDayTradeData.setChgType(newUserAcctDayDataset.getData(i).getString("CHG_TYPE"));
            userAcctDayTradeData.setEndDate(newUserAcctDayDataset.getData(i).getString("END_DATE"));
            userAcctDayTradeData.setFirstDate(newUserAcctDayDataset.getData(i).getString("FIRST_DATE"));
            userAcctDayTradeData.setInstId(newUserAcctDayDataset.getData(i).getString("INST_ID"));
            userAcctDayTradeData.setModifyTag(newUserAcctDayDataset.getData(i).getString("MODIFY_TAG"));
            userAcctDayTradeData.setRemark(newUserAcctDayDataset.getData(i).getString("REMARK"));
            userAcctDayTradeData.setRsrvDate1(newUserAcctDayDataset.getData(i).getString("RSRV_DATE1"));
            userAcctDayTradeData.setRsrvDate2(newUserAcctDayDataset.getData(i).getString("RSRV_DATE2"));
            userAcctDayTradeData.setRsrvDate3(newUserAcctDayDataset.getData(i).getString("RSRV_DATE3"));
            userAcctDayTradeData.setRsrvNum1(newUserAcctDayDataset.getData(i).getString("RSRV_NUM1"));
            userAcctDayTradeData.setRsrvNum2(newUserAcctDayDataset.getData(i).getString("RSRV_NUM2"));
            userAcctDayTradeData.setRsrvNum3(newUserAcctDayDataset.getData(i).getString("RSRV_NUM3"));
            userAcctDayTradeData.setRsrvNum4(newUserAcctDayDataset.getData(i).getString("RSRV_NUM4"));
            userAcctDayTradeData.setRsrvNum5(newUserAcctDayDataset.getData(i).getString("RSRV_NUM5"));
            userAcctDayTradeData.setRsrvStr1(newUserAcctDayDataset.getData(i).getString("RSRV_STR1"));
            userAcctDayTradeData.setRsrvStr2(newUserAcctDayDataset.getData(i).getString("RSRV_STR2"));
            userAcctDayTradeData.setRsrvStr3(newUserAcctDayDataset.getData(i).getString("RSRV_STR3"));
            userAcctDayTradeData.setRsrvStr4(newUserAcctDayDataset.getData(i).getString("RSRV_STR4"));
            userAcctDayTradeData.setRsrvStr5(newUserAcctDayDataset.getData(i).getString("RSRV_STR5"));
            userAcctDayTradeData.setRsrvTag1(newUserAcctDayDataset.getData(i).getString("RSRV_TAG1"));
            userAcctDayTradeData.setRsrvTag2(newUserAcctDayDataset.getData(i).getString("RSRV_TAG2"));
            userAcctDayTradeData.setRsrvTag3(newUserAcctDayDataset.getData(i).getString("RSRV_TAG3"));
            userAcctDayTradeData.setStartDate(newUserAcctDayDataset.getData(i).getString("START_DATE"));
            userAcctDayTradeData.setUserId(newUserAcctDayDataset.getData(i).getString("USER_ID"));

            btd.add(btd.getRD().getUca().getSerialNumber(), userAcctDayTradeData);
        }
    }
}
