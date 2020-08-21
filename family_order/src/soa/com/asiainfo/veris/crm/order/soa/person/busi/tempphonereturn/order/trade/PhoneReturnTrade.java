
package com.asiainfo.veris.crm.order.soa.person.busi.tempphonereturn.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.tempphonereturn.order.requestdata.PhoneReturnRequestData;

public class PhoneReturnTrade extends BaseTrade implements ITrade
{

    @SuppressWarnings("unchecked")
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        PhoneReturnRequestData reqData = (PhoneReturnRequestData) btd.getRD();
        createTradeRes(btd, reqData);
    }

    private void createTradeRes(BusiTradeData<BaseTradeData> btd, PhoneReturnRequestData reqData) throws Exception
    {
        String simCardNo = reqData.getSimCardNo();
        String seriNum = reqData.getSerialNumber();
        String batchId = reqData.getBatchId();
        String flg = reqData.getFlg();
        String resState = "3";
        String userId = SeqMgr.getUserId();

        btd.getMainTradeData().setUserId(userId);
        if (StringUtils.isNotEmpty(batchId))
        {
            btd.getMainTradeData().setBatchId(batchId);
        }

        ResTradeData newRes = new ResTradeData();
        newRes.setUserId(userId);
        newRes.setUserIdA("-1");
        newRes.setInstId(SeqMgr.getInstId());
        newRes.setStartDate(SysDateMgr.getSysTime());
        String endDate = SysDateMgr.END_DATE_FOREVER;
        newRes.setModifyTag("0");
        if ("1".equals(reqData.getFlg()))
        {
            newRes.setModifyTag("1");
            resState = "6";
            endDate = SysDateMgr.getSysTime();
        }
        newRes.setEndDate(endDate);

        ResTradeData newRes0 = new ResTradeData();
        newRes0 = newRes.clone();
        newRes0.setResTypeCode("0");
        newRes0.setResCode(seriNum);
        btd.add(seriNum, newRes0);

        String oracleSimCard = null;
        String imsi = null;
        String opc = null;
        String emptyCard = null;
        String ki = null;

        if (StringUtils.isEmpty(batchId))
        {
            imsi = reqData.getImis();
            opc = reqData.getOpc();
            emptyCard = reqData.getEmptyCard();
            ki = reqData.getKi();
        }
        else
        {
            IData resData = new DataMap();
            resData = ResCall.qryPhoneEmptyBySn(seriNum, resState);
            if (IDataUtil.isNotEmpty(resData))
            {
                oracleSimCard = resData.getString("SIM_CARD_NO", "");
                imsi = resData.getString("IMSI", "");
                ki = resData.getString("KI", "");
                opc = resData.getString("OPC", "");
                emptyCard = resData.getString("EMPTY_CARD_ID", "");
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号码查询不到对应的SIM卡信息");
            }
            if (oracleSimCard != null && imsi != null && opc != null && emptyCard != null)
            {
                if (!oracleSimCard.equals(simCardNo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "输入的sim卡和数据库查询到的sim卡不一致");
                }
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号码查询不到对应的SIM卡信息");
            }
        }

        String xChoiceTag = "0";
        if ("1".equals(flg))
        {
            xChoiceTag = "1";
        }
        ResCall.tempPhoneOpenDestory(seriNum, xChoiceTag);

        ResTradeData newRes6 = new ResTradeData();
        newRes6 = newRes.clone();
        newRes6.setResTypeCode("6");
        newRes6.setResCode(emptyCard);
        btd.add(seriNum, newRes6);

        ResTradeData newRes1 = new ResTradeData();
        newRes1 = newRes.clone();
        newRes1.setResTypeCode("1");
        newRes1.setResCode(simCardNo);
        newRes1.setKi(ki);
        newRes1.setImsi(imsi);
        newRes1.setRsrvStr1(opc);
        newRes1.setRsrvTag3("1");
        btd.add(seriNum, newRes1);

    }

}
