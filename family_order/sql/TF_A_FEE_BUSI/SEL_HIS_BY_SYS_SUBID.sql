SELECT sys_code,to_char(log_id) log_id,to_char(sub_log_id) sub_log_id,partition_id,to_char(acc_date,'yyyy-mm-dd hh24:mi:ss') acc_date,to_char(oper_date,'yyyy-mm-dd hh24:mi:ss') oper_date,oper_type_code,profit_cen_id,to_char(acct_id) acct_id,agent_code,fee,dc_tag,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3 
  FROM tf_ah_fee_busi
 WHERE sys_code=:SYS_CODE
   AND sub_log_id=TO_NUMBER(:SUB_LOG_ID)
   AND partition_id=:PARTITION_ID