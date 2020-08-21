delete from TF_F_USER_PLAT_ORDER
 WHERE partition_id = MOD(to_number(:USER_ID),10000)
   AND user_id = to_number(:USER_ID)
   AND BIZ_CODE = :BIZ_CODE
   AND SP_CODE = :SP_CODE
   AND sysdate between START_DATE  AND END_DATE