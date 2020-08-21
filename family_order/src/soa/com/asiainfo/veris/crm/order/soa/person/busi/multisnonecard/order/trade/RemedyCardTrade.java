
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.MultiSNOneCardBean;
import com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order.requestdata.MultiSNOneCardRequestData;

public class RemedyCardTrade extends BaseTrade implements ITrade
{

    public boolean changcardToIboss(MultiSNOneCardRequestData reqData,String oldImsi, String newImsi) throws Exception
    {
        String routeEparchycode = "0898";
        String provinceCode = "8981";
        String acceptDate = SysDateMgr.getSysDate();// 原代码去当前时间的前后几秒，参数为0，取的时间为当前时间，格式为YYYYMMDDHH24MISS
        String resValueCode = "SIMM";
        String kingId = "BIP2B158_T2001128_0_0";
        String oprCode = reqData.getOper_type();
        String servType = reqData.getService_type();
        String osn = reqData.getSerialNumberO();
        String sn = reqData.getAuth_serial_number();
        IData data = IBossCall.roamOCNCNotifyIBOSS(provinceCode,oldImsi, newImsi, sn, osn, "8981", routeEparchycode, sn, SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        if ("0000".equals(data.getString("X_RSPCODE")))
        {
            return true;
        }
        else
        {
            CSAppException.apperr(BizException.CRM_BIZ_5, data.getString("X_RESULTCODE"), data.getString("X_RESULTINFO"));
            return false;
        }
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        MultiSNOneCardRequestData multiSNOneCardRD = (MultiSNOneCardRequestData) btd.getRD();
        MultiSNOneCardBean multibean = new MultiSNOneCardBean();

        //
        if ("01".equals(multiSNOneCardRD.getOper_type()))
        {
            // td.setChildTradeInfo(X_TRADE_DATA.X_TRADE_MAIN, "IMSI_CODE", pageData.getString("IMSI_CODE"));无IMSICODE字段
            btd.getMainTradeData().setSerialNumberB("");// 插台账
        }
        else
        {
            btd.getMainTradeData().setSerialNumberB(multiSNOneCardRD.getSerialNumberO());// 插台账
        }
        stringTableTradeOther(multibean, multiSNOneCardRD, btd);
    }

    /**
     * 获取ismi
     *
     * @param userId
     * @throws Exception
     */
    public String getImsi(String userId) throws Exception
    {
        IDataset imsiInfos = UserOtherInfoQry.getUserOther(userId, "SIMM");
        String imsiCode = "";
        if (!IDataUtil.isEmpty(imsiInfos))
        {
            for (int i = 0; i < imsiInfos.size(); i++)
            {
                if (!"".equals(imsiInfos.getData(i).getString("RSRV_STR7","")))
                {
                    imsiCode = imsiInfos.getData(i).getString("RSRV_STR7");
                    break;
                }
            }
        }
        return imsiCode;
    }


    protected void stringTableTradeOther(MultiSNOneCardBean multibean, MultiSNOneCardRequestData reqData, BusiTradeData btd) throws Exception
    {
        OtherTradeData otherTD = new OtherTradeData();
        IData params = new DataMap();
        String sn = reqData.getAuth_serial_number();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        IDataset userOther = UserOtherInfoQry.getUserOtherInfo(userInfo.getString("USER_ID", ""), "SIMM");

        IDataset dataset = TradeInfoQry.getHisMainTradeLast(userInfo.getString("USER_ID", ""));

        String imsiOld = dataset.getData(0).getString("RSRV_STR6");
        String imsi = dataset.getData(0).getString("RSRV_STR7");

        if (IDataUtil.isEmpty(userOther))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户资料获取失败！");
        }
        // 补卡操作
       // String imsi = getImsi(userInfo.getString("USER_ID"));

    	IDataset resDataset = UserResInfoQry.getUserIMSI(reqData.getUca().getUserId());
    	String imsiRes = resDataset.getData(0).getString("IMSI");
    	IDataset tradeOther = TradeOtherInfoQry.queryTradeOtherBySIMM(reqData.getUca().getUserId());
    	String imsiTradeOther = tradeOther.getData(0).getString("RSRV_STR7");
        if ("01".equals(reqData.getOper_type()))
        {

        	if("".equals(imsiTradeOther)||imsiRes.equals(imsiTradeOther)){
        		CSAppException.apperr(CrmUserException.CRM_USER_1208);
        	}

            otherTD.setRsrvValueCode("SIMM");
            otherTD.setUserId(userInfo.getString("USER_ID"));
            otherTD.setRsrvValue(userOther.getData(0).getString("RSRV_VALUE"));
            otherTD.setRsrvStr1(userOther.getData(0).getString("RSRV_STR1"));
            otherTD.setRsrvStr2(userOther.getData(0).getString("RSRV_STR2"));
            otherTD.setRsrvStr3(userOther.getData(0).getString("RSRV_STR3"));
            otherTD.setRsrvStr4("01");
            otherTD.setRsrvStr5(userOther.getData(0).getString("RSRV_STR5"));
            otherTD.setRsrvStr6(userOther.getData(0).getString("RSRV_STR6"));
            otherTD.setRsrvStr7(imsi);
            otherTD.setRsrvStr8(userOther.getData(0).getString("RSRV_STR8"));
            otherTD.setRsrvStr9("");
            otherTD.setRsrvStr10(userOther.getData(0).getString("RSRV_STR10"));
            otherTD.setRsrvStr11("一卡多号补换卡,补卡操作");
            otherTD.setRsrvDate10(userOther.getData(0).getString("START_DATE"));
            otherTD.setStartDate(userOther.getData(0).getString("START_DATE"));
            otherTD.setEndDate(userOther.getData(0).getString("END_DATE"));
            otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTD.setModifyTag("2");
            otherTD.setInstId(SeqMgr.getInstId());
            btd.add(sn, otherTD);

            changcardToIboss(reqData, imsiOld,imsi);

            return;
        }
        else
        {// 换号操作
        	if("".equals(imsiTradeOther)){
        		CSAppException.apperr(CrmUserException.CRM_USER_1208);
        	}

        	String osn = reqData.getSerialNumberO();
            IData userInfos = UserInfoQry.getUserInfoBySN(osn);
            //String imsio = getImsi(userInfos.getString("USER_ID"));

            // 注销原有用户的信息，将结束日期更改为 今天
            IData resOld = userOther.getData(0);

            otherTD.setRsrvValueCode("SIMM");
            otherTD.setUserId(userInfo.getString("USER_ID"));
            otherTD.setRsrvValue(resOld.getString("RSRV_VALUE"));
            otherTD.setRsrvStr1(resOld.getString("RSRV_STR1"));
            otherTD.setRsrvStr2(resOld.getString("RSRV_STR2"));
            otherTD.setRsrvStr3(resOld.getString("RSRV_STR3"));
            otherTD.setRsrvStr4("01");
            otherTD.setRsrvStr5(resOld.getString("RSRV_STR5"));
            otherTD.setRsrvStr6(resOld.getString("RSRV_STR6"));
            otherTD.setRsrvStr7(resOld.getString("RSRV_STR7"));
            otherTD.setRsrvStr8(resOld.getString("RSRV_STR8"));
            otherTD.setRsrvStr9("");
            otherTD.setRsrvStr10(resOld.getString("RSRV_STR10"));
            otherTD.setRsrvStr11("一卡多号补换卡,换号操作");
            otherTD.setRsrvDate10(resOld.getString("START_DATE"));
            otherTD.setStartDate(userOther.getData(0).getString("START_DATE"));
            otherTD.setEndDate(userOther.getData(0).getString("END_DATE"));
            otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
            otherTD.setModifyTag("1");
            otherTD.setInstId(resOld.getString("INST_ID"));
            btd.add(sn, otherTD);

            // 新增一条现有号码的信息
            OtherTradeData otherNEW = new OtherTradeData();
            otherNEW.setUserId(userInfos.getString("USER_ID"));
            otherNEW.setRsrvValueCode("SIMM");
            otherNEW.setRsrvValue(resOld.getString("RSRV_VALUE"));
            otherNEW.setRsrvStr1(resOld.getString("RSRV_STR1"));
            otherNEW.setRsrvStr2(resOld.getString("RSRV_STR2"));
            otherNEW.setRsrvStr3(resOld.getString("RSRV_STR3"));
            otherNEW.setRsrvStr4("01");
            otherNEW.setRsrvStr5(resOld.getString("RSRV_STR5"));
            otherNEW.setRsrvStr6(resOld.getString("RSRV_STR6"));
            //otherNEW.setRsrvStr7(imsio);// 新的IMSI
            otherNEW.setRsrvStr7(imsi);// 新的IMSI
            otherNEW.setRsrvStr8(resOld.getString("RSRV_STR8"));
            otherNEW.setRsrvStr9("");
            otherNEW.setRsrvStr10(sn);
            otherNEW.setRsrvStr11("一卡多号补换卡,换号操作");
            otherNEW.setRsrvDate10(resOld.getString("START_DATE"));
            otherNEW.setStartDate(resOld.getString("START_DATE"));
            otherNEW.setEndDate(resOld.getString("END_DATE"));
            otherNEW.setStaffId(CSBizBean.getVisit().getStaffId());
            otherNEW.setDepartId(CSBizBean.getVisit().getDepartId());
            otherNEW.setEndDate(resOld.getString("END_DATE"));
            otherNEW.setModifyTag("0");
            otherNEW.setInstId(SeqMgr.getInstId());
            btd.add(osn, otherNEW);

            //changcardToIboss(reqData,imsi, imsio);
            changcardToIboss(reqData,imsiOld, imsi);

        }
    }
}
