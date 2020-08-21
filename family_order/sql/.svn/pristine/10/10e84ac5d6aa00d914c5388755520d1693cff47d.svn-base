--IS_CACHE=Y
SELECT staff_id,external_sys_type,use_staff_id,staff_passwd,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_m_staff_id_relation
 WHERE staff_id=:STAFF_ID
   AND external_sys_type=:EXTERNAL_SYS_TYPE