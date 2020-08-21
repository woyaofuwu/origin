UPDATE tf_f_user_discnt
   SET end_date = SYSDATE
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date+0 >= SYSDATE