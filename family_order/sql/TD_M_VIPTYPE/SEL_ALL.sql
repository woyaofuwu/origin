--IS_CACHE=Y
SELECT vip_type_code,vip_type,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,0 x_tag
  FROM td_m_viptype