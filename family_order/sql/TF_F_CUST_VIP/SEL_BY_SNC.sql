SELECT c.vip_id,b.cust_name,c.vip_no,a.serial_number,a.eparchy_code,'V' user_type_code,
c.vip_type_code2,c.class_id2,c.cust_manager_id,a.brand_code,b.birthday,decode(to_char(b.birthday,'mmdd'),to_char(sysdate,'mmdd'),1,0) x_tag,c.vip_type_code,c.class_id
FROM tf_f_user a,tf_f_cust_person b,tf_f_cust_vip c
where a.usecust_id=b.cust_id and a.user_id=c.user_id and c.remove_tag=:REMOVE_TAG and a.serial_number=:SERIAL_NUMBER