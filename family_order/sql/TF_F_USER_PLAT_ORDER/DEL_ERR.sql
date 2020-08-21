DELETE FROM TF_F_USER_PLAT_ORDER a
 WHERE a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND a.start_date>a.end_date