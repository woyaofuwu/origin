
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 白卡换号  工具类 
 * 
 * 日志表:  TF_F_SEL_WHITECARD_FLOW
 * 
 * @author zhengkai5
 * */
public class UserWhitCardChoiceSnInfoQry
{

	//根据手机号码查询 白卡换号 状态
    public static IData qryWhiteCardChoiceSnInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        SQLParser sql = new SQLParser(param);

        sql.addSQL(" select to_char(trans_id) trans_id,pspt_id,cust_name,contract_phone,email,contract_addr," +
        		"offer_code,advance_pay,chnnel_id,user_id,serial_number,serial_number_temp,eparchy_code,state," +
        		"TO_CHAR(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,TO_CHAR(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date," +
        		"TO_CHAR(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time," +
        		"imsi_new,pin_new,pin2_new,puk_new,puk2_new,ki_new,opc_new,sim_card_no_new," +
        		"opc_temp,ki_temp,imsi_temp,pin_temp,pin2_temp,puk_temp,puk2_temp,sim_card_no_temp,empty_card_id," +
        		"remark,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_str6,rsrv_str7 ");
        sql.addSQL(" from ucr_crm1.TF_F_SEL_WHITECARD_FLOW where 1=1");
        sql.addSQL(" and SERIAL_NUMBER = :SERIAL_NUMBER");
        sql.addSQL(" and STATE != 'N' ");
        
        IDataset ids = Dao.qryByParse(sql);
        
        if (IDataUtil.isNotEmpty(ids))  return ids.getData(0);
        else  return null;
    }
  
    //根据临时手机号码查询 白卡换号 状态
    public static IData qryWhiteCardChoiceSnInfoBySnTemp(String serialNumberTemp) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_TEMP", serialNumberTemp);

        SQLParser sql = new SQLParser(param);
        sql.addSQL(" select to_char(trans_id) trans_id,pspt_id,cust_name,contract_phone,email,contract_addr," +
        		"  offer_code,advance_pay,chnnel_id,user_id," +
        		" serial_number,serial_number_temp,eparchy_code,state," +
        		" TO_CHAR(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date," +
        		" TO_CHAR(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date," +
        		" TO_CHAR(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time," +
        		" imsi_new,pin_new,pin2_new,puk_new,puk2_new,ki_new,opc_new,sim_card_no_new," +
        		" opc_temp,ki_temp,imsi_temp,pin_temp,pin2_temp,puk_temp,puk2_temp,sim_card_no_temp,empty_card_id," +
        		" remark," +
        		" rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7," +
        		" rsrv_tag1,rsrv_tag2,rsrv_tag3  ");
        sql.addSQL(" from ucr_crm1.TF_F_SEL_WHITECARD_FLOW where 1=1");
        sql.addSQL(" and SERIAL_NUMBER = :SERIAL_NUMBER_TEMP");
        sql.addSQL(" and STATE != 'N' ");
        
        IDataset ids = Dao.qryByParse(sql);
        
        if (IDataUtil.isNotEmpty(ids))  return ids.getData(0);
        else  return null;
    }
    
    //根据  流水号   查询 白卡换号 状态
    public static IData qryWhiteCardChoiceSnInfoByTransId(String transId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRANS_ID", transId);

        SQLParser sql = new SQLParser(param);
        sql.addSQL(" select to_char(trans_id) trans_id,pspt_id,cust_name,contract_phone,email,contract_addr," +
        		"  offer_code,advance_pay,chnnel_id,user_id," +
        		" serial_number,serial_number_temp,eparchy_code,state," +
        		" TO_CHAR(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date," +
        		" TO_CHAR(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date," +
        		" TO_CHAR(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time," +
        		" imsi_new,pin_new,pin2_new,puk_new,puk2_new,ki_new,opc_new,sim_card_no_new," +
        		" opc_temp,ki_temp,imsi_temp,pin_temp,pin2_temp,puk_temp,puk2_temp,sim_card_no_temp,empty_card_id," +
        		" remark," +
        		" rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7," +
        		" rsrv_tag1,rsrv_tag2,rsrv_tag3  ");
        sql.addSQL(" from ucr_crm1.TF_F_SEL_WHITECARD_FLOW where 1=1");
        sql.addSQL(" and TRANS_ID = :TRANS_ID");
        
        IDataset ids = Dao.qryByParse(sql);
        
        if (IDataUtil.isNotEmpty(ids))  return ids.getData(0);
        else  return null;
    }

    //短信校验查询
	public static IDataset checkTimeOut(String transId) throws Exception
	{
        IData param = new DataMap();
        param.put("TRANS_ID", transId);

        SQLParser sql = new SQLParser(param);
        sql.addSQL(" select to_char(trans_id) trans_id,pspt_id,cust_name,contract_phone,email,contract_addr," +
        		"  offer_code,advance_pay,chnnel_id,user_id," +
        		" serial_number,serial_number_temp,eparchy_code,state," +
        		" TO_CHAR(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date," +
        		" TO_CHAR(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date," +
        		" TO_CHAR(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time," +
        		" imsi_new,pin_new,pin2_new,puk_new,puk2_new,ki_new,opc_new,sim_card_no_new," +
        		" opc_temp,ki_temp,imsi_temp,pin_temp,pin2_temp,puk_temp,puk2_temp,sim_card_no_temp,empty_card_id," +
        		" remark," +
        		" rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7," +
        		" rsrv_tag1,rsrv_tag2,rsrv_tag3  ");
        sql.addSQL(" from ucr_crm1.TF_F_SEL_WHITECARD_FLOW where 1=1");
        sql.addSQL(" and END_DATE > SYSDATE");
        sql.addSQL(" and TRANS_ID = :TRANS_ID");
        
        IDataset ids = Dao.qryByParse(sql);
        
        if (IDataUtil.isNotEmpty(ids))  return ids;
        else  return null;
	}

	public static int excuteUpdate(IData param) throws Exception
    {
         StringBuilder sql = new StringBuilder();
         sql.append(" update TF_F_SEL_WHITECARD_FLOW  set CONTRACT_PHONE=:CONTRACT_PHONE,EMAIL=:EMAIL,USER_ID=:USER_ID," );
         sql.append("STATE=:STATE," );
         sql.append("SERIAL_NUMBER_TEMP=:SERIAL_NUMBER_TEMP," );
         sql.append("START_DATE= TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')," );
         sql.append("END_DATE= TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')," );
         sql.append("UPDATE_TIME= TO_DATE(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),");
         sql.append("IMSI_NEW=:IMSI_NEW,PIN_NEW=:PIN_NEW,PIN2_NEW=:PIN2_NEW,PUK_NEW=:PUK_NEW,PUK2_NEW=:PUK2_NEW,");
         sql.append("KI_NEW=:KI_NEW,OPC_NEW=:OPC_NEW,SIM_CARD_NO_NEW=:SIM_CARD_NO_NEW,OPC_TEMP=:OPC_TEMP,");
         sql.append("KI_TEMP=:KI_TEMP,IMSI_TEMP=:IMSI_TEMP,PIN_TEMP=:PIN_TEMP,PIN2_TEMP=:PIN2_TEMP,");
         sql.append("PUK_TEMP=:PUK_TEMP,PUK2_TEMP=:PUK2_TEMP,SIM_CARD_NO_TEMP=:SIM_CARD_NO_TEMP,");
         sql.append("EMPTY_CARD_ID=:EMPTY_CARD_ID,REMARK=:REMARK,");
         sql.append("RSRV_STR1=:RSRV_STR1,RSRV_STR2=:RSRV_STR2,RSRV_STR3=:RSRV_STR3,");
         sql.append("RSRV_STR4=:RSRV_STR4,RSRV_STR5=:RSRV_STR5,RSRV_STR6=:RSRV_STR6,");
         sql.append("RSRV_STR7=:RSRV_STR7,RSRV_TAG1=:RSRV_TAG1,RSRV_TAG2=:RSRV_TAG2,");
         sql.append("RSRV_TAG3=:RSRV_TAG3");
         sql.append(" WHERE TRANS_ID =:TRANS_ID");
        sql.append(" AND SERIAL_NUMBER =:SERIAL_NUMBER");
        return Dao.executeUpdate(sql,param);
    }

}
