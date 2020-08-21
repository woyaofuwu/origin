UPDATE tf_f_user_discnt
   SET end_date = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=sysdate
 WHERE user_id_a = TO_NUMBER(:USER_ID_A)
   AND user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND discnt_code+0 =:DISCNT_CODE
   AND end_date > TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')