SELECT b.serial_number,c.cust_name,f_sys_getcodename('brand_code',b.brand_code,NULL,NULL) brand,
       f_sys_getcodename('city_code',b.city_code,NULL,NULL) city_name,a.serv_para1 rsrv_str1,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       f_sys_getcodename('class_id',d.vip_type_code,d.class_id,NULL) vip_type,' ' remark
FROM tf_f_user_svc a,tf_f_user b,tf_f_customer c,tf_f_cust_vip d
WHERE a.user_id=b.user_id AND b.cust_id=c.cust_id AND b.user_id=d.user_id(+)
      AND a.service_id=199 AND (d.remove_tag='0' OR d.remove_tag IS NULL) 
      AND (b.serial_number=:SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
      AND b.city_code LIKE '%'||:CITY_CODE||'%'
      AND (a.serv_para1=:UPDATE_STAFF_ID OR :UPDATE_STAFF_ID IS NULL)
      AND a.start_date>=to_date(:START_DATE,'yyyy-mm-dd')
      AND a.end_date<to_date(:END_DATE,'yyyy-mm-dd')