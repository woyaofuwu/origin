INSERT INTO tf_f_user_res(partition_id,user_id,res_type_code,res_code,start_date,end_date,update_time)
SELECT MOD(TO_NUMBER(:USER_ID_A),10000),TO_NUMBER(:USER_ID_A),res_type_code,res_code,start_date,end_date,sysdate
  FROM tf_f_user_res
 WHERE user_id = TO_NUMBER(:USER_ID_B)
   AND partition_id=MOD(TO_NUMBER(:USER_ID_B),10000)
   AND end_date >= SYSDATE