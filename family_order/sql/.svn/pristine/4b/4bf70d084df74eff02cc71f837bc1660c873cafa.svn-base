--IS_CACHE=Y
SELECT release_eparchy_code,design_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,res_type_code,res_kind_code,capacity_type_code,start_res_no,end_res_no,design_name,design_index,code_state_code,to_char(time_in,'yyyy-mm-dd hh24:mi:ss') time_in,staff_id_in,to_char(release_time,'yyyy-mm-dd hh24:mi:ss') release_time,release_staff_id,release_depart_id,remark,rsvalue1,rsvalue2,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_design
 WHERE (:RELEASE_EPARCHY_CODE is null or release_eparchy_code=:RELEASE_EPARCHY_CODE)
   AND design_code=:DESIGN_CODE
   AND (:START_DATE is null or start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND (:CODE_STATE_CODE is null or code_state_code=:CODE_STATE_CODE)