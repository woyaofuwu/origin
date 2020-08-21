DELETE FROM tf_f_user_discnt
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code=TO_NUMBER(:DISCNT_CODE)   
   and PRODUCT_ID=TO_NUMBER(:PRODUCT_ID)
   and PACKAGE_ID=TO_NUMBER(:PACKAGE_ID) 
   and user_id_a =TO_NUMBER(:USER_ID_A)	
   and start_date = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')