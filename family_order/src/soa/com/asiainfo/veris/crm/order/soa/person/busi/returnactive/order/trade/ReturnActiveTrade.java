
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ReturnActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.ReturnActiveBean;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.ReturnActiveReqData;

public class ReturnActiveTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        ReturnActiveReqData reqData = (ReturnActiveReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String acceptNum = reqData.getAcceptNum();
        if (StringUtils.isBlank(acceptNum))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "参加活动次数");
        }
        if (!StringUtils.isNumeric(acceptNum) || Integer.parseInt(acceptNum) <= 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1044, "参加活动次数");
        }

        ReturnActiveBean bean = BeanManager.createBean(ReturnActiveBean.class);

        IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", uca.getSerialNumber());
        IData userReturnActiveInfo = bean.getReturnActiveInfo(cond);

        int cardFee = userReturnActiveInfo.getInt("CARDFEE");
        int limitMoney = userReturnActiveInfo.getInt("LIMIT_MONEY");
        int sumMoney = Integer.parseInt(acceptNum) * limitMoney;
        int haveNum = userReturnActiveInfo.getInt("HAVE_NUM");

        MainTradeData mainTD = bd.getMainTradeData();
        mainTD.setRsrvStr1(acceptNum);
        mainTD.setRsrvStr2(userReturnActiveInfo.getString("CARD_ACCT_BALANCE_ID"));
        mainTD.setRsrvStr4(userReturnActiveInfo.getString("CASH_ACCT_BALANCE_ID"));
        if (cardFee > sumMoney)
        {
            mainTD.setRsrvStr3(String.valueOf(sumMoney));
            mainTD.setRsrvStr5("0");
            // 调账务接口
        }
        else
        {
            mainTD.setRsrvStr3(String.valueOf(cardFee));
            mainTD.setRsrvStr5(String.valueOf((sumMoney - cardFee)));
            // 调账务接口
        }

        int iAcceptNum = Integer.parseInt(acceptNum);
        if (haveNum < iAcceptNum)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_1, haveNum, iAcceptNum);
        }

        IDataset userOtherList = UserOtherInfoQry.getUserOther(uca.getUserId(), "RETURNACTIVE");
        for (int i = 0, size = userOtherList.size(); i < size; i++)
        {
            IData userOther = userOtherList.getData(i);
            iAcceptNum += userOther.getInt("RSRV_STR1");
            OtherTradeData delOtherTD = new OtherTradeData(userOther);
            delOtherTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            delOtherTD.setEndDate(reqData.getAcceptTime());
            bd.add(uca.getSerialNumber(), delOtherTD);
        }

        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(uca.getUserId());
        otherTD.setRsrvValueCode("RETURNACTIVE");
        otherTD.setRsrvValue("真情回馈百分百");
        otherTD.setRsrvStr1(String.valueOf(iAcceptNum));
        otherTD.setRsrvStr2(bd.getTradeId());
        otherTD.setStartDate(reqData.getAcceptTime());
        otherTD.setEndDate(SysDateMgr.getTheLastTime());
        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTD.setInstId(SeqMgr.getInstId());
        otherTD.setRemark("受理新增记录");

        bd.add(uca.getSerialNumber(), otherTD);
    }

}
