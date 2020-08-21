--IS_CACHE=Y
SELECT eparchy_code,device_type_code,factory_code,factory,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,0 x_tag
  FROM td_m_device_factory
 WHERE eparchy_code=:EPARCHY_CODE
   AND device_type_code=:DEVICE_TYPE_CODE