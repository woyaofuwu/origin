--IS_CACHE=Y
SELECT staff_id,term_ip,machine_ip,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM tf_m_selfstaffbindip
 WHERE staff_id=:STAFF_ID
   AND term_ip=:TERM_IP
   AND machine_ip=:MACHINE_IP