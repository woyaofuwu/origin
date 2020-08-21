SELECT partition_id,to_char(user_id) user_id,relation_type_code,to_char(user_id_a) user_id_a,discnt_code,spec_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE partition_id = TO_NUMBER(:PARTITION_ID)
   AND user_id = TO_NUMBER(:USER_ID)
   AND user_id_a+0 = TO_NUMBER(:USER_ID_A) 
AND end_date > sysdate
AND end_date > start_date
