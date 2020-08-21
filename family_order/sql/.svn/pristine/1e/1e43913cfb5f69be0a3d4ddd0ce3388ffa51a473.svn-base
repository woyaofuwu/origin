SELECT b.vip_id,b.serial_number,b.vip_no,d.end_date,a.cust_name,a.phone,
a.post_address,b.open_date,b.open_depart_id,b.open_staff_id,c.vip_manager_name,
a.contact,a.contact_phone,c.serial_number link_phone,decode(nvl(c.serial_number,-1),'-1',-1,0) priority
FROM tf_f_cust_person a,tf_f_cust_vip b,tf_f_managerstaff c,tf_f_vipcard d
WHERE a.cust_id=b.usecust_id and b.cust_manager_id=c.vip_manager_id(+) 
and b.vip_no=d.vip_no(+) 
and b.serial_number=:SERIAL_NUMBER and  b.remove_tag=:REMOVE_TAG