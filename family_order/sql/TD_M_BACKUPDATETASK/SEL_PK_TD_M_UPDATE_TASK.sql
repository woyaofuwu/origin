--IS_CACHE=Y
SELECT update_id,update_dll_name,update_interval,update_pri,update_tag,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_backupdatetask
 WHERE update_tag='1'