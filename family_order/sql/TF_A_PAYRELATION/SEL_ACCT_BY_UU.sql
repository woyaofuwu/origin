SELECT partition_id,user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,
end_cycle_id,default_tag,act_tag,limit_type,limit,complement_tag,update_staff_id,update_depart_id,update_time 
  FROM tf_a_payrelation a
 WHERE a.user_id IN
       (SELECT  user_id_b
  FROM tf_f_relation_uu a
 WHERE user_id_a In (SELECT user_id_a
                      FROM tf_f_relation_uu b
                     WHERE b.user_id_b = :USER_ID
                       AND b.partition_id = to_number(MOD(:USER_ID, 10000))
                       And ( b.role_code_b =:ROLE_CODE_B Or  :ROLE_CODE_B= '*' )
                       AND relation_type_code = :RELATION_TYPE_CODE
                       AND b.end_date + 0 > SYSDATE)      
   AND a.end_date + 0 > SYSDATE)               
   AND default_tag = '1'
   AND act_tag = '1'
   AND to_char(SYSDATE, 'YYYYMMDD') Between start_cycle_id AND end_cycle_id