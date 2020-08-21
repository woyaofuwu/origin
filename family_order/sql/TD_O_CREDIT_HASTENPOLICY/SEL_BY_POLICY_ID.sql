--IS_CACHE=Y
SELECT hasten_policy_id,hasten_policy_name,hasten_count,interval,hasten_mode,valid_time_id,hasten_temp_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,eparchy_code 
  FROM td_o_credit_hastenpolicy
 WHERE hasten_policy_id=:HASTEN_POLICY_ID