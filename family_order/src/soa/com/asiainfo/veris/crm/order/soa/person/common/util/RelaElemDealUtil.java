
package com.asiainfo.veris.crm.order.soa.person.common.util;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ElemRelaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.script.rule.common.SplCheckByRegular;

public class RelaElemDealUtil
{

    public static boolean addCheckByParam(BusiTradeData btd, IData relaInfo) throws Exception
    {
        BaseReqData reqData = btd.getRD();
        UcaData uca = reqData.getUca();

        String regExpSn = relaInfo.getString("REGEXP_SN");// 指定号码
        String appointTradeTypeCode = relaInfo.getString("TRADE_TYPE_CODE");// 指定业务类型
        String appointInModeCode = relaInfo.getString("IN_MODE_CODE");// 指定渠道

        boolean flag = true;
        if (StringUtils.isNotBlank(regExpSn) && !SplCheckByRegular.matcherText(regExpSn, uca.getSerialNumber()))
        {
            return false;
        }

        if (StringUtils.isNotBlank(appointTradeTypeCode) && !appointTradeTypeCode.equals(btd.getTradeTypeCode()))
        {
            return false;
        }

        if (StringUtils.isNotBlank(appointInModeCode) && appointInModeCode.indexOf(CSBizBean.getVisit().getInModeCode()) != -1)
        {
            return false;
        }

        return flag;
    }

    public static void addElemRelaTradeData(BusiTradeData btd, ProductModuleTradeData elemTD, ProductModuleTradeData relaElemTD) throws Exception
    {
        BaseReqData reqData = btd.getRD();
        UcaData uca = reqData.getUca();

        ElemRelaTradeData elemRelaTD = new ElemRelaTradeData();
        if (relaElemTD.getModifyTag().equals(BofConst.MODIFY_TAG_ADD) || relaElemTD.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
        {
            elemRelaTD.setModifyTag(relaElemTD.getModifyTag());
            elemRelaTD.setUserId(elemTD.getUserId());
            elemRelaTD.setElementTypeCode(elemTD.getElementType());
            elemRelaTD.setElementId(elemTD.getElementId());
            elemRelaTD.setInstId(elemTD.getInstId());
            elemRelaTD.setRelaUserId(relaElemTD.getUserId());
            elemRelaTD.setRelaElementTypeCode(relaElemTD.getElementType());
            elemRelaTD.setRelaElementId(relaElemTD.getElementId());
            elemRelaTD.setRelaInstId(relaElemTD.getInstId());
            btd.add(uca.getSerialNumber(), elemRelaTD);
        }
    }

    public static String getEndDate(String startDate, BusiTradeData btd, IData relaInfo) throws Exception
    {
        String endDate = null;

        String relaElementEnd = relaInfo.getString("RELA_ELEMENT_END");
        String endOffSet = relaInfo.getString("END_OFFSET");
        String endUnit = relaInfo.getString("END_UNIT");
        String dayLine = relaInfo.getString("DAYLINE");

        if (StringUtils.isNotBlank(relaElementEnd))
        {
            if ("NORMAL".equals(relaElementEnd))
            {
                endDate = SysDateMgr.endDate(startDate, "1", null, endOffSet, "3");
            }
            else if (relaElementEnd.indexOf("-") != -1)
            {
                endDate = relaElementEnd;
            }
            else if ("EndDateOfMonth".equals(relaElementEnd))
            {
                int iCompDay = 28;
                int iCurDay = Integer.parseInt(btd.getRD().getAcceptTime().substring(8, 10));
                if (StringUtils.isNotBlank(dayLine))
                {
                    iCompDay = Integer.parseInt(dayLine);
                }
                if (iCurDay > iCompDay)
                {
                    endDate = SysDateMgr.getAddMonthsLastDay(2);
                }
                else
                {
                    endDate = SysDateMgr.getLastDateThisMonth();
                }
            }
        }

        return endDate;
    }

    public static String getStartDate(BusiTradeData btd, IData relaInfo) throws Exception
    {
        String startDate = null;
        String relaElementStart = relaInfo.getString("RELA_ELEMENT_START");
        String startOffSet = relaInfo.getString("START_OFFSET");
        String startUnit = relaInfo.getString("START_UNIT");

        if (StringUtils.isNotBlank(relaElementStart))
        {
            if ("FirstDayOfNextMonth".equals(relaElementStart))
            {
                startDate = SysDateMgr.getFirstDayOfNextMonth();
            }
            else if ("GET_FIRSTDAYOFNEXTMONTHS".equals(relaElementStart))
            {
                startDate = SysDateMgr.startDate("1", null, startOffSet, "3");
            }
            else if (relaElementStart.indexOf("-") != -1)
            {
                startDate = relaElementStart;
            }
        }

        return startDate;
    }
}
