--IS_CACHE=Y
SELECT update_id,update_dll_name,update_interval,update_pri,update_tag,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,eparchy_code 
  FROM td_m_backupdatetask
 WHERE eparchy_code=:EPARCHY_CODE
  AND  update_tag='1'
order by update_pri