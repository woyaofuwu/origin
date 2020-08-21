--IS_CACHE=Y
SELECT eparchy_code,capacity_type_code,capacity_type,remark,
to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
update_staff_id,update_depart_id,0 x_tag
  FROM td_s_simcapacity
 WHERE (eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ')
   AND (:CAPACITY_TYPE_CODE IS NULL OR (:CAPACITY_TYPE_CODE IS NOT NULL AND capacity_type_code=:CAPACITY_TYPE_CODE))