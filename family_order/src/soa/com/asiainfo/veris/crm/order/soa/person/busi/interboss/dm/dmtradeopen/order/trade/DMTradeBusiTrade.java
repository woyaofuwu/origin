
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradeopen.order.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradeopen.order.requestdata.DMTradeBusiRequestData;

public class DMTradeBusiTrade extends BaseTrade implements ITrade
{
    static final Logger logger = Logger.getLogger(DMTradeBusiTrade.class);

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        DMTradeBusiRequestData reqData = (DMTradeBusiRequestData) btd.getRD();

        String dMTag = reqData.getDMTag();

        if (logger.isDebugEnabled())
            logger.debug("-----------dMTag-------------" + dMTag);

        if (dMTag.equals("DMOPEN"))
        {
            if (logger.isDebugEnabled())
                logger.debug("-----------DMOPEN-------------");
            stringTradeOpenRes(reqData, btd);
        }
        else if (dMTag.equals("DMUPD"))
        {
            if (logger.isDebugEnabled())
                logger.debug("-----------DMUPD-------------");
            createUPDChildStableTrade(reqData, btd);
        }
        else if (dMTag.equals("DMCANCEL"))
        {
            if (logger.isDebugEnabled())
                logger.debug("-----------DMCANCEL-------------");
            createCancelChildStableTrade(reqData, btd);
        }
        else
        {
            CSAppException.apperr(DMBusiException.CRM_DM_148);
        }

    }

    public void createCancelChildStableTrade(DMTradeBusiRequestData reqData, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String userid = btd.getRD().getUca().getUserId();
        String resCode = reqData.getResCode();
        String serialNum = reqData.getSerialNum();

        if (logger.isDebugEnabled())
            logger.debug("----userid-----" + userid + "----resCode----" + resCode + "------serialNum-------" + serialNum);

        IDataset resInfos = UserResInfoQry.getUserResByRsrvStr(userid, serialNum);
        if (logger.isDebugEnabled())
            logger.debug("----------------resInfos---------------------" + resInfos.toString());

        if (IDataUtil.isEmpty(resInfos))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_149);
        }

        for (int i = 0; i < resInfos.size(); i++)
        {
            IData tempData = resInfos.getData(i);

            if (resCode.equals(tempData.getString("RES_CODE")))
            {
                stringTradeCanCelRes(serialNum, tempData, btd);
            }
        }
    }

    public void createUPDChildStableTrade(DMTradeBusiRequestData reqData, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String strUserId = btd.getRD().getUca().getUserId();
        String strResCode = reqData.getResCode();// / IMEI号
        String strSerialNumber = btd.getRD().getUca().getSerialNumber();// 手机号码
        String strUpdateTag = reqData.getUpdateTag();// 01-根据号码变更IMEI号 02-根据IMEI号变更手机号码
        if (logger.isDebugEnabled())
            logger.debug("-----------------createUPDChildStableTrade-------------------" + strUpdateTag);

        IDataset resInfos = new DatasetList();
        IData resInfo = new DataMap();

        // 01.根据号码变更IMEI号
        if ("01".equals(strUpdateTag))
        {
            resInfos = UserResInfoQry.getUserResByRsrvStr(strUserId, strSerialNumber);

            if (IDataUtil.isNotEmpty(resInfos))
            {
                resInfo = resInfos.getData(0);
            }
        }
        // 02.根据IMEI号变更手机号码
        if ("02".equals(strUpdateTag))
        {
            resInfos = UserResInfoQry.getUserResByResCode(strResCode);

            if (IDataUtil.isNotEmpty(resInfos))
            {
                if (strSerialNumber.equals(resInfos.getData(0).getString("RSRV_STR1")))
                {
                    CSAppException.apperr(DMBusiException.CRM_DM_124);
                }
                resInfo = resInfos.getData(0);
            }
            else
            {
                CSAppException.apperr(DMBusiException.CRM_DM_125);
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("------------resInfo------------" + resInfo.toString());
        stringTradeUpdRes(resInfo, btd);
        StringTradeAddRes(strResCode, strUserId, btd);
    }

    public void StringTradeAddRes(String rescode, String userId, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        ResTradeData resTD = new ResTradeData();

        resTD.setUserId(userId);
        resTD.setUserIdA("-1");
        resTD.setResTypeCode("F");
        resTD.setResCode(rescode);
        resTD.setInstId(SeqMgr.getInstId());
        resTD.setModifyTag("0");
        resTD.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        resTD.setEndDate(SysDateMgr.getAddMonthsNowday(12, SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD)));
        resTD.setRsrvStr1(btd.getRD().getUca().getSerialNumber());

        btd.add(btd.getRD().getUca().getSerialNumber(), resTD);
    }

    public void stringTradeCanCelRes(String serialNum, IData inparam, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("-----------inparam------------------" + inparam.toString());
        inparam.put("END_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        inparam.put("MODIFY_TAG", "1");
        if (logger.isDebugEnabled())
            logger.debug("-----------inparam----inparam--------------" + inparam.toString());

        ResTradeData resTD = new ResTradeData(inparam);

        btd.add(serialNum, resTD);
    }

    public void stringTradeOpenRes(DMTradeBusiRequestData reqData, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        IDataset resInfos = UserResInfoQry.getUserResByResCode(reqData.getResCode());

        if (IDataUtil.isNotEmpty(resInfos))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_119);
        }

        ResTradeData resTD = new ResTradeData();

        resTD.setUserId(btd.getRD().getUca().getUserId());
        resTD.setUserIdA("-1");
        resTD.setResTypeCode("F");
        resTD.setResCode(reqData.getResCode());
        resTD.setInstId(SeqMgr.getInstId());
        resTD.setModifyTag("0");
        resTD.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        resTD.setEndDate(SysDateMgr.getAddMonthsNowday(12, SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD)));
        resTD.setRsrvStr1(btd.getRD().getUca().getSerialNumber());

        btd.add(btd.getRD().getUca().getSerialNumber(), resTD);
    }

    public void stringTradeUpdRes(IData inparam, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("-----------inparam------------------" + inparam.toString());
        inparam.put("END_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        inparam.put("MODIFY_TAG", "1");

        ResTradeData resTD = new ResTradeData(inparam);

        btd.add(btd.getRD().getUca().getSerialNumber(), resTD);
    }
}
