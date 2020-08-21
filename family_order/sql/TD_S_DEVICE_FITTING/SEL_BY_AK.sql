--IS_CACHE=Y
SELECT eparchy_code,device_type_code,fitting_code,fitting,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark 
  FROM td_s_device_fitting
 WHERE device_type_code=:DEVICE_TYPE_CODE
   AND fitting_code=:FITTING_CODE