UPDATE tf_f_user_trans
   SET process_tag=:PROCESS_TAG,cancel_time=sysdate,cancel_eparchy_code=:CANCEL_EPARCHY_CODE,cancel_city_code=:CANCEL_CITY_CODE,cancel_depart_id=:CANCEL_DEPART_ID,cancel_staff_id=:CANCEL_STAFF_ID  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND para_code=:PARA_CODE
   AND rsrv_info1=:RSRV_INFO1
   AND process_tag=0