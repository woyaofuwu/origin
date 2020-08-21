SELECT partition_id,to_char(user_id) user_id,to_char(acct_id) acct_id,to_char(inst_id) inst_id ,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,limit_type,to_char(limit) limit,complement_tag,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time ,rsrv_str1 
  FROM tf_a_payrelation
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND default_tag = '0'
   AND act_tag = '1'
   AND TO_CHAR(SYSDATE,'YYYYMMDD')
              BETWEEN  start_cycle_id and end_cycle_id