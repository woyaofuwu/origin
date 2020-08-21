UPDATE tf_f_user
   SET cust_id=TO_NUMBER(:CUST_ID),brand_code=nvl(:BRAND_CODE,brand_code),product_id=decode(:PRODUCT_ID,-1,product_id,:PRODUCT_ID),
   eparchy_code=:EPARCHY_CODE,city_code=:CITY_CODE,user_passwd=:USER_PASSWD,user_type_code=:USER_TYPE_CODE,
   serial_number=:SERIAL_NUMBER,update_time=sysdate
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)