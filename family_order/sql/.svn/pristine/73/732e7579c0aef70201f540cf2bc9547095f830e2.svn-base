SELECT partition_id,to_char(user_id) user_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_validchange
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND end_date=(SELECT MAX(end_date) FROM tf_f_user_validchange
                 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                 AND user_id = TO_NUMBER(:USER_ID))