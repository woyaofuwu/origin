UPDATE td_s_device_model
   SET 
device_model=:DEVICE_MODEL,
update_time=SYSDATE,
update_staff_id=:UPDATE_STAFF_ID,
update_depart_id=:UPDATE_DEPART_ID,
remark=:REMARK  
 WHERE eparchy_code=:EPARCHY_CODE
   AND device_model_code=:DEVICE_MODEL_CODE
   AND device_type_code =:DEVICE_TYPE_CODE