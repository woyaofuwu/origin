SELECT COUNT(1) recordcount
  FROM tf_f_user_platsvc a
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.service_id = :SERVICE_ID
   AND   (A.BIZ_STATE_CODE = 'A' OR A.BIZ_STATE_CODE = 'N' OR A.BIZ_STATE_CODE = 'L')
   AND a.end_date > SYSDATE