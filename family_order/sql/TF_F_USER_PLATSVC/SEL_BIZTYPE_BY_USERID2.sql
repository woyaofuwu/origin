SELECT a.*
FROM TF_F_USER_PLATSVC a
  WHERE  a.PARTITION_ID = MOD(:USER_ID,10000)
    AND a.USER_ID = :USER_ID
    AND a.SERVICE_ID IN(40227762,80025539)