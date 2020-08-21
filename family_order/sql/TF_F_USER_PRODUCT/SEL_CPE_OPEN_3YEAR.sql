 select a.user_id,a.cust_id,a.serial_number,a.open_date from tf_F_user a,tf_f_user_product b,tf_f_user_other c
 where a.user_id=b.user_id
 and a.user_id=c.user_id
 and a.remove_tag='0'
 and b.brand_code='CPE1'
 and b.product_id='10001001'
 and add_months(a.open_date, 36) <= sysdate
 and c.rsrv_value_code='CPE_DEVICE'
 and c.rsrv_str5 is null
 and sysdate < c.end_date
 and c.start_date < c.end_date