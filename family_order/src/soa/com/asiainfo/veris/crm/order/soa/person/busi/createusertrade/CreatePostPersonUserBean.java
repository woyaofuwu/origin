
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;




public class CreatePostPersonUserBean extends CSBizBean
{

    public static IDataset getPostCardInfo(IData input) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        return Dao.qryByCode("TD_B_POSTCARD_INFO", "SEL_BY_SERIAL_NUMBER", data, Route.getCrmDefaultDb());
    }
    
    public static IDataset getPostSMSInfo() throws Exception
    {
        IData data = new DataMap();

        return Dao.qryByCode("TD_B_POSTCARD_INFO", "SEL_FOR_SMS", data, Route.getCrmDefaultDb());
    }
    
    public static int updateAuditResult(IData input) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", input.getString("SERIALNUMBER"));
        data.put("PSPT_ID", input.getString("CUSTCERTNO"));
        
        String setSql = "";    
        
        if(input.getString("AUDITTRANSACTIONID","").trim().length()>0){
            data.put("AUDITTRANSACTIONID", input.getString("AUDITTRANSACTIONID"));
            setSql += "  ,AUDITTRANSACTIONID= :AUDITTRANSACTIONID  ";
        }
        
        if(input.getString("BUSITYPE","").trim().length()>0){
            data.put("BUSITYPE", input.getString("BUSITYPE"));
            setSql += "  ,BUSITYPE= :BUSITYPE  ";

        }
        
        if(input.getString("AUDITSTATUS","").trim().length()>0){
            data.put("AUDITSTATUS", input.getString("AUDITSTATUS"));
            setSql += "  ,AUDITSTATUS= :AUDITSTATUS  ";

        }
        
        if(input.getString("AUDITMESSAGE","").trim().length()>0){
            data.put("AUDITMESSAGE", input.getString("AUDITMESSAGE"));
            setSql += "  ,AUDITMESSAGE= :AUDITMESSAGE  ";

        }
        
        if(input.getString("KEEPPARAM","").trim().length()>0){
            data.put("KEEPPARAM", input.getString("KEEPPARAM"));
            setSql += " ,KEEPPARAM= :KEEPPARAM  ";

        }
        
        if(input.getString("RESERMARK","").trim().length()>0){
            data.put("RESERMARK", input.getString("RESERMARK"));
            setSql += "  ,RESERMARK= :RESERMARK  ";
        }
        
        setSql = setSql.trim();
        
		
        StringBuilder updSql = new StringBuilder();
        int num = 0;
		if (setSql.length() > 0) {
		    if("42".equals(input.getString("BUSITYPE"))){
                setSql += "  ,UPDATE_TIME= SYSDATE  ";
                updSql.append(" UPDATE TL_B_UNITOPEN_RECORD  ");
            }else{
                updSql.append(" UPDATE TD_B_POSTCARD_INFO  ");
            }


			updSql.append(" SET  ");
			updSql.append(setSql.substring(1, setSql.length()));// 去掉第1个逗号
	        updSql.append("  WHERE SERIAL_NUMBER = :SERIAL_NUMBER AND PSPT_ID= :PSPT_ID  ");
	        num = Dao.executeUpdate(updSql, data, Route.getCrmDefaultDb());

		}

        return num ; 
    }
    public static void updatePostCardInfo(IData input) throws Exception
    {
        IData data = new DataMap();
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "STATE");
        IDataUtil.chkParam(input, "OLD_STATE");
        data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        data.put("STATE", input.getString("STATE"));
        data.put("OLD_STATE", input.getString("OLD_STATE"));                
        
        String setSql = "";
        
        if(input.getString("PICNAMEV2","").trim().length()>0){
            data.put("PICNAMEV2", input.getString("PICNAMEV2"));
            setSql += "  ,PICNAMEV2= :PICNAMEV2  ";

        }
        if(input.getString("PICNAMER","").trim().length()>0){
            data.put("PICNAMER", input.getString("PICNAMER"));
            setSql += "  ,PICNAMER= :PICNAMER  ";
        }
        if(input.getString("RESERMARK","").trim().length()>0){
            data.put("RESERMARK", input.getString("RESERMARK"));
            setSql += "  ,RESERMARK= :RESERMARK  ";
        }
        if(input.getString("CHECKTYPE","").trim().length()>0){
            data.put("CHECKTYPE", input.getString("CHECKTYPE"));
            setSql += "  ,CHECKTYPE= :CHECKTYPE  ";
        }
        if(input.getString("SALECHANNLE","").trim().length()>0){
            data.put("SALECHANNLE", input.getString("SALECHANNLE"));
            setSql += "  ,SALECHANNLE= :SALECHANNLE  ";
        }
        setSql = setSql.trim();
        
		StringBuilder updSql = new StringBuilder();
		updSql.append(" UPDATE TD_B_POSTCARD_INFO  ");
		updSql.append(" SET STATE= :STATE,UPDATA_TIME = sysdate   ");
		if (setSql.length() > 0) {
			updSql.append(setSql);
		}
        updSql.append("        WHERE  SERIAL_NUMBER = :SERIAL_NUMBER  AND  STATE= :OLD_STATE    ");
        Dao.executeUpdate(updSql, data, Route.getCrmDefaultDb());
                
    }
    public static void sendSMS(IData input) throws Exception
  
    {
    	// 拼短信表参数
    	IDataUtil.chkParam(input, "POST_PHONE");//发送短信的号码
    	IDataUtil.chkParam(input, "CONTENT");
 
        IData param = new DataMap();
        param.put("NOTICE_CONTENT", input.getString("CONTENT"));
        param.put("EPARCHY_CODE", input.getString("EPARCHY_CODE","0898"));
        param.put("IN_MODE_CODE", "0");
        param.put("RECV_OBJECT", input.getString("POST_PHONE"));
        param.put("RECV_ID", "99999999");
        param.put("REFER_STAFF_ID", input.getString("TRADE_STAFF_ID",""));
        param.put("REFER_DEPART_ID", input.getString("TRADE_DEPART_ID",""));
        param.put("REMARK", "邮寄卡短信");
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

        Dao.insert("ti_o_sms", param);
    }
    public static void updateSMSfalg(IData input) throws Exception
    {
        IData data = new DataMap();
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        data.put("SEND_SMS_FLAG", "1");
        Dao.executeUpdateByCodeCode("TD_B_POSTCARD_INFO", "UPDATE_SMSFLAG", data, Route.getCrmDefaultDb());
    }
    
    public static IDataset getDestroyuser() throws Exception
    {
        IData data = new DataMap();

        return Dao.qryByCode("TD_B_POSTCARD_INFO", "SEL_DESTROY_USER", data, Route.getCrmDefaultDb());
    }

    public static void updateDUfalg(IData input) throws Exception
    {
        IData data = new DataMap();
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        data.put("CANCEL_FLAG", "1");
        Dao.executeUpdateByCodeCode("TD_B_POSTCARD_INFO", "UPDATE_CANCEL_FLAG", data, Route.getCrmDefaultDb());
    }

    /**
     * 通过手机号码查询补换卡订单
     * @param serialNumber
     * @return
     */
    public IDataset queryExchangeOrderForSerialNumber(String serialNumber) throws Exception {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER",serialNumber);
        return Dao.qryByCode("TF_B_ORDER_DETAIL", "SEL_ORDER_BY_SN", data, Route.getCrmDefaultDb());
    }

    public int updateExchangeCardResult(IData data) throws Exception {
        return Dao.executeUpdateByCodeCode("TF_B_ORDER_DETAIL", "UPDATE_STATE_BY_SN_PSPT", data, Route.getCrmDefaultDb());

    }

    public void updateExchangeCardFinish(IData data) throws Exception {

        Dao.executeUpdateByCodeCode("TF_B_ORDER_DETAIL", "UPDATE_STATE_BY_SN_ICCID", data, Route.getCrmDefaultDb());


    }

    public void updateExchangeCardException(IData data) throws Exception {
        Dao.executeUpdateByCodeCode("TF_B_ORDER_DETAIL", "UPDATE_EXCEPTION_BY_SN_ICCID", data, Route.getCrmDefaultDb());
    }
}
