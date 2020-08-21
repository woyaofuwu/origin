--IS_CACHE=Y
SELECT mgmt_district eparchy_code,hlr_code switch_id,hlr_type_code switch_type_code,hlr_name switch_name,remarks remark,to_char(done_time,'yyyy-mm-dd hh24:mi:ss') update_time,
op_id update_staff_id,org_id update_depart_id,0 x_tag 
  FROM res_hlr
 WHERE (mgmt_district=:EPARCHY_CODE or mgmt_district='ZZZZ')
   AND ((:SWITCH_ID IS NOT NULL AND hlr_code=:SWITCH_ID) OR :SWITCH_ID IS NULL)