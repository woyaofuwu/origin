UPDATE tf_f_user_infochange
   SET product_id=:PRODUCT_ID,brand_code=:BRAND_CODE,serial_number=:SERIAL_NUMBER,imsi=:IMSI,end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')