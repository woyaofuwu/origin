--IS_CACHE=Y
SELECT eparchy_code,device_model_code,device_type_code,device_model,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,0 x_tag
  FROM td_s_device_model
 WHERE eparchy_code=:EPARCHY_CODE
   AND (:DEVICE_MODEL_CODE IS NULL or (:DEVICE_MODEL_CODE IS NOT NULL AND device_model_code=:DEVICE_MODEL_CODE))
   AND (:DEVICE_TYPE_CODE IS NULL  or (:DEVICE_TYPE_CODE  IS NOT NULL AND device_type_code=:DEVICE_TYPE_CODE))