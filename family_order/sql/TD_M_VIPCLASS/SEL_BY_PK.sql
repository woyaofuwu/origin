--IS_CACHE=Y
SELECT vip_type_code,class_id,class_name,priority,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,0 x_tag 
  FROM td_m_vipclass
 WHERE vip_type_code like '%'||:VIP_TYPE_CODE||'%'
   AND class_id like '%'||:CLASS_ID||'%'