SELECT count(1) recordcount
FROM tf_f_user_svc
WHERE
    user_id = TO_NUMBER(:USER_ID)
    AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
    AND end_date+0> SYSDATE
 AND    NOT EXISTS
(SELECT 1
  FROM tf_f_user_svc a
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.service_id = :SERVICE_ID
   AND a.end_date+0 > SYSDATE)