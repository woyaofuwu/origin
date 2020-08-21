UPDATE tf_a_noteprintlog_center
   SET cancel_tag='1',cancel_staff_id=:CANCEL_STAFF_ID,cancel_depart_id=:CANCEL_DEPART_ID,cancel_city_code=:CANCEL_CITY_CODE,cancel_eparchy_code=:CANCEL_EPARCHY_CODE  
 WHERE print_id=TO_NUMBER(:PRINT_ID)