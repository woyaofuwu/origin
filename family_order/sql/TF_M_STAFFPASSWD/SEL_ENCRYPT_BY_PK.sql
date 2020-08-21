--IS_CACHE=Y
SELECT staff_id,staff_passwd,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id, f_csb_encrypt(:PASSWD,'00linkage')  PASSWD
  FROM tf_m_staffpasswd
 WHERE staff_id=:STAFF_ID