--IS_CACHE=Y
SELECT eparchy_code,action_code,action_name,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_goodchange_type
WHERE (EPARCHY_CODE='ZZZZ' OR EPARCHY_CODE=:EPARCHY_CODE)
  AND END_DATE>SYSDATE