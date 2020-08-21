--IS_CACHE=Y
SELECT eparchy_code,device_type_code,fitting_code,fitting,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,0 x_tag
  FROM td_s_device_fitting
 WHERE eparchy_code=:EPARCHY_CODE
   AND ((:DEVICE_TYPE_CODE IS NOT NULL AND device_type_code=:DEVICE_TYPE_CODE) OR :DEVICE_TYPE_CODE IS NULL)
   AND ((:FITTING_CODE IS NOT NULL AND fitting_code=:FITTING_CODE) OR :FITTING_CODE IS NULL)