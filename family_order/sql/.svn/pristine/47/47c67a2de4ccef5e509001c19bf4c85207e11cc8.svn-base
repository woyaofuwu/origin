UPDATE tf_f_user_infochange
   SET end_date =TRUNC(SYSDATE+1)-1/24/3600
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND end_date >= TRUNC(LAST_DAY(SYSDATE))+1