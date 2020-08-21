--IS_CACHE=Y
SELECT
um.mod_code, um.mod_size, um.mod_version, um.mod_buildno,
um.UPDATE_METHOD,
um.must_update_tag,
um.reg_update_tag,
um.mod_path,
um.del_tag,
um.remark,
to_char(um.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
um.update_staff_id,
um.update_depart_id,
mf.mod_name,
mf.mod_type
  FROM td_m_updatemod um, td_s_modfile mf
 WHERE um.mod_code = mf.mod_code
   AND mf.mod_type <> '1'