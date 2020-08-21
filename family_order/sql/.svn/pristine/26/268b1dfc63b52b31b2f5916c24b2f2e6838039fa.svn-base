UPDATE tf_f_user_res
   SET IMSI=:IMSI,KI=:KI,start_date=TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
 WHERE user_id=:USER_ID
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND res_type_code=:RES_TYPE_CODE
   AND res_code=:RES_CODE
   AND start_date=TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')