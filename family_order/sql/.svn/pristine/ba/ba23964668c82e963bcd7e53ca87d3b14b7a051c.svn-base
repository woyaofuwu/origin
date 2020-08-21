SELECT c.vip_id,c.usecust_name cust_name,b.sex,
    decode(to_char( b.birthday , 'mmdd'),to_char(sysdate, 'mmdd'),1,0) x_tag,
    b.pspt_type_code,b.pspt_id,b.cust_id,
    b.marriage,b.educate_degree_code,d.vip_manager_name||'['||
    f_sys_getcodename('depart_id',s.depart_id,NULL,null)||']' vip_manager_name,
    to_char(a.serial_number)  client_info1,b.contact_phone,b.post_address,c.cust_manager_id,c.vip_no,
    a.eparchy_code,'V' user_type_code,c.vip_type_code,c.class_id,a.brand_code,
    to_char(b.birthday , 'yyyy-mm-dd hh24:mi:ss') birthday,a.score_value,
    to_char(c.open_date , 'yyyy-mm-dd hh24:mi:ss') open_date,d.serial_number ,
    f_sys_getcodename('class_id',c.vip_type_code,c.class_id,null) client_info3,
    f_sys_getcodename('vip_type_code',c.vip_type_code,NULL,null) client_info2
 FROM tf_f_user a,tf_f_cust_person b,tf_f_cust_vip c,tf_f_managerstaff d,td_m_staff s
 where a.usecust_id=b.cust_id
  AND b.partition_id=MOD(a.usecust_id,10000)
      and a.user_id=c.user_id
      and c.cust_manager_id = d.vip_manager_id(+)
      and d.vip_manager_id = s.staff_id(+)
      and c.remove_tag=:REMOVE_TAG
      and a.serial_number=:SERIAL_NUMBER
union all
SELECT c.vip_id,c.usecust_name cust_name,b.sex,
    decode(to_char( b.birthday , 'mmdd'),to_char(sysdate, 'mmdd'),1,0) x_tag,
    b.pspt_type_code,b.pspt_id,b.cust_id,
    b.marriage,b.educate_degree_code,d.vip_manager_name||'['||
    f_sys_getcodename('depart_id',s.depart_id,NULL,null)||']' vip_manager_name,
    to_char(a.serial_number) client_info1,b.contact_phone,b.post_address,c.cust_manager_id,c.vip_no,
    a.eparchy_code,'V' user_type_code,c.vip_type_code,c.class_id,a.brand_code,
    to_char(b.birthday , 'yyyy-mm-dd hh24:mi:ss') birthday,a.score_value,
    to_char(c.open_date , 'yyyy-mm-dd hh24:mi:ss') open_date,d.serial_number ,
    '[中高端大客户]'||f_sys_getcodename('class_id',c.vip_type_code,c.class_id,null) client_info3,
    f_sys_getcodename('vip_type_code',c.vip_type_code,NULL,null) client_info2
 FROM tf_f_user a,tf_f_cust_person b,tf_f_cust_vip_selfdef c,tf_f_managerstaff d,td_m_staff s
 where a.usecust_id=b.cust_id
  AND b.partition_id=MOD(a.usecust_id,10000)
      and a.user_id=c.user_id
      and c.cust_manager_id = d.vip_manager_id(+)
      and d.vip_manager_id = s.staff_id(+)
      and c.remove_tag=:REMOVE_TAG
      and a.serial_number=:SERIAL_NUMBER