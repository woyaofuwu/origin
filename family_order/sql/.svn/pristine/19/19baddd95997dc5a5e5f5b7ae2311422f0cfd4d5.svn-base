UPDATE tf_f_user_res
   SET end_date=to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 WHERE user_id =to_number(:USER_ID) 
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date>sysdate