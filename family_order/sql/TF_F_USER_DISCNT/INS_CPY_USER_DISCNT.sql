INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time)
SELECT MOD(TO_NUMBER(:USER_ID_A),10000), TO_NUMBER(:USER_ID_A), user_id_a, discnt_code, spec_tag,relation_type_code,start_date,end_date,sysdate 
  FROM tf_f_user_discnt
 WHERE user_id = TO_NUMBER(:USER_ID_B)
   AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND end_date > SYSDATE