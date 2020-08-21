SELECT  c.vip_id,b.cust_name,b.sex,
        decode(to_char( b.birthday , 'mmdd'),to_char(sysdate, 'mmdd'),1,0) x_tag,
        b.pspt_type_code,b.pspt_id,a.cust_id,
        b.marriage,b.educate_degree_code,
	 a.serial_number,b.contact_phone,b.post_address,c.cust_manager_id,c.vip_no,
	 a.eparchy_code,'V' user_type_code,c.vip_type_code,c.class_id,a.brand_code
	 ,to_char(b.birthday , 'yyyy-mm-dd hh24:mi:ss') birthday,a.score_value,
         to_char(c.open_date , 'yyyy-mm-dd hh24:mi:ss') open_date,d.serial_number
FROM tf_f_user a,tf_f_cust_person b,tf_f_cust_vip c,tf_f_managerstaff d
where a.usecust_id=b.cust_id
      and a.user_id=c.user_id
      and c.cust_manager_id = d.vip_manager_id(+)
      and c.remove_tag=:REMOVE_TAG
      and c.vip_no=:VIP_NO