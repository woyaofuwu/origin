SELECT *
  FROM TF_F_USER_PRODUCT A
 where A.USER_ID = :USER_ID
   and partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND SYSDATE BETWEEN start_date AND end_date