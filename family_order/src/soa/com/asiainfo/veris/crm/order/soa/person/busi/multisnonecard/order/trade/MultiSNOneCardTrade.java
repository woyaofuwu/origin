
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.MultiSNOneCardBean;
import com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order.requestdata.MultiSNOneCardRequestData;

public class MultiSNOneCardTrade extends BaseTrade implements ITrade
{

    /**
     * 通过IBOSS查询判断台湾副卡号码能否签权
     * 
     * @param reqData
     * @return
     * @throws Exception
     */
    public boolean checkResourceForMphone(MultiSNOneCardRequestData reqData) throws Exception
    {
        String inModeCode = "0";
        String routeEparchycode = "0898";
        String routeType = "00";
        String routeValue = "000";
        String serialNumberF = reqData.getDeputy_sn_input();
        String userPassWd = "";
        String idCardType = reqData.getUca().getCustomer().getPsptTypeCode();
        String idCardNum = reqData.getUca().getCustomer().getPsptId();
        String provinceCode = "8981";
        String acceptDate = SysDateMgr.getSysDate("YYYYMMDDHH24MISS");// 原代码去当前时间的前后几秒，参数为0，取的时间为当前时间，格式为YYYYMMDDHH24MISS
        String kingId = "BIP2B293_T2001026_0_0";
        // IData data = (IData) HttpHelper.callHttpSvc( "TCS_CrmToPlat", inparam, true); //yangyztest
        IData data = IBossCall.checkResourceForMphone(inModeCode, serialNumberF, idCardType, idCardNum, routeType, routeValue, routeEparchycode, provinceCode, acceptDate, kingId, userPassWd);
        if ("00".equals(data.getString("X_RESULTCODE")))
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
        MultiSNOneCardRequestData reqData = (MultiSNOneCardRequestData) btd.getRD();
        MultiSNOneCardBean multibean = new MultiSNOneCardBean();
        if ("854".equals(reqData.getCooper_area())) // 台湾远传
        {
            if ("01".equals(reqData.getOper_type())) // 绑定
            {
                this.checkResourceForMphone(reqData);// 副号码签权
            }
            this.subCardChangeOrder(reqData, multibean); // 副号码业务变更
        }        
        btd.getMainTradeData().setRsrvStr1(reqData.getCooper_area());// 漫游属性 0允许漫游，1不允许漫游

        crtChildStableTrade(btd);
        tcsOneCardmuilNumber(reqData,btd);
    }

    /**
     * 登记TF_B_TRADE_OTHER表数据
     * 
     * @param btd
     * @throws Exception
     */
    public void crtChildStableTrade(BusiTradeData btd) throws Exception
    {
        MultiSNOneCardRequestData multiSNOneCardRD = (MultiSNOneCardRequestData) btd.getRD();
        MultiSNOneCardBean multibean = new MultiSNOneCardBean();
        stringTableTradeOther(multibean, multiSNOneCardRD, btd);
    }

    /**
     * 登记TF_B_TRADE_OTHER表数据
     * 
     * @param multibean
     * @param reqData
     * @param btd
     * @throws Exception
     */
    protected void stringTableTradeOther(MultiSNOneCardBean multibean, MultiSNOneCardRequestData reqData, BusiTradeData btd) throws Exception
    {
        OtherTradeData otherTD = new OtherTradeData();
        IData params = new DataMap();

        if ("01".equals(reqData.getOper_type()))
        {
            params.clear();
            IDataset resDataset = UserResInfoQry.getUserResInfoByUserId(reqData.getUca().getUserId());
            if (!resDataset.isEmpty())
            {
                for (int i = 0; i < resDataset.size(); i++)
                {
                    if ("1".equals(resDataset.getData(i).getString("RES_TYPE_CODE")))
                    {
                        otherTD.setRsrvStr7(resDataset.getData(i).getString("IMSI"));
                        break;
                    }
                }
            }
            else
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_71);
            }
            otherTD.setUserId(reqData.getUca().getUserId());
            otherTD.setRsrvValueCode("SIMM");
            otherTD.setRsrvValue("万众一卡多号");
            if ("1".equals(reqData.getService_type()))
            {
                otherTD.setRsrvStr1(reqData.getDeputy_sn_input());
                otherTD.setRsrvStr9(reqData.getDeputy_sn_input());

            }
            else
            {
                otherTD.setRsrvStr1("");
                otherTD.setRsrvStr9("系统控制");

            }
            otherTD.setRsrvStr2(reqData.getCooper_area());
            otherTD.setRsrvStr3(reqData.getCooper_net());
            otherTD.setRsrvStr4(reqData.getOper_type());
            otherTD.setRsrvStr5(reqData.getMax_fee());
            otherTD.setRsrvStr6(reqData.getSum_fee());
            otherTD.setRsrvStr8(reqData.getService_type());
            otherTD.setRsrvStr10(reqData.getAuth_serial_number());
            otherTD.setRsrvDate10(reqData.getVaild_date());
            otherTD.setEndDate(reqData.getInvaild_date());
            otherTD.setStartDate(reqData.getVaild_date());
            otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTD.setModifyTag("0");
            otherTD.setInstId(SeqMgr.getInstId());

            btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
            return;
        }
        if (("04".equals(reqData.getOper_type())) || ("03".equals(reqData.getOper_type())))
        {
            params.put("USER_ID", reqData.getUca().getUserId());
            params.put("RSRV_VALUE_CODE", "SIMM");
            otherTD.setUserId(reqData.getUca().getUserId());
            IDataset dataset = multibean.getUserOther(params);
            for (int i = 0; i < dataset.size(); i++)
            {
                if (dataset.getData(i).getString("RSRV_STR2").equals(reqData.getCooper_area()))
                {
                    otherTD.setRsrvValueCode("SIMM");
                    otherTD.setRsrvValue(reqData.getUca().getUserId());
                    otherTD.setRsrvStr1(dataset.getData(i).getString("RSRV_STR1", ""));
                    otherTD.setRsrvStr2(dataset.getData(i).getString("RSRV_STR2", ""));
                    otherTD.setRsrvStr3(dataset.getData(i).getString("RSRV_STR3", ""));
                    otherTD.setRsrvStr4(reqData.getOper_type());
                    otherTD.setRsrvStr5(dataset.getData(i).getString("RSRV_STR5", ""));
                    otherTD.setRsrvStr6(dataset.getData(i).getString("RSRV_STR6", ""));
                    otherTD.setRsrvStr7(dataset.getData(i).getString("RSRV_STR7", ""));
                    otherTD.setRsrvStr8(dataset.getData(i).getString("RSRV_STR8", ""));
                    otherTD.setRsrvStr10(dataset.getData(i).getString("RSRV_STR10", ""));
                    otherTD.setRsrvDate10(dataset.getData(i).getString("START_DATE"));
                    otherTD.setEndDate(dataset.getData(i).getString("END_DATE"));
                    otherTD.setStartDate(dataset.getData(i).getString("START_DATE"));
                    otherTD.setStaffId(dataset.getData(i).getString("STAFF_ID", ""));
                    otherTD.setDepartId(dataset.getData(i).getString("DEPART_ID", ""));
                    otherTD.setInstId(dataset.getData(i).getString("INST_ID", ""));
                    otherTD.setModifyTag("2");

                    btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
                    return;
                }
            }
            CSAppException.apperr(CrmUserException.CRM_USER_723);
        }
        if ("02".equals(reqData.getOper_type()))
        {
            params.put("USER_ID", reqData.getUca().getUserId());
            params.put("RSRV_VALUE_CODE", "SIMM");
            otherTD.setUserId(reqData.getUca().getUserId());
            IDataset dataset = multibean.getUserOther(params);
            for (int i = 0; i < dataset.size(); i++)
            {
                if (dataset.getData(i).getString("RSRV_STR2").equals(reqData.getCooper_area()))
                {
                    otherTD.setRsrvValueCode("SIMM");
                    otherTD.setRsrvValue("万众一卡多号");
                    otherTD.setRsrvStr1(dataset.getData(i).getString("RSRV_STR1", ""));
                    otherTD.setRsrvStr2(dataset.getData(i).getString("RSRV_STR2", ""));
                    otherTD.setRsrvStr3(dataset.getData(i).getString("RSRV_STR3", ""));
                    otherTD.setRsrvStr4(reqData.getOper_type());
                    otherTD.setRsrvStr5(dataset.getData(i).getString("RSRV_STR5", ""));
                    otherTD.setRsrvStr6(dataset.getData(i).getString("RSRV_STR6", ""));
                    otherTD.setRsrvStr7(dataset.getData(i).getString("RSRV_STR7", ""));
                    otherTD.setRsrvStr8(dataset.getData(i).getString("RSRV_STR8", ""));
                    otherTD.setRsrvStr10(dataset.getData(i).getString("RSRV_STR10", ""));
                    otherTD.setRsrvDate10(dataset.getData(i).getString("START_DATE"));
                    otherTD.setEndDate(dataset.getData(i).getString("END_DATE"));
                    otherTD.setStartDate(dataset.getData(i).getString("START_DATE"));
                    otherTD.setStaffId(dataset.getData(i).getString("STAFF_ID", ""));
                    otherTD.setDepartId(dataset.getData(i).getString("DEPART_ID", ""));
                    otherTD.setInstId(dataset.getData(i).getString("INST_ID", ""));
                    otherTD.setModifyTag("1");

                    btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
                    return;
                }
            }
            CSAppException.apperr(CrmUserException.CRM_USER_723);
        }
        CSAppException.apperr(CrmCommException.CRM_COMM_387);
    }

    /**
     * 通过IBOSS查询副号码能否进行业务变更
     * 
     * @param reqData
     * @param multibean
     * @return
     * @throws Exception
     * @author wangww
     */
    public boolean subCardChangeOrder(MultiSNOneCardRequestData reqData, MultiSNOneCardBean multibean) throws Exception
    {
        IData inparam = new DataMap();
        String inModeCode = "0";
        String routeEparchycode = "0898";
        String routeType = "00";
        String routeValue = "000";
        String operCode = reqData.getOper_type();
        String serialNumber = reqData.getUca().getSerialNumber();
        String serialNumberF = reqData.getDeputy_sn_input();
        String imisF = "";
        if ("01".equals(reqData.getOper_type())) // 开通
        {
            imisF = "0";
        }
        else
        {
            IData params = new DataMap();
            params.put("USER_ID", reqData.getUca().getUserId());
            params.put("RSRV_VALUE_CODE", "SIMM");
            IDataset dataset = multibean.getUserOther(params);
            imisF = dataset.getData(0).getString("RSRV_STR7");
        }
        String provinceCode = "8981";
        String acceptDate = SysDateMgr.getSysDate("YYYYMMDDHH24MISS");
        String kingId = "BIP2B293_T2001027_0_0";
        // IData data = (IData) HttpHelper.callHttpSvc("TCS_CrmToPlat", inparam, true);//yangyz test
        IData data = IBossCall.subCardChangeOrder(inModeCode, serialNumberF, serialNumber, imisF, routeType, routeValue, routeEparchycode, provinceCode, acceptDate, kingId, operCode);
        if ("00".equals(data.getString("X_RESULTCODE")))
        {
            return true;
        }
        else
        {
            CSAppException.apperr(BizException.CRM_BIZ_5, data.getString("X_RESULTCODE"), data.getString("X_RESULTINFO"));
            return false;
        }
    }

    public boolean tcsOneCardmuilNumber(MultiSNOneCardRequestData reqData,BusiTradeData btd) throws Exception
    {
    	 List<OtherTradeData> otherTD = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
    	 IData data = new DataMap();
    	 for (int i = 0; i < otherTD.size(); i++)
         {
    		 String kindId = "BIP2B212_T2001202_0_0";
             String routeType = "00";
             String routeValue = "000";
             String provinceCode = "0898";
             String opr = otherTD.get(i).getRsrvStr4();
             String resTradeType = otherTD.get(i).getRsrvStr8();
             String imsi = otherTD.get(i).getRsrvStr7();
             String serialNumber = btd.getRD().getUca().getSerialNumber();
             String fee = otherTD.get(i).getRsrvStr5();
             String latencyFeeSum = otherTD.get(i).getRsrvStr6();
             String provinceA = "8981";
             String tradeCityCode = CSBizBean.getTradeEparchyCode();
             String serialNumberA = otherTD.get(i).getRsrvStr1();
             String outGroupId = otherTD.get(i).getRsrvStr2();
             String outNetType = otherTD.get(i).getRsrvStr3();
             String efftt = otherTD.get(i).getStartDate();
             String validTo = otherTD.get(i).getEndDate();
             data = IBossCall.HKOneCardMuilNumber(imsi, serialNumberA, serialNumber, opr, routeType, routeValue, resTradeType, provinceCode, latencyFeeSum, kindId, fee, provinceA, tradeCityCode, outGroupId, outNetType, efftt, validTo);
         }
        /*String inModeCode = "0";
        String routeEparchycode = "0898";
        String routeType = "00";
        String routeValue = "000";
        String serialNumberF = reqData.getDeputy_sn_input();
        String userPassWd = "";
        String idCardType = reqData.getUca().getCustomer().getPsptTypeCode();
        String idCardNum = reqData.getUca().getCustomer().getPsptId();
        String provinceCode = "8981";
        String acceptDate = SysDateMgr.getSysDate();// 原代码去当前时间的前后几秒，参数为0，取的时间为当前时间，格式为YYYYMMDDHH24MISS
        String resValueCode = "SIMM";
        String kingId = "BIP2B140_T2001202_0_0";
        String oprCode = reqData.getOper_type();
        String servType = reqData.getService_type();
        String dailyConsumeLimit = reqData.getMax_fee();
        String TotalConsumeLimit = reqData.getSum_fee();
        String sn = reqData.getAuth_serial_number();
        // IData data = (IData) HttpHelper.callHttpSvc( "TCS_CrmToPlat", inparam, true); //yangyztest
        IData data = IBossCall.tcsOneCardmuilNumber(resValueCode, inModeCode, serialNumberF, idCardType, idCardNum, routeType, routeValue, routeEparchycode, provinceCode, acceptDate, kingId, userPassWd, oprCode, servType, dailyConsumeLimit,
                TotalConsumeLimit, sn);*/
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

}
