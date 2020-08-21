SELECT 0 accept_month,'' cust_contact_id,'' cust_id,'' cust_name,'' pspt_type_code,'' pspt_id,'' in_mode_code,'' serial_number,'' user_id,0 product_id,'' staff_id,'' depart_id,'' start_time,'' finish_time,'' contact_desc,'' city_code,'' eparchy_code,nvl(c.vip_type,'普通用户') rsrv_str1,nvl(d.class_name,'普通用户') rsrv_str2,f.in_mode rsrv_str3,g.area_name rsrv_str4,e.count rsrv_str5,e.avg_time rsrv_str6,'' rsrv_str7,'' rsrv_str8,'' rsrv_str9,'' rsrv_str10,'' remark,'' language_code,'' access_code,'' contact_mode,'' parent_contact_id,'' sub_in_mode_code,'' media_type_code,'' callcenter,'' device_id,'' usereparchycode,'' vip_class,'' brand,'' cust_state,'' contact,'' contact_phone,'' post_address,'' post_code,'' email,'' fax_nbx,'' contact_type_code 
  FROM td_m_viptype c,td_m_vipclass d,(
SELECT b.vip_type_code,b.class_id,a.in_mode_code,a.eparchy_code,COUNT(1) COUNT,AVG(nvl(a.finish_time,SYSDATE)-a.start_time)*24*3600 avg_time
  FROM tf_b_cust_contact a,tf_f_cust_vip b
 WHERE a.start_time between to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') and to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
    AND b.usecust_id(+) = a.cust_id
GROUP BY b.vip_type_code,b.class_id,a.in_mode_code,a.eparchy_code) e,td_s_inmode f,td_m_area g
WHERE e.vip_type_code=c.vip_type_code(+)
   AND e.vip_type_code=d.vip_type_code(+)
   AND e.class_id=d.class_id(+)
   AND e.in_mode_code=f.in_mode_code
   AND e.eparchy_code=f.eparchy_code
   AND SYSDATE BETWEEN f.start_date AND f.end_date
   AND g.area_code=e.eparchy_code
   AND g.area_level=20
   AND SYSDATE BETWEEN g.start_date AND g.end_date
   AND g.use_tag='1'