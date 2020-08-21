--IS_CACHE=Y
SELECT eparchy_code,switch_type_code,switch_type,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,0 x_tag 
  FROM td_s_switchtype
 WHERE eparchy_code=:EPARCHY_CODE
   AND (:SWITCH_TYPE_CODE IS NULL OR (:SWITCH_TYPE_CODE IS NOT NULL AND switch_type_code=:SWITCH_TYPE_CODE))