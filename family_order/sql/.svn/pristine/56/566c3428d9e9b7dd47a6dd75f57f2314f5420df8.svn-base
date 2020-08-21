UPDATE tf_f_user
   SET mpute_month_fee = :MPUTE_TAG,
       mpute_date = DECODE(:MPUTE_TAG,'2',TRUNC(SYSDATE,'MM'),SYSDATE)
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)