UPDATE tf_f_user_deduct
   SET destroy_tag=:DESTROY_TAG,destroy_time=TO_DATE(:DESTROY_TIME, 'YYYY-MM-DD HH24:MI:SS'),destroy_city_code=:DESTROY_CITY_CODE,destroy_depart_id=:DESTROY_DEPART_ID,destroy_staff_id=:DESTROY_STAFF_ID  
 WHERE user_id=TO_NUMBER(:USER_ID) and destroy_tag<>'1'