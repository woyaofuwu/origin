
package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UserSertrackLogInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeSvcStateParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.requestdata.ServiceOperReqData;

public class ServiceOperTrade extends BaseTrade implements ITrade
{

    public void addUserSerTrackLog(BusiTradeData btd,String Serv_type) throws Exception
    {
        IData param = new DataMap();
        ServiceOperReqData req = (ServiceOperReqData) btd.getRD();
        UcaData uca = req.getUca();
//        String serviceId = req.getServiceId();
        String operCode = req.getOperCode();
        String tradeId = btd.getTradeId();
        String accptMonth = StrUtil.getAcceptMonthById(tradeId);
        String remark = "";
        String stateCode = "";

        String sendFlag = req.getSendFlag();
        String servType = "0";
        
        if ("04".equals(operCode))
        {
            remark = "国际漫游日封顶";
            stateCode = "0";
            if(sendFlag!=null&&("0".equals(sendFlag)||"1".equals(sendFlag))){
            	servType = "2";
            }else if(sendFlag!=null && "3".equals(sendFlag)){
            	servType = "0";
            }
        }
        else
        {
            remark = "国际漫游日封顶主动恢复";
            stateCode = "1";
            servType = Serv_type; //恢复时以暂停的那个
        }

        param.put("USER_ID", uca.getUserId());
        param.put("ACCEPT_MONTH", accptMonth);
        param.put("SERIAL_NUMBER", uca.getSerialNumber());
        param.put("REMARK", remark);
        param.put("TRADE_ID", tradeId);
        param.put("STATE_CODE", stateCode);

        StringBuilder strSql = new StringBuilder();
        StringBuilder strSqlDel = new StringBuilder();

        IData cond = new DataMap();

        // 插入MODIFY_TAG=2通知账务修改
        strSqlDel.append(" INSERT INTO TL_F_USER_SERTRACK_LOG(TRADE_ID,USER_ID,ACCEPT_MONTH,SERIAL_NUMBER,");
        strSqlDel.append(" SERV_TYPE,STATE_CODE,START_DATE,END_DATE,UPDATE_DATE,REMARK,MODIFY_TAG )");
        strSqlDel.append(" select :TRADE_ID ,USER_ID,ACCEPT_MONTH,SERIAL_NUMBER,");
        strSqlDel.append("  SERV_TYPE,STATE_CODE,START_DATE,sysdate,sysdate,REMARK,'2'");
        strSqlDel.append("  from TL_F_USER_SERTRACK_LOG");
        strSqlDel.append("  WHERE USER_ID = :USER_ID");
        strSqlDel.append("   AND MODIFY_TAG = '0'");
        strSqlDel.append("   AND SYSDATE BETWEEN START_DATE AND END_DATE");
        Dao.executeUpdate(strSqlDel, param);

        // 修改状态使轨迹表中用户其他记录失效
        strSql.append(" UPDATE TL_F_USER_SERTRACK_LOG   SET END_DATE=SYSDATE,MODIFY_TAG ='1' ");
        strSql.append(" WHERE USER_ID = :USER_ID AND SYSDATE BETWEEN START_DATE AND END_DATE  AND MODIFY_TAG = '0'");
        
        int r = Dao.executeUpdate(strSql, param);

        // 插入MODIFY_TAG=0通知账务新增

        cond.putAll(param);
        
        cond.put("SERV_TYPE", servType);
        cond.put("START_DATE", req.getAcceptTime());
        cond.put("END_DATE", SysDateMgr.getLastDateThisMonth()); //getLastDateThisMonth
        cond.put("UPDATE_DATE", req.getAcceptTime());
        cond.put("MODIFY_TAG", "0");
        Dao.insert("TL_F_USER_SERTRACK_LOG", cond);

        // Dao.executeUpdate(strSqlIns, param);
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ServiceOperReqData req = (ServiceOperReqData) btd.getRD();
        UcaData uca = req.getUca();
        String serviceId = req.getServiceId();
        String operCode = req.getOperCode();

        IDataset serviceStateChangeConfigs = TradeSvcStateParamInfoQry.querySvcStateParamByOperCode(btd.getTradeTypeCode(), uca.getBrandCode(), uca.getProductId(), uca.getUserEparchyCode(), operCode, serviceId);
        if (IDataUtil.isNotEmpty(serviceStateChangeConfigs))
        {
            SvcStateTradeData userSvcState = uca.getUserSvcsStateByServiceId(serviceId);
            if (userSvcState == null)
            {
                List<SvcTradeData> userSvcs = uca.getUserSvcBySvcId(serviceId);
                if (userSvcs.size() > 0)
                {
                    SvcStateTradeData newSvcStateTradeData = new SvcStateTradeData();
                    newSvcStateTradeData.setUserId(uca.getUserId());
                    newSvcStateTradeData.setInstId(SeqMgr.getInstId());
                    newSvcStateTradeData.setMainTag(userSvcs.get(0).getMainTag());
                    newSvcStateTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    newSvcStateTradeData.setServiceId(serviceId);
                    newSvcStateTradeData.setStartDate(req.getAcceptTime());
                    newSvcStateTradeData.setEndDate(SysDateMgr.getTheLastTime());
                    //恢复操作需要校验
	                if("04".equals(operCode)){
                		newSvcStateTradeData.setRsrvTag1(req.getSendFlag());//记录封顶原因标记
                		newSvcStateTradeData.setRsrvStr3(SysDateMgr.getNowCyc());
                	}
                    newSvcStateTradeData.setStateCode(serviceStateChangeConfigs.getData(0).getString("NEW_STATE_CODE"));
                    btd.add(uca.getSerialNumber(), newSvcStateTradeData);
                    
                    //由于22服务原来没有服务状态时，130业务送服务开通时会匹配不出联指，搞一条不存在的记录给服务开通。
                    SvcStateTradeData newSvcStateTradeDataDel = new SvcStateTradeData();
                    newSvcStateTradeDataDel.setUserId(uca.getUserId());
                    newSvcStateTradeDataDel.setInstId(SeqMgr.getInstId());
                    newSvcStateTradeDataDel.setMainTag(userSvcs.get(0).getMainTag());
                    newSvcStateTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    newSvcStateTradeDataDel.setServiceId(serviceId);
                    newSvcStateTradeDataDel.setStartDate(req.getAcceptTime());
                    newSvcStateTradeDataDel.setEndDate(req.getAcceptTime());
                    if("0".equals(serviceStateChangeConfigs.getData(0).getString("NEW_STATE_CODE"))){
                    	newSvcStateTradeDataDel.setStateCode("2");
                    }else{
                    	newSvcStateTradeDataDel.setStateCode("0");
                    }
                    newSvcStateTradeDataDel.setRemark("为了联指，人工造一条结束的");
                    
                    btd.add(uca.getSerialNumber(), newSvcStateTradeDataDel);
                }
                else
                {
                    CSAppException.apperr(ParamException.CRM_PARAM_400);
                }
            }
            else
            {
            	
                IData config = this.findMatchConfig(serviceStateChangeConfigs, userSvcState.getStateCode());
                if (IDataUtil.isNotEmpty(config))
                {
                	
                    SvcStateTradeData svcStateTradeData = userSvcState.clone();
                    svcStateTradeData.setEndDate(SysDateMgr.getLastSecond(req.getAcceptTime()));
                    svcStateTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(uca.getSerialNumber(), svcStateTradeData);

                    SvcStateTradeData newSvcStateTradeData = new SvcStateTradeData();
                    
                    //恢复操作需要校验
                	if("05".equals(operCode)){
                		String rsrvTag1 = userSvcState.getRsrvTag1();
                		String rsrvStr3 = userSvcState.getRsrvStr3();
                		if("1".equals(rsrvTag1) && SysDateMgr.getNowCyc().equals(rsrvStr3)){
                            CSAppException.apperr(CrmUserException.CRM_USER_783, "该用户的GPRS流量本月已使用超过50G，不能再次开通！");
                		}
                	}else if("04".equals(operCode)){
                		newSvcStateTradeData.setRsrvTag1(req.getSendFlag());//记录封顶原因标记
                		newSvcStateTradeData.setRsrvStr3(SysDateMgr.getNowCyc());
                	}
                    
                    newSvcStateTradeData.setUserId(uca.getUserId());
                    newSvcStateTradeData.setInstId(SeqMgr.getInstId());
                    newSvcStateTradeData.setMainTag(userSvcState.getMainTag());
                    newSvcStateTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    newSvcStateTradeData.setServiceId(serviceId);
                    newSvcStateTradeData.setStartDate(req.getAcceptTime());
                    newSvcStateTradeData.setEndDate(SysDateMgr.getTheLastTime());
                    newSvcStateTradeData.setStateCode(config.getString("NEW_STATE_CODE"));
                    
                    btd.add(uca.getSerialNumber(), newSvcStateTradeData);
                }
                else
                {
                    IData newConfig = this.findMatchNewStateConfig(serviceStateChangeConfigs, userSvcState.getStateCode());
                    if (IDataUtil.isNotEmpty(newConfig))
                    {
                        CSAppException.apperr(ParamException.CRM_PARAM_452);
                    }
                    else
                    {
                        CSAppException.apperr(ParamException.CRM_PARAM_451, serviceId);
                    }
                }
            }
        }
        else
        {
            CSAppException.apperr(ParamException.CRM_PARAM_450);
        }
        
        if("04".equals(req.getOperCode())){
        
        if("0".equals(req.getSendFlag()) || "1".equals(req.getSendFlag()) || "3".equals(req.getSendFlag()))
	        {
	             sendSMS(btd ,req.getSendFlag());
	          
	        }
        }

        String servType = req.getServType();
        if ("1".equals(servType) || "2".equals(servType) || "0".equals(servType))
        {
            if ("04".equals(operCode))
            {
                // 暂停
                String rsrvStr4 = "";
//                if ("0".equals(servType))
//                {
                    String sendFlag = req.getSendFlag();
                    if ("1".equals(sendFlag))
                    {
                        rsrvStr4 = "NATIONAL OVER 100G";
                    }
                    else if ("0".equals(sendFlag))
                    {
                        rsrvStr4 = "NATIONAL OVER 15G";
                    }
                    else if ("2".equals(sendFlag))
                    {
                        rsrvStr4 = "USER INITIATIVE SUSPEND";
                    }
                    else if ("3".equals(sendFlag))
                    {
                        rsrvStr4 = "INTERNATIONAL OVER 50M";
                    }
                    else if ("4".equals(sendFlag))
                    {
                        rsrvStr4 = "CPE OVER 60G";
                    }
//                }

                MainTradeData mainTD = btd.getMainTradeData();
                mainTD.setRsrvStr4(rsrvStr4);
                mainTD.setRsrvStr6("3");
                
                mainTD.setRsrvStr5(serviceId);
                mainTD.setRsrvStr7("04");
                mainTD.setRsrvStr8(servType);
                mainTD.setRsrvStr9(sendFlag);
            }
            else
            {
                // 恢复
                MainTradeData mainTD = btd.getMainTradeData();
                mainTD.setRsrvStr8("SERV_TYPE1");
            }

            boolean flag = true;
            String Serv_type = servType;
            if ("05".equals(operCode))
            {
            	IDataset serTrackLogs = UserSertrackLogInfoQry.queryByUserId(uca.getUserId());
            	if(IDataUtil.isNotEmpty(serTrackLogs)&&serTrackLogs.size() > 0){
            		Serv_type = serTrackLogs.getData(0).getString("SERV_TYPE");
            	} else{
                    flag = false;
                }
            }

            if (flag)
            {
                addUserSerTrackLog(btd,Serv_type);
            }
        }
    }

    private IData findMatchConfig(IDataset stateChangeConfigs, String stateCode)
    {
        for (Object stateChangeConfig : stateChangeConfigs)
        {
            IData config = (IData) stateChangeConfig;
            if (config.getString("OLD_STATE_CODE", "").equals(stateCode))
            {
                return config;
            }
        }
        return null;
    }
    
    private void sendSMS(BusiTradeData btd ,String flag) throws Exception
    {
    	
     String content ="";
     IData param = new DataMap();
     ServiceOperReqData req = (ServiceOperReqData) btd.getRD();
     UcaData uca = req.getUca();
     String gprsTotal= req.getGprsTotal();
     if( gprsTotal!= null && !gprsTotal.trim().equals(""))
     {
    	 content="【流量提醒】您好！您本月移动数据流量已达"+gprsTotal+"，将暂停数据流量功能，并将在下月自动恢复。中国移动"; 
     }
     else {
	     if("0".equals(flag))
	     {
	    	 content="【流量提醒】您好！您本月移动数据流量已达30GB，将暂停数据流量功能，并将在下月自动恢复。中国移动";
	     }
	     if("1".equals(flag))
	     {
	         //REQ201707240025单客户每月设置的总流量封顶值由50G调整为100G
	    	 //content="【流量提醒】您好！您本月移动数据流量已达50GB，将暂停数据流量功能，并将在下月自动恢复。中国移动"; 
	    	 content="【流量提醒】您好！您本月移动数据流量已达200GB，将暂停数据流量功能，并将在下月自动恢复。中国移动"; 
	     }
	     if("3".equals(flag))
	     {
	         content="【流量提醒】您好！您本日移动数据流量已达50M，将暂停数据流量功能。中国移动"; 	 
	     }
     }

     StringBuilder strSqlDel = new StringBuilder();
     IData smsData = new DataMap();
     
   
     smsData.put("RECV_OBJECT", uca.getSerialNumber());
     smsData.put("RECV_ID", uca.getUserId());
     smsData.put("NOTICE_CONTENT", content);
     smsData.put("SMS_KIND_CODE", "08");
     smsData.put("SMS_PRIORITY", "50");
     smsData.put("SMS_TYPE_CODE", "I1");     
     smsData.put("RECV_OBJECT_TYPE", "00");
     smsData.put("IN_MODE_CODE", "0");
     SmsSend.insSms(smsData);
    }

    private IData findMatchNewStateConfig(IDataset stateChangeConfigs, String stateCode)
    {
        for (Object stateChangeConfig : stateChangeConfigs)
        {
            IData config = (IData) stateChangeConfig;
            if (config.getString("NEW_STATE_CODE", "").equals(stateCode))
            {
                return config;
            }
        }
        return null;
    }
}
