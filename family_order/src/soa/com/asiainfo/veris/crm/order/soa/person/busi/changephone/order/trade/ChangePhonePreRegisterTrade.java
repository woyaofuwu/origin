
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AltSnPlatMrgTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ChangePhonePreRegisterRequestData;

public class ChangePhonePreRegisterTrade extends BaseTrade implements ITrade
{

    public String convertIdType(String psptTypeCode, String direct) throws Exception
    {
        // TODO Auto-generated method stub

        IDataset dsCommonParam = CommparaInfoQry.getCommNetInfo("CSM", "8000", "PSPT_IBOSS_ALT");

        for (int i = 0; i < dsCommonParam.size(); i++)
        {
            if ("+".equals(direct))
            {
                String str = "," + dsCommonParam.getData(0).getString("PARA_CODE1", "") + ",";
                if (str.contains("," + psptTypeCode + ","))
                {
                    return dsCommonParam.getData(0).getString("PARA_CODE3", "");
                }
            }
            else
            {
                if (psptTypeCode.equals(dsCommonParam.getData(0).getString("PARA_CODE3", "")))
                {
                    return dsCommonParam.getData(0).getString("PARA_CODE1", "");
                }
            }
        }
        if (IDataUtil.isEmpty(dsCommonParam))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2924, psptTypeCode);
        }

        return "";
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        this.otherTableTrade(btd);
        this.preTradeTable(btd);
        this.prePlatTradeTable(btd);
        this.platmsg(btd);

        this.syncData(btd);

    }

    private void daoInsPreAltPlatTrade(BusiTradeData btd, IData param) throws Exception
    {
        // TODO Auto-generated method stub
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();

        String serialNumber = changePhoneReqData.getUca().getUser().getSerialNumber();

        AltSnPlatMrgTradeData AltsnPlatMrgTD = new AltSnPlatMrgTradeData();
        AltsnPlatMrgTD.setMonth(SysDateMgr.getCurMonth());
        AltsnPlatMrgTD.setSerialNumber(param.getString("SERIAL_NUMBER", ""));
        AltsnPlatMrgTD.setRelaSerialNumber(param.getString("RELA_SERIAL_NUMBER", ""));
        AltsnPlatMrgTD.setRelaType(param.getString("RELA_TYPE", ""));
        AltsnPlatMrgTD.setPreAcceptTime(param.getString("PRE_ACCEPT_TIME", ""));
        AltsnPlatMrgTD.setPlatSvcId(param.getString("PLAT_SVC_ID", ""));
        AltsnPlatMrgTD.setNeedTransfer(param.getString("NEED_TRANSFER", ""));
        AltsnPlatMrgTD.setIsLocalBase(param.getString("IS_LOCAL_BASE", ""));
        AltsnPlatMrgTD.setEparchyCode(param.getString("EPARCHY_CODE", ""));

        btd.add(param.getString("SERIAL_NUMBER", serialNumber), AltsnPlatMrgTD);
    }

    private Object DatasetList()
    {
        // TODO Auto-generated method stub
        return null;
    }

    private void otherTableTrade(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();

        MainTradeData tradeData = btd.getMainTradeData();

        tradeData.setRsrvStr7(changePhoneReqData.getOldSerialNum());
        tradeData.setRsrvStr6(changePhoneReqData.getPsptId());
        tradeData.setRsrvStr5(changePhoneReqData.getPsptTypeCode());

    }

    private void platmsg(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

    }

    private void prePlatTradeTable(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();

        IData param = new DataMap();

        String move_Info = changePhoneReqData.getMoveInfo();
        if(!"[]".equals(move_Info)&&null != move_Info){
            // MOVE_INFO2":[{"BIZ_INFO":"01","MOVED":"0"}]
            // MOVE_INFO=[{MOVED=["0", "0"], BIZ_INFO=["01", "02"]}]
            String pre_time = SysDateMgr.getSysTime();
            
            List list = Wade3DataTran.strToList(move_Info);
            IDataset moveInfo = Wade3DataTran.wade3To4Dataset(list);
            
            if (!IDataUtil.isEmpty(moveInfo))
            {
                
                param.put("RELA_TYPE", "1"); // 1：SERIAL_NUMBER为新号码
                param.put("SERIAL_NUMBER", changePhoneReqData.getNewSerialNum());
                param.put("RELA_SERIAL_NUMBER", changePhoneReqData.getOldSerialNum());
                param.put("PRE_ACCEPT_TIME", pre_time);
                
                param.put("IS_LOCAL_BASE", changePhoneReqData.getNewProvince());
                param.put("EPARCHY_CODE", changePhoneReqData.getNewEparchy());
                
                if ("A".equals(param.getString("IS_LOCAL_BASE", "B")))
                {
                    
                    if (moveInfo != null && moveInfo.size() > 0)
                    {// 有值代表发起方
                        for (int j = 0; j < moveInfo.size(); j++)
                        {// bizInfo的长度和move的长度对应,值也是对应的
                            
                            param.put("NEED_TRANSFER", moveInfo.getData(j).getString("MOVED"));
                            param.put("PLAT_SVC_ID", moveInfo.getData(j).getString("BIZ_INFO"));
                            if ("0".equals(param.getString("NEED_TRANSFER", "1")))
                            {// 1代表不转移,0代表转移
                                daoInsPreAltPlatTrade(btd, param);
                            }
                        }
                    }
                }
            }
            // 老号码写一组
            if ((moveInfo != null) && (moveInfo.size() > 0))
            {
                
                param.put("RELA_TYPE", "2"); // 1：SERIAL_NUMBER为新号码
                param.put("SERIAL_NUMBER", changePhoneReqData.getOldSerialNum());
                param.put("RELA_SERIAL_NUMBER", changePhoneReqData.getNewSerialNum());
                param.put("PRE_ACCEPT_TIME", pre_time);
                param.put("IS_LOCAL_BASE", changePhoneReqData.getOldProvince());
                param.put("EPARCHY_CODE", changePhoneReqData.getOldEparchy());
                
                if ("A".equals(param.getString("IS_LOCAL_BASE", "B")))
                {
                    
                    if (moveInfo != null && moveInfo.size() > 0)
                    {// 有值代表发起方
                        for (int j = 0; j < moveInfo.size(); j++)
                        {// bizInfo的长度和move的长度对应,值也是对应的
                            
                            param.put("NEED_TRANSFER", moveInfo.getData(j).getString("MOVED"));
                            param.put("PLAT_SVC_ID", moveInfo.getData(j).getString("BIZ_INFO"));
                            
                            if ("0".equals(param.getString("NEED_TRANSFER", "1")))
                            {// 1代表不转移,0代表转移
                                daoInsPreAltPlatTrade(btd, param);
                            }
                        }
                    }
                }
            }
        }

    }

    private void preTradeTable(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();
        String serialNumber = changePhoneReqData.getUca().getUser().getSerialNumber();

        MainTradeData tradeData = btd.getMainTradeData().clone();
        PreTradeData preTD = new PreTradeData();
        String sysdate = SysDateMgr.getSysTime();
        preTD.setMonth(SysDateMgr.getCurMonth());
        preTD.setTradeTypeCode(tradeData.getTradeTypeCode());
        preTD.setPreAcceptTime(sysdate);
        preTD.setPreInvalidTime(SysDateMgr.getAddMonthsNowday(1, sysdate));
        preTD.setRemark(btd.getRD().getRemark());
        preTD.setStatus("1");
        preTD.setRsrvStr1(changePhoneReqData.getNewSerialNum());
        preTD.setRsrvStr2(changePhoneReqData.getOldSerialNum());

        preTD.setRsrvStr5(changePhoneReqData.getNewEparchy());
        preTD.setRsrvStr6(changePhoneReqData.getRsrvstr1());
        preTD.setRsrvStr7(changePhoneReqData.getOldEparchy());
        preTD.setSerialNumber(serialNumber);
        preTD.setInModeCode(CSBizBean.getVisit().getInModeCode());

        btd.add(serialNumber, preTD);
    }

    private void provinceInDeal(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();
        IData param = new DataMap();
        param.put("OLD_ID_VALUE", changePhoneReqData.getOldSerialNum());
        param.put("NEW_ID_VALUE", changePhoneReqData.getNewSerialNum());
        
        String serial_number = btd.getMainTradeData().getSerialNumber();
        if(serial_number.equals(param.getString("NEW_ID_VALUE"))) {
            param.put("WH_HANDLE", "01"); // 01:新归属地接收业务申请
        }
        else {
            param.put("WH_HANDLE", "02"); // 02:原归属地接收业务申请
        }

        String channel = btd.getMainTradeData().getInModeCode();
        if (channel.equals("2"))
        {
            channel = "02";
        }
        else if (channel.equals("5"))
        {
            channel = "04";
        }
        else if (channel.equals("1"))
        {
            channel = "07";
        }
        else if (channel.equals("0"))
        {
            channel = "08";
        }
        else if (channel.equals("L"))
        {
            channel = "03";
        }
        else if (channel.equals("K"))
        {
            channel = "01";
        }
        else
        {
            channel = "08"; // 默认营业厅接入
        }
        param.put("CHANNEL", channel);

        param.put("ID_CARD_TYPE", convertIdType(changePhoneReqData.getPsptTypeCode(), "+"));
        param.put("ID_CARD_NUM", changePhoneReqData.getPsptId());
        param.put("OPR_CODE", "01"); // 01-预申请
        param.put("RESERVE", "");

        String moveinfo = changePhoneReqData.getMoveInfo();
        param.put("MOVE_INFO", moveinfo);
//        if(!"[]".equals(moveinfo)&&null != moveinfo){
//            IData moveInfo = new DataMap((changePhoneReqData.getMoveInfo()));
//            param.put("BIZ_INFO", moveInfo.get("BIZ_INFO"));
//            param.put("MOVED", moveInfo.get("MOVED"));
//        }

        param.put("SYN_TAG", "N");
        // 框架要求特殊处理

        IDataset resultInfos = CSAppCall.call("SS.ChangePhonePreRegisterIntfSVC.changePhonePreRegisterSyn", param);

        // 框架要求特殊处理 的恢复

        IData pPreUpdate = new DataMap();
        pPreUpdate.put("RSRV_STR7", resultInfos.getData(0).getString("BIZ_ORDER_RESULT") + "|" + resultInfos.getData(0).getString("RSPCODE")); // "X_RESULTCODE",
        // ""
        // )
        // )
        // ;
        pPreUpdate.put("RSRV_STR8", resultInfos.getData(0).getString("BIZ_ORDER_RSP_DESC")); // X_RESULTINFO", "" ) ) ;

        PreTradeData preTD = new PreTradeData();

        if (!"0000".equals(resultInfos.getData(0).getString("BIZ_ORDER_RESULT")))
        {
            String content = "您好，很抱歉，您不符合改号业务办理条件，详情请垂询10086。";
            IData sendInfo = new DataMap();
            sendInfo.put("EPARCHY_CODE", "0022");
            sendInfo.put("RECV_OBJECT", param.get("NEW_ID_VALUE"));
            sendInfo.put("RECV_ID", param.get("NEW_ID_VALUE"));
            sendInfo.put("SMS_PRIORITY", "50");
            sendInfo.put("NOTICE_CONTENT", content);
            sendInfo.put("REMARK", "改号业务预受理提醒");
            sendInfo.put("FORCE_OBJECT", "10086");
            SmsSend.insSms(sendInfo);
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2925, resultInfos.getData(0).getString("BIZ_ORDER_RSP_DESC"));
        }else
        {
            String content = "您好，经核实，您符合改号业务办理条件。请您在一个月内，用新号码发送短信“JHGH身份证号码”或“JHGH（空格）”到10086，拨打10086热线，或到营业厅开通业务。中国移动 ";
            IData sendInfo = new DataMap();
            sendInfo.put("EPARCHY_CODE", "0022");
            sendInfo.put("RECV_OBJECT", param.get("NEW_ID_VALUE"));
            sendInfo.put("RECV_ID", param.get("NEW_ID_VALUE"));
            sendInfo.put("SMS_PRIORITY", "50");
            sendInfo.put("NOTICE_CONTENT", content);
            sendInfo.put("REMARK", "改号业务预受理提醒");
            sendInfo.put("FORCE_OBJECT", "10086");
            SmsSend.insSms(sendInfo);        }
    }

    private IData provinceOutDeal(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();

        IData inData = new DataMap();
        IData returndata = new DataMap();
        String WhHandle = "";
        String psptId = "";
        String old_sn = changePhoneReqData.getOldSerialNum();
        String new_sn = changePhoneReqData.getNewSerialNum();
        if ("A".equals(changePhoneReqData.getNewProvince()) || "B".equals(changePhoneReqData.getNewProvince()))
        {
            inData.put("ROUTEVALUE", changePhoneReqData.getOldSerialNum());

            WhHandle = "01"; // 01:新归属地接收业务申请
            psptId = convertIdType(changePhoneReqData.getPsptTypeCode(), "+"); // 此数据，必定是本地的，带过去给落地方校验
        }
        else
        {
            inData.put("ROUTEVALUE", changePhoneReqData.getNewSerialNum());

            WhHandle = "02"; // 02:原归属地接收业务申请
            psptId = convertIdType(changePhoneReqData.getPsptTypeCode(), "+"); // 此数据，必定是本地的，带过去给落地方校验
        }
        try
        {
            // 调用一级BOSS的时候需要传入ROUTEVALUE,ROUTETYPE and by lihf
            inData.put("ROUTETYPE", "01");

            inData.put("KIND_ID", "BIP2B074_T2001073_0_0"); // 交易唯一标识
            inData.put("X_TRANS_CODE", ""); // 交易编码-IBOSS
            inData.put("OLD_ID_VALUE", old_sn);
            inData.put("NEW_ID_VALUE", new_sn);
            inData.put("WH_HANDLE", WhHandle);

            String channel = CSBizBean.getVisit().getInModeCode();
            if (channel.equals("2"))
            {
                channel = "02";
            }
            else if (channel.equals("5"))
            {
                channel = "04";
            }
            else if (channel.equals("1"))
            {
                channel = "07";
            }
            else if (channel.equals("0"))
            {
                channel = "08";
            }
            else if (channel.equals("L"))
            {
                channel = "03";
            }
            else if (channel.equals("K"))
            {
                channel = "01";
            }
            else
            {
                channel = "08"; // 默认营业厅接入
            }
            inData.put("CHANNEL", channel);

            inData.put("ID_CARD_TYPE", psptId);
            inData.put("ID_CARD_NUM", changePhoneReqData.getPsptId());
            inData.put("OPR_CODE", "01");
            inData.put("RESERVE", "");

            String moveinfo = changePhoneReqData.getMoveInfo();

            if(!"[]".equals(moveinfo)&&null != moveinfo){
                List list = Wade3DataTran.strToList(moveinfo);
                IDataset moveInfo = Wade3DataTran.wade3To4Dataset(list);

                IData temp1 = new DataMap();
                IDataset temp2 = new DatasetList();
                IDataset bizInfos = new DatasetList();
                IDataset movedInfos = new DatasetList();
                for (int i = 0; i < moveInfo.size(); i++)
                {
                    bizInfos.add(i, moveInfo.getData(i).getString("BIZ_INFO"));
                    movedInfos.add(i, moveInfo.getData(i).getString("MOVED"));
                }
                temp1.put("BIZ_INFO", bizInfos);
                temp1.put("MOVED", movedInfos);
                temp2.add(temp1);
                inData.put("MOVE_INFO", temp2);
            }
//            IDataset reData = IBossCall.callHttpIBOSS("IBOSS", inData);
            IDataset reData = IBossCall.dealInvokeUrl("BIP2B074_T2001073_0_0", "IBOSS6", inData);

            // 校验客户资料
            if (!"0000".equals(reData.getData(0).getString("X_RSPCODE")))
            {
                String content = "您好，很抱歉，您不符合改号业务办理条件，详情请垂询10086。";
                IData sendInfo = new DataMap();
                sendInfo.put("EPARCHY_CODE", "0022");
                sendInfo.put("RECV_OBJECT", new_sn);
                sendInfo.put("RECV_ID", new_sn);
                sendInfo.put("SMS_PRIORITY", "50");
                sendInfo.put("NOTICE_CONTENT", content);
                sendInfo.put("REMARK", "改号业务预受理提醒");
                sendInfo.put("FORCE_OBJECT", "10086");
                SmsSend.insSms(sendInfo);
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2925, reData.getData(0).getString("X_RESULTINFO"));
            }
            else
            {
                String content = "您好，经核实，您符合改号业务办理条件。请您在一个月内，用新号码发送短信“JHGH身份证号码”或“JHGH（空格）”到10086，拨打10086热线，或到营业厅开通业务。中国移动 ";
                IData sendInfo = new DataMap();
                sendInfo.put("EPARCHY_CODE", "0022");
                sendInfo.put("RECV_OBJECT", new_sn);
                sendInfo.put("RECV_ID", new_sn);
                sendInfo.put("SMS_PRIORITY", "50");
                sendInfo.put("NOTICE_CONTENT", content);
                sendInfo.put("REMARK", "改号业务预受理提醒");
                sendInfo.put("FORCE_OBJECT", "10086");
                SmsSend.insSms(sendInfo);
                
                returndata.put("X_RESULTINFO", "OK");
                returndata.put("X_RECORDNUM", "1");
                returndata.put("X_RESULTCODE", "0");
                return returndata;
            }

        }
        catch (Exception e)
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2928, e);

        }
        return returndata;
    }

    private void syncData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();

        if (!"N".equals(changePhoneReqData.getSynTag()))
        {

            String serialNumber = changePhoneReqData.getUca().getUser().getSerialNumber();
            List<PreTradeData> preTD = btd.getTradeDatas(TradeTableEnum.TRADE_PRE);

            String new_province = changePhoneReqData.getNewProvince();
            String old_province = changePhoneReqData.getOldProvince();
            String new_eparchy = changePhoneReqData.getNewEparchy();
            String old_eparchy = changePhoneReqData.getOldEparchy();
            if(("A".equals(new_province) || "B".equals(new_province))&&("A".equals(old_province) || "B".equals(old_province)))
            {
                preTD.get(0).setRsrvStr8("0000||OK");
                preTD.get(0).setRsrvStr4(changePhoneReqData.getPsptId());
//                 btd.add(serialNumber, preTD);
                this.provinceInDeal(btd);
            }
            else
            {
                this.provinceOutDeal(btd);

            }
        }
    }

}
