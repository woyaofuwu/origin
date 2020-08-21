SELECT partition_id,to_char(user_id) user_id,to_char(acct_id) acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,limit_type,to_char(limit) limit,complement_tag,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_a_payrelation a
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
and TO_CHAR(SYSDATE,'YYYYMMDD') < end_cycle_id
and act_tag='1'
and exists (select 1 from tf_f_user where partition_id = MOD(TO_NUMBER(a.user_id),10000) and user_id=a.user_id and brand_code like 'G0%')