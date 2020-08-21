UPDATE tf_f_user_svc
   SET update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,rsrv_str2=:RSRV_STR2
 WHERE user_id = TO_NUMBER(:USER_ID)
    AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)  
    AND service_id = :SERVICE_ID    
    AND USER_ID_A= TO_NUMBER(:USER_ID_A)
    AND PRODUCT_ID=TO_NUMBER(:PRODUCT_ID)
    AND PACKAGE_ID=TO_NUMBER(:PACKAGE_ID)
    AND INST_ID=TO_NUMBER(:INST_ID)
    AND end_date+0 > sysdate