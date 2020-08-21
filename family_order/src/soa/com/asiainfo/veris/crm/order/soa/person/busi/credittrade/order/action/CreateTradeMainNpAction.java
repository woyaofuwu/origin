
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class CreateTradeMainNpAction implements ITradeAction
{

    public void createNpTradeData(BusiTradeData btd) throws Exception
    {

        String userTagset = btd.getRD().getUca().getUser().getUserTagSet();
        String userId = btd.getRD().getUca().getUserId();
        String strUserNpTag = "";
        if (StringUtils.isBlank(userTagset))
        {
            strUserNpTag = "0";
        }
        else
        {
            strUserNpTag = userTagset.substring(0, 1);
        }

        String npServiceType = "";
        String portOutNetid = "";
        String portInNetid = "";
        String homeNetid = "";
        if ("1".equals(strUserNpTag) || "4".equals(strUserNpTag) || "6".equals(strUserNpTag) || "8".equals(strUserNpTag))
        {

            IDataset ids = TradeNpQry.getUserNpByUserId(userId);
            if (IDataUtil.isEmpty(ids))
            {
                CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "115001 获取USER_ID=[" + userId + "]的携号资料失败！");
            }
            IData data = ids.getData(0);
            npServiceType = data.getString("NP_SERVICE_TYPE");
            portOutNetid = data.getString("PORT_OUT_NETID");
            portInNetid = data.getString("PORT_IN_NETID");
            homeNetid = data.getString("HOME_NETID");
        }

        String tradeTypeCode = btd.getMainTradeData().getTradeTypeCode();
        String serialNumber = btd.getMainTradeData().getSerialNumber();

        if ("44".equals(tradeTypeCode) || "48".equals(tradeTypeCode))
        {
            IDataset ids = TradeNpQry.getUserNpBySn(serialNumber);
            npServiceType = "MOBILE";
            if (IDataUtil.isNotEmpty(ids))
            {
                IData data = ids.getData(0);
                npServiceType = data.getString("NP_SERVICE_TYPE");
                portOutNetid = data.getString("PORT_OUT_NETID");
                portInNetid = data.getString("PORT_IN_NETID");
                homeNetid = data.getString("HOME_NETID");
            }
        }

        String flowId = "";
        if ("46".equals(tradeTypeCode))
        {
            IDataset ids = TradeNpQry.getTradeNpByUserIdTradeTypeCodeCancelTag(userId, "44", "0");
            if (IDataUtil.isNotEmpty(ids) && ids.size() != 1)
            {
                CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "115002 获取用户USER_ID=[" + userId + "]的携出方欠费停机工单异常！");
            }
            flowId = ids.getData(0).getString("FLOW_ID");
        }

        String resultCode = "";
        String resultMessage = "";
        if ("47".equals(tradeTypeCode))
        {
            resultCode = "646";
            resultMessage = "用户欠费（携入方）停机导致销号";
        }
        else if ("48".equals(tradeTypeCode))
        {
            resultCode = "647";
            resultMessage = "用户欠费（携出方）停机导致销号";
        }
        else if ("49".equals(tradeTypeCode))
        {
            resultCode = "645";
            resultMessage = "用户主动退网";
        }

        String state = "000";
        if ("43".equals(tradeTypeCode) || "45".equals(tradeTypeCode))
        {
            state = "100";
        }

        String remark = "";
        if ("44".equals(tradeTypeCode))
        {
            IData fee = AcctCall.getOweFeeByUserId(userId);

            int acctBalance = fee.getInt("ACCT_BALANCE", 0) / 100;
            remark = "因欠费需停机," + acctBalance + "元";

        }

        NpTradeData nptd = new NpTradeData();
        nptd.setUserId(btd.getMainTradeData().getUserId());
        nptd.setTradeTypeCode(tradeTypeCode);
        nptd.setNpServiceType(npServiceType);
        nptd.setSerialNumber(serialNumber);
        nptd.setFlowId(flowId);
        nptd.setMessageId("");
        nptd.setBrcId("");
        nptd.setMsgCmdCode("");
        nptd.setCreateTime(btd.getRD().getAcceptTime());
        nptd.setBookSendTime(btd.getRD().getAcceptTime());
        nptd.setCancelTag("0");

        nptd.setMd5("");
        nptd.setPortOutNetid(portOutNetid);
        nptd.setPortInNetid(portInNetid);
        nptd.setHomeNetid(homeNetid);
        nptd.setBNpCardType("20000000");
        nptd.setANpCardType("");

        nptd.setCustName(btd.getMainTradeData().getCustName());
        nptd.setCredType("");
        nptd.setPsptId(btd.getRD().getUca().getCustPerson().getPsptId());
        nptd.setPhone(btd.getRD().getUca().getCustPerson().getPhone());
        nptd.setActorCustName("");
        nptd.setActorCredType("");
        nptd.setActorPsptId("");

        nptd.setNpStartDate("");
        nptd.setSendTimes("0");
        nptd.setState(state);
        nptd.setResultCode(resultCode);
        nptd.setResultMessage(resultMessage);
        nptd.setErrorMessage("");

        nptd.setRemark(remark);
        nptd.setRsrvStr1("");
        nptd.setRsrvStr2("");
        nptd.setRsrvStr3("");
        nptd.setRsrvStr4("");
        nptd.setRsrvStr5("");

        //新增受理员工信息 add by dengyi5
        nptd.setUpdateDepartId(CSBizBean.getVisit().getDepartId());
        nptd.setUpdateStaffId(CSBizBean.getVisit().getStaffId());
        nptd.setEparchyCode(CSBizBean.getTradeEparchyCode());
        btd.add(serialNumber, nptd);
    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        createNpTradeData(btd);
    }

}
