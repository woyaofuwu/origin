SELECT COUNT(1) recordcount
  FROM tf_f_user_discnt
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND discnt_code = :DISCNT_CODE
   AND end_date > SYSDATE