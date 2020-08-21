--IS_CACHE=Y
SELECT eparchy_code,device_type_code,color_code,color,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,x_tag 
  FROM td_s_device_color
 WHERE eparchy_code=:EPARCHY_CODE
   AND device_type_code=:DEVICE_TYPE_CODE
   AND color_code=:COLOR_CODE