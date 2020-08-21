SELECT partition_id,user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,inst_id,start_cycle_id,
end_cycle_id,default_tag,act_tag,limit_type,limit,complement_tag,update_staff_id,update_depart_id,
update_time 
  FROM tf_a_payrelation
 WHERE user_id=to_number(:USER_ID)
   AND partition_id=MOD(to_number(:USER_ID),10000)
   AND default_tag='1'
   AND act_tag='1'
   AND to_number(to_char(sysdate, 'yyyymmdd')) 
         BETWEEN start_cycle_id 
                 AND end_cycle_id