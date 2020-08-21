SELECT partition_id,to_char(user_id) user_id,to_char(acct_id) acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,limit_type,to_char(limit) limit,complement_tag,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM tf_a_payrelation
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND (act_tag = :ACT_TAG or :ACT_TAG = '2')
   AND (default_tag = :DEFAULT_TAG or :DEFAULT_TAG = '2')
   AND :ACYC_ID BETWEEN start_cycle_id AND end_cycle_id
 order by default_tag desc