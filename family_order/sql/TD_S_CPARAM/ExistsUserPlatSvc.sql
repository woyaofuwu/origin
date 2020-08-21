SELECT COUNT(1) recordcount
  FROM tf_f_user_platsvc
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND service_id = :SERVICE_ID
   AND end_date > SYSDATE