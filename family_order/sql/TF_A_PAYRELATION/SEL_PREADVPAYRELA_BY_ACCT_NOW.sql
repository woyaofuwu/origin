SELECT partition_id,to_char(user_id) user_id,to_char(acct_id) acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,limit_type,to_char(limit) limit,to_char(inst_id) inst_id,addup_months,addup_method,complement_tag,update_staff_id,update_depart_id,rsrv_str1,rsrv_str10,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_a_payrelation 
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND default_tag = '0' 
   AND act_tag = '1' 
   AND end_cycle_id>TO_CHAR(SYSDATE,'YYYYMMDD') 
   AND end_cycle_id>start_cycle_id