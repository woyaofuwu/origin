SELECT partition_id,INST_ID,to_char(user_id) user_id,to_char(acct_id) acct_id,inst_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,limit_type,to_char(limit) limit,complement_tag,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_a_payrelation pr
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   and pr.ACCT_ID=:ACCT_ID
   AND default_tag = :DEFAULT_TAG
   and pr.PAYITEM_CODE = :PAYITEM_CODE
   AND act_tag = '1' 
   AND TO_CHAR(SYSDATE,'YYYYMMDD') BETWEEN  start_cycle_id and end_cycle_id 
   AND start_cycle_id < end_cycle_id