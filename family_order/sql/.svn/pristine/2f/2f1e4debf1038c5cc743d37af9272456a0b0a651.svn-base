--IS_CACHE=Y
SELECT a.depart_id,a.depart_code,a.depart_name,a.depart_kind_code,a.depart_frame,a.validflag,a.area_code,a.parent_depart_id,a.manager_id,a.order_no,a.user_depart_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,a.depart_level,a.remark,a.rsvalue1,a.rsvalue2,a.rsvalue3,a.rsvalue4,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,a.update_staff_id,a.update_depart_id,b.rsvalue1 rsrv_str1 
  FROM td_m_depart a,td_m_staff b
 WHERE a.depart_id=b.depart_id and b.staff_id=:STAFF_ID
and (:VALIDFLAG is null or a.VALIDFLAG=:VALIDFLAG)
and (:DIMISSION_TAG is null or b.DIMISSION_TAG=:DIMISSION_TAG)