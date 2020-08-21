
package com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelout.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelout.order.requestdata.NpApplyCancelOutReqData;

public class NpApplyCancelOutTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        NpApplyCancelOutReqData reqData = (NpApplyCancelOutReqData) btd.getRD();

        if (StringUtils.isBlank(reqData.getCancelTag()))
        {
            createNpUserTradeData(btd);
        }

        if (StringUtils.isBlank(reqData.getCancelType()))
        {
            //modifyNpApplyTradeMain(btd);
            modifyNpApplyTradeNp(btd);
        }

    }
    

    private void createNpUserTradeData(BusiTradeData btd) throws Exception
    {
        NpApplyCancelOutReqData reqData = (NpApplyCancelOutReqData) btd.getRD();

        IDataset ids = TradeNpQry.getTradeNpBySelTradenpByIdd(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(ids) || ids.size() != 1)
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_115001, reqData.getUca().getUserId());
        }

        NpTradeData nptd = new NpTradeData();
        nptd.setBookSendTime(reqData.getAcceptTime());
        nptd.setUserId(ids.getData(0).getString("USER_ID"));
        nptd.setTradeTypeCode("1504");
        nptd.setNpServiceType(ids.getData(0).getString("NP_SERVICE_TYPE"));
        nptd.setSerialNumber(ids.getData(0).getString("SERIAL_NUMBER"));
        nptd.setFlowId(ids.getData(0).getString("FLOW_ID"));
        nptd.setMessageId(ids.getData(0).getString("MESSAGE_ID"));
        nptd.setBrcId("");
        nptd.setMsgCmdCode("");
        nptd.setMd5("");
        nptd.setPortOutNetid(ids.getData(0).getString("PORT_OUT_NETID"));
        nptd.setPortInNetid(ids.getData(0).getString("PORT_IN_NETID"));
        nptd.setHomeNetid(ids.getData(0).getString("HOME_NETID"));
        nptd.setBNpCardType(ids.getData(0).getString("B_NP_CARD_TYPE"));
        nptd.setANpCardType(ids.getData(0).getString("A_NP_CARD_TYPE"));
        nptd.setCustName(ids.getData(0).getString("CUST_NAME"));
        nptd.setCredType(ids.getData(0).getString("CRED_TYPE"));
        nptd.setPsptId(ids.getData(0).getString("PSPT_ID"));
        nptd.setPhone(ids.getData(0).getString("PHONE"));
        nptd.setActorCustName(ids.getData(0).getString("ACTOR_CUST_NAME"));
        nptd.setActorCredType(ids.getData(0).getString("ACTOR_CRED_TYPE"));
        nptd.setActorPsptId(ids.getData(0).getString("ACTOR_PSPT_ID"));
        nptd.setNpStartDate("");
        nptd.setCreateTime(reqData.getAcceptTime());
        nptd.setSendTimes("");
        nptd.setResultCode("");
        nptd.setResultMessage("");
        nptd.setErrorMessage("");
        nptd.setCancelTag("0");
        nptd.setState("000");
        nptd.setRemark("");
        nptd.setRsrvStr1("");
        nptd.setRsrvStr2("");
        nptd.setRsrvStr3("");
        nptd.setRsrvStr4("");
        nptd.setRsrvStr5("");
        //新增受理员工信息 add by dengyi5
        nptd.setUpdateDepartId(CSBizBean.getVisit().getDepartId());
        nptd.setUpdateStaffId(CSBizBean.getVisit().getStaffId());
        nptd.setEparchyCode(CSBizBean.getTradeEparchyCode());
        btd.add(reqData.getUca().getUser().getSerialNumber(), nptd);

    }

    public void modifyNpApplyTradeMain(BusiTradeData btd) throws Exception
    {
        NpApplyCancelOutReqData reqData = (NpApplyCancelOutReqData) btd.getRD();
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", reqData.getCancelTradeId());
        inparam.put("CANCEL_TAG", "1");
        Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_CANCELTAG_BY_TID_NP", inparam);

        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INSERT_FROM_TRADE", inparam);

        Dao.executeUpdateByCodeCode("TF_B_TRADE", "DEL_BY_TRADEID", inparam);
    }

    public void modifyNpApplyTradeNp(BusiTradeData btd) throws Exception
    {
        NpApplyCancelOutReqData reqData = (NpApplyCancelOutReqData) btd.getRD();
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", reqData.getCancelTradeId());
        inparam.put("CANCEL_TAG", "1");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_NP_CANCEL_TAG", inparam);
    }
}
