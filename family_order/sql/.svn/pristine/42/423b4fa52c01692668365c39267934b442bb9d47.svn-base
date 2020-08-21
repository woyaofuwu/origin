SELECT partition_id,to_char(user_id) user_id,to_char(acct_id) acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,limit_type,to_char(limit) limit,complement_tag,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_a_payrelation
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND (default_tag=:DEFAULT_TAG or :DEFAULT_TAG = '2')
   AND act_tag=:ACT_TAG
   AND start_cycle_id <= end_cycle_id
   AND end_cycle_id=
   (select max(a.end_cycle_id) from tf_a_payrelation a
    where a.user_id = TO_NUMBER(:USER_ID)
      and a.partition_id=mod(TO_NUMBER(:USER_ID),10000)
      and (a.default_tag = :DEFAULT_TAG or :DEFAULT_TAG = '2')
      AND (act_tag = :ACT_TAG or :ACT_TAG = '2')
      AND start_cycle_id <= end_cycle_id
      and (:ACYC_ID BETWEEN start_cycle_id AND end_cycle_id or :ACYC_ID is null))
    and (:ACYC_ID BETWEEN start_cycle_id AND end_cycle_id or :ACYC_ID is null)