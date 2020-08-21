SELECT COUNT(1) recordcount
  FROM TF_F_USER_SVC r
 WHERE r.user_id = TO_NUMBER(:USER_ID)
   AND r.service_id ='467'
   AND r.PARTITION_ID=MOD(TO_NUMBER(:USER_ID), 10000)
   AND end_date + 0 > SYSDATE
   AND ROWNUM < 2