package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.finish;   

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;

/** 
 * @ClassName: DealForAgentPresentFeeAction.java
 * @Description: 一证多号需求
 * @version: v1.0.0
 * @author: liquan
 */
public class ModifyCustOnePsptIdMoreNameAction implements ITradeFinishAction {

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        boolean action = true;   

        IDataset tradeCustmoerDs = TradeCustomerInfoQry.getTradeCustomerByTradeId(mainTrade.getString("TRADE_ID",""));
        if(tradeCustmoerDs!=null&&tradeCustmoerDs.size()>0){
            IData customerData = tradeCustmoerDs.getData(0);
            if(customerData.getString("IS_REAL_NAME","").equals("0")){//如是非实名制，不进行操作
                action = false ; 
            }
        }else{
            action = false ;
        }
        
		/*if(!remark.isEmpty() ){
            action = false ; 
        }*/
		/*
        if(params.isEmpty()){
            action = false ; 
        }else{             
               // IData params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
                if(params.getString("IS_REAL_NAME","").equals("0")){//如果是非实名制
                    action = false;
                }            
        }
        */
        /*//检查在线开关是否打开 和 该工号是否免身份验证
        //默认实名认证二代身份证信息，如果认证平台无法正常工作时，可通过此开关设置是否进行认证
        IDataset staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_IDCARD");

        String isVerify = "";
        if (IDataUtil.isNotEmpty(staticInfo)) {
            isVerify = staticInfo.getData(0).getString("PARA_CODE1", "0");//是否进行认证, 返回1为认证, 0为不认证
        }

        if (!isVerify.equals("1")) {//如不认证，则不进行一证多名插表和发短信动作，
            action = false;
        }

        //是否有不需要网上实名认证直接提交二代身份证识别仪采集信息权限,如果有权限，可跳过平台认证免认证
        boolean hasPriv = StaffPrivUtil.isFuncDataPriv(mainTrade.getString("TRADE_STAFF_ID"), "CRM_IDCARDWITHOUTVERIFY");//是否免认证    返回true为免认证  , false则需要认证 
        if (hasPriv) {
            action = false;
        }*/

        if (action) {
            /*
             *(张兴已校验，) 1.检查tf_f_customer表的实名制isrealname，如为1，要再去检查 TF_F_ONEPSPTMORENAME该手机号码和userid下，是否removetag=0(有效的记录)，如有，这修改为 1(无效)
             * 
             * 2.检查tf_f_cust_person，看开户人，在给定品牌下是否有一证多名的数据
             *   如存在，存量开户人：下发短信，变更实名制，走半停全停流程，发送客户资料变更工单，将设置为非实名制，特别的，客户资料变更完工不发短信，但变更实名制要发短信
             * 
             * 3.检查 TF_F_USER_PSPT，看使用人、经办人、责任人，在给定品牌下是否有一证多名的数据
             *   如存在，更新TF_F_USER_PSPT表的选出记录的人名
             */
            String serial_number = mainTrade.getString("SERIAL_NUMBER","");
            String tradeId = mainTrade.getString("TRADE_ID");
            IDataset custpersonDs = TradeCustPersonInfoQry.getTradeCustPersonByTradeId(tradeId);
            if (DataSetUtils.isNotBlank(custpersonDs)) {
                IData custPersonData = custpersonDs.getData(0);

                //开户人
                String PsptType = custPersonData.getString("PSPT_TYPE_CODE", "");
                if (PsptType.equals("0") || PsptType.equals("1") || PsptType.equals("2")|| PsptType.equals("3")) {//本地、外地身份证、户口
                    String custName = custPersonData.getString("CUST_NAME", "");
                    String psptId = custPersonData.getString("PSPT_ID", "");
                    if (custName.trim().length() > 0 && psptId.trim().length() > 0) {
                        insertCustomer(mainTrade, custName, psptId,serial_number);
                        //updateUseAgentZrr(mainTrade, custName, psptId);
                    }
                }

                //使用人
                String usePsptType = custPersonData.getString("RSRV_STR6", "");
                if (usePsptType.equals("0") || usePsptType.equals("1") || usePsptType.equals("2")|| usePsptType.equals("3")) {//本地、外地身份证、户口
                    String useName = custPersonData.getString("RSRV_STR5", "");
                    String usePsptid = custPersonData.getString("RSRV_STR7", "");
                    if (useName.trim().length() > 0 && usePsptid.trim().length() > 0) {
                        insertCustomer(mainTrade, useName, usePsptid,serial_number);
                        //updateUseAgentZrr(mainTrade, useName, usePsptid);
                    }
                }
            }

            //经办人
            IDataset customerDs = TradeCustomerInfoQry.getTradeCustomerByTradeId(tradeId);
            if (DataSetUtils.isNotBlank(customerDs)) {
                IData customerData = customerDs.getData(0);
                String agentPsptType = customerData.getString("RSRV_STR8", "");
                if (agentPsptType.equals("0") || agentPsptType.equals("1") || agentPsptType.equals("2")|| agentPsptType.equals("3")) {//本地、外地身份证、户口
                    String agentName = customerData.getString("RSRV_STR7", "");
                    String agentPsptid = customerData.getString("RSRV_STR9", "");
                    if (agentName.trim().length() > 0 && agentPsptid.trim().length() > 0) {
                        insertCustomer(mainTrade, agentName, agentPsptid,serial_number);
                        //updateUseAgentZrr(mainTrade, agentName, agentPsptid);
                    }
                }
            }

        }
    }

    private void insertCustomer(IData mainTrade, String custName, String psptId,String serial_number) throws Exception
    {
        IDataset ds = CustPersonInfoQry.qryPerInfoByPsptId_2(psptId, custName,serial_number);
        if (DataSetUtils.isNotBlank(ds)) {
            IDataset smsDs = new DatasetList();
            //插入表    TF_F_ONEPSPTMORENAME               
            for (int i = 0; i < ds.size(); i++) {
                IData data = ds.getData(i);
                String custId = data.getString("CUST_ID", "");
                //组装TF_F_ONEPSPTMORENAME表数据         
                long tradeId = Long.parseLong(SeqMgr.getTradeId());
                //long tradeId = mainTrade.getLong("TRADE_ID");
                data.put("TRADE_ID", tradeId);              
                data.put("REMOVE_TAG", "0");
                data.put("INSERT_DATE", SysDateMgr.getSysTime());
                data.put("INSERT_STAFFID", mainTrade.getString("TRADE_STAFF_ID"));
                data.put("FIRST_STOPTIME", null);
                data.put("SECOND_STOPTIME", null);
                data.put("REMARK", null);
                data.put("TRIGGER_TRADE_ID", mainTrade.getString("TRADE_ID",""));                      
                data.put("RSRV_STR1", null);
                data.put("RSRV_STR2", null);
                data.put("RSRV_STR3", null);
                data.put("RSRV_STR4", null);
                data.put("RSRV_STR5", null);
                data.put("RSRV_DATE1", null);
                data.put("RSRV_DATE2", null);
                data.put("RSRV_DATE3", null);
                data.put("RSRV_NUM1", null);
                data.put("RSRV_NUM2", null);
                data.put("RSRV_NUM3", null);
                data.put("RSRV_TAG1", null);
                data.put("RSRV_TAG2", null);
                data.put("RSRV_TAG3", null);

                // 拼短信表参数
                IData param = new DataMap();
                String sms = "尊敬的客户，您登记的客户证件信息有误，请凭个人有效身份证件和SIM卡于48小时内前往我公司营业网点或微信公众号（仅支持普通居民身份证），逾期将暂停通信服务。中国移动海南公司";
                param.put("NOTICE_CONTENT", sms);
                param.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE", ""));
                param.put("IN_MODE_CODE", "0");
                param.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
                param.put("RECV_ID", "99999999");
                param.put("REFER_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID", ""));
                param.put("REFER_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID", ""));
                param.put("REMARK", "一证多名告知短信");
                String seq = SeqMgr.getSmsSendId();
                long seq_id = Long.parseLong(seq);
                param.put("SMS_NOTICE_ID", seq_id);
                param.put("PARTITION_ID", seq_id % 1000);
                param.put("SEND_COUNT_CODE", "1");
                param.put("REFERED_COUNT", "0");
                param.put("CHAN_ID", "C009");
                param.put("SMS_NET_TAG", "0");
                param.put("RECV_OBJECT_TYPE", "00");
                param.put("SMS_TYPE_CODE", "20");
                param.put("SMS_KIND_CODE", "02");
                param.put("NOTICE_CONTENT_TYPE", "0");
                param.put("FORCE_REFER_COUNT", "1");
                param.put("FORCE_OBJECT", "10086");
                param.put("SMS_PRIORITY", "3000");
                param.put("DEAL_STATE", "15");
                param.put("SEND_TIME_CODE", "1");
                param.put("SEND_OBJECT_CODE", "6");
                param.put("REFER_TIME", SysDateMgr.getSysTime());
                param.put("DEAL_TIME", SysDateMgr.getSysTime());
                param.put("MONTH", SysDateMgr.getCurMonth());
                param.put("DAY", SysDateMgr.getCurDay());
                param.put("ISSTAT", "0");
                param.put("TIMEOUT", "0");
                smsDs.add(param);

                //取消实名制
                // 插入到期处理表，调客户资料变更接口，修改一证多名的存量存量用户为非实名制（不发短信）；                
                IData deal = new DataMap();
                String userId = data.getString("USER_ID","");
                String execTime  = SysDateMgr.getSysDate();
                deal.put("DEAL_ID", SeqMgr.getTradeId());
                deal.put("USER_ID", userId);
                deal.put("PARTITION_ID", userId.substring(userId.length() - 4));
                deal.put("SERIAL_NUMBER",  data.getString("SERIAL_NUMBER",""));
                deal.put("EPARCHY_CODE", "0898");
                deal.put("IN_TIME", SysDateMgr.getSysTime());
                deal.put("DEAL_STATE", "0");
                deal.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_ONEPSPTIDMORENAME);
                deal.put("EXEC_TIME", execTime);
                deal.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
                deal.put("TRADE_ID", tradeId);
                Dao.insert("TF_F_EXPIRE_DEAL", deal);    
            }

            Dao.insert("TF_F_ONEPSPTMORENAME", ds);
            Dao.insert("TI_O_SMS", smsDs);
        }
    }

  /*  private void updateUseAgentZrr(IData mainTrade, String custName, String psptId) throws Exception
    {

        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("PSPT_ID", psptId);
        Dao.qryByCodeParser("TF_F_USER_PSPT", "UPDATE_BY_PSPTID", param);

    }*/

}
