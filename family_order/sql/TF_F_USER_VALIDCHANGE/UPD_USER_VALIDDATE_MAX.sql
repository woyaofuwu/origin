UPDATE tf_f_user_validchange
   SET end_date= sysdate + :NUM
 WHERE partition_id=TO_NUMBER(:PARTITION_ID)
   AND user_id=TO_NUMBER(:USER_ID)
   AND end_date > sysdate + :NUM