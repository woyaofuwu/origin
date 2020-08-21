
package com.asiainfo.veris.crm.order.soa.person.busi.np.destroynp.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.np.destroynp.order.requestdata.DestroyNpReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyNpTrade.java
 * @Description:1.系统GTM触发定时任务:每天23点扫描用户资料主表TF_F_USER,从中获取5天前销户（申请/欠费）的携转标志是1:已携入或者6:携回用户。 
 *                                                                                        2.根据这些用户的信息，判断用户是否存在未完工的复机工单，如果存在
 *                                                                                        ，不做任何处理；否则生成这些用户的携入方注销业务的预约工单。
 *                                                                                        （台账表TF_B_TRADE中EXEC_TIME=2050/
 *                                                                                        12/31;和携转用户台账子表TF_B_TRADE_NP
 *                                                                                        的记录CREATE_TIME
 *                                                                                        =sysdate,BOOK_SEND_TIME
 *                                                                                        =sysdate,STATE=000）
 *                                                                                        需要在td_s_tradectrl表里配置 备份数据
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014年7月30日 下午3:23:13 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014年7月30日 lijm3 v1.0.0 修改原因
 */
public class DestroyNpTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        DestroyNpReqData reqData = (DestroyNpReqData) btd.getRD();
        String userTagtSet = reqData.getUca().getUser().getUserTagSet();
        String newUserTagSet = "";
        if (StringUtils.isNotBlank(userTagtSet))
        {
            btd.getMainTradeData().setExecTime(SysDateMgr.END_DATE_FOREVER);
            btd.getMainTradeData().setRemark("携入用户注销登记");
            btd.getMainTradeData().setOlcomTag("1");
            createUserTrade(btd);
            createUserNpTrade(btd);
            createUserResTrade(btd);
        }

    }

    public void createUserNpTrade(BusiTradeData btd) throws Exception
    {
        DestroyNpReqData reqData = (DestroyNpReqData) btd.getRD();
        String userId = reqData.getUca().getUserId();
        IDataset ids = TradeNpQry.getUserNpByUserId(userId);
        if (IDataUtil.isNotEmpty(ids))
        {
            NpTradeData nptd = new NpTradeData();
            nptd.setActorCredType("");
            nptd.setActorCustName("");
            nptd.setActorPsptId("");
            nptd.setANpCardType(ids.getData(0).getString("A_NP_CARD_TYPE"));
            nptd.setBNpCardType(ids.getData(0).getString("B_NP_CARD_TYPE"));
            nptd.setBookSendTime(reqData.getAcceptTime());
            nptd.setBrcId("");
            nptd.setCancelTag("0");
            nptd.setCreateTime(reqData.getAcceptTime());
            nptd.setCredType(reqData.getUca().getCustomer().getPsptTypeCode());
            nptd.setCustName(reqData.getUca().getCustomer().getCustName());
            nptd.setErrorMessage("");
            nptd.setFlowId("");
            nptd.setHomeNetid(ids.getData(0).getString("HOME_NETID"));
            nptd.setMd5("");
            nptd.setMessageId("");
            nptd.setModifyTag("2");
            nptd.setMsgCmdCode("");
            nptd.setNpDestroyTime(ids.getData(0).getString("NP_DESTROY_TIME"));
            nptd.setNpServiceType(ids.getData(0).getString("NP_SERVICE_TYPE"));
            nptd.setNpStartDate(ids.getData(0).getString("APPLY_DATE"));
            nptd.setNpTag(ids.getData(0).getString("NP_TAG"));
            nptd.setPhone("");
            nptd.setPortInDate(ids.getData(0).getString("PORT_IN_DATE"));
            nptd.setPortInNetid(ids.getData(0).getString("PORT_IN_NETID"));
            nptd.setPortOutDate(ids.getData(0).getString("PORT_OUT_DATE"));
            nptd.setPortOutNetid(ids.getData(0).getString("PORT_OUT_NETID"));
            nptd.setPsptId(reqData.getUca().getCustomer().getPsptId());
            nptd.setRemark("GTM扫描生成");
            nptd.setResultCode("645");
            nptd.setResultMessage("用户主动退网");
            nptd.setSendTimes("0");
            nptd.setRsrvStr1("");
            nptd.setRsrvStr2("");
            nptd.setRsrvStr3("");
            nptd.setRsrvStr4("");
            nptd.setRsrvStr5("");
            nptd.setTradeTypeCode("49");
            nptd.setState("000");
            nptd.setSerialNumber(reqData.getUca().getSerialNumber());
            nptd.setUserId(reqData.getUca().getUserId());
            //新增受理员工信息 add by dengyi5
            nptd.setUpdateDepartId(CSBizBean.getVisit().getDepartId());
            nptd.setUpdateStaffId(CSBizBean.getVisit().getStaffId());
            nptd.setEparchyCode(CSBizBean.getTradeEparchyCode());
            btd.add(reqData.getUca().getSerialNumber(), nptd);
        }
    }

    public void createUserResTrade(BusiTradeData btd) throws Exception
    {
        DestroyNpReqData reqData = (DestroyNpReqData) btd.getRD();
        IDataset ids = TradeHistoryInfoQry.getDestroyMaxTradeIdByUserId(reqData.getUca().getUserId());
        if (IDataUtil.isNotEmpty(ids))
        {
            String tradeId = ids.getData(0).getString("TRADE_ID");
            IDataset reses = TradeResInfoQry.queryAllTradeResByTradeId(tradeId);
            if (IDataUtil.isNotEmpty(reses))
            {
                for (int i = 0, len = reses.size(); i < len; i++)
                {
                    if ("1".equals(reses.getData(i).getString("RES_TYPE_CODE")) || "0".equals(reses.getData(i).getString("RES_TYPE_CODE")))
                    {
                        ResTradeData rtd = new ResTradeData(reses.getData(i));
                        btd.add(reqData.getUca().getSerialNumber(), rtd);
                    }
                }
            }
        }
    }

    public void createUserTrade(BusiTradeData btd) throws Exception
    {
        DestroyNpReqData reqData = (DestroyNpReqData) btd.getRD();

        String userTagtSet = reqData.getUca().getUser().getUserTagSet();
        String newUserTagSet = "";
        if (StringUtils.isNotBlank(userTagtSet))
        {
            if ("1".equals(userTagtSet.substring(0, 1)))
            {
                newUserTagSet = "2" + userTagtSet.substring(1);
            }
            else if ("6".equals(userTagtSet.substring(0, 1)))
            {
                newUserTagSet = "7" + userTagtSet.substring(1);
            }
        }
        UserTradeData utd = reqData.getUca().getUser().clone();
        utd.setUserTagSet(newUserTagSet);
        utd.setModifyTag("2");
        btd.add(reqData.getUca().getSerialNumber(), utd);

    }

}
