--IS_CACHE=Y
SELECT vip_type_code,class_id,class_name,priority,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_vipclass
 WHERE vip_type_code=:VIP_TYPE_CODE