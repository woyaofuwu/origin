UPDATE tf_f_user_brandchange
   SET brand_code=:BRAND_CODE,rsrv_str1=:RSRV_STR1,rsrv_str2=:RSRV_STR2,end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')