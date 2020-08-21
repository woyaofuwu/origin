SELECT b.vip_id,b.serial_number,b.vip_no,a.cust_name,a.phone,
a.post_address,b.open_date,b.open_depart_id,b.open_staff_id,c.vip_manager_name,
a.contact,a.contact_phone,c.link_phone,b.vip_type_code2,b.class_id2,
g.depart_id,d.sim_card_no,d.imsi,d.sim_type_code,d.act_tag,
b.vip_type_code,b.class_id,b.city_code
FROM tf_f_cust_person a,tf_f_cust_vip b,tf_f_managerstaff c,tf_f_cust_vipsimbak d,
td_m_viptype e,td_m_vipclass f,td_m_staff g
WHERE a.cust_id=b.usecust_id and b.cust_manager_id=c.vip_manager_id(+)
and b.vip_id=d.vip_id(+) and b.vip_type_code=e.vip_type_code
and b.vip_type_code=f.vip_type_code and b.class_id=f.class_id
and b.cust_manager_id=g.staff_id(+)
and b.serial_number=:SERIAL_NUMBER and  b.remove_tag=:REMOVE_TAG