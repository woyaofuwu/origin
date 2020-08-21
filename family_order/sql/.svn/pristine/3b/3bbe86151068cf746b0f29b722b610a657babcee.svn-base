SELECT /*+index(a)*/ partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_relation_uu a
 WHERE user_id_a = (SELECT user_id_a
                      FROM tf_f_relation_uu b
                     WHERE b.user_id_b = :USER_ID
                       AND b.partition_id = to_number(MOD(:USER_ID, 10000))
                       And ( b.role_code_b =:ROLE_CODE_B Or  :ROLE_CODE_B= '*' )
                       AND relation_type_code = :RELATION_TYPE_CODE
                       AND b.end_date + 0 > SYSDATE)      
   AND a.end_date + 0 > SYSDATE