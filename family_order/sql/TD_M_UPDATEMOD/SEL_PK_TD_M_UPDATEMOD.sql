--IS_CACHE=Y
SELECT mod_code,mod_version,mod_buildno,mod_size,update_method,must_update_tag,reg_update_tag,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_updatemod
 WHERE mod_code=:MOD_CODE