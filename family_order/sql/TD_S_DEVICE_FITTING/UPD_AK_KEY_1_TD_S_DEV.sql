UPDATE td_s_device_fitting
   SET eparchy_code=:EPARCHY_CODE,device_type_code=:DEVICE_TYPE_CODE,fitting_code=:FITTING_CODE,fitting=:FITTING,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,remark=:REMARK  
 WHERE device_type_code=:DEVICE_TYPE_CODE
   AND fitting_code=:FITTING_CODE