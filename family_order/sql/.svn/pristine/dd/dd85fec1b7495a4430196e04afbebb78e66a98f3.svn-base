UPDATE tf_f_user_discnt
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID ,
    PRODUCT_ID=TO_NUMBER(:PRODUCT_ID), PACKAGE_ID=TO_NUMBER(:PACKAGE_ID)
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND USER_ID_A= TO_NUMBER(:USER_ID_A)
  AND INST_ID=TO_NUMBER(:INST_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND discnt_code+0 = :DISCNT_CODE
   AND SYSDATE < end_date+0