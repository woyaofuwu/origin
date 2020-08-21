UPDATE tf_f_user_infochange
   SET product_id=:PRODUCT_ID,brand_code=:BRAND_CODE
 WHERE user_id=TO_NUMBER(:USER_ID)
 AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND start_date>=TO_DATE(:DATE_JUST, 'YYYY-MM-DD HH24:MI:SS')
   AND end_date>=TO_DATE(:DATE_JUST, 'YYYY-MM-DD HH24:MI:SS')