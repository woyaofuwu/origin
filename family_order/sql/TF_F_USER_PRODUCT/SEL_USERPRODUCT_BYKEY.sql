SELECT partition_id,
       to_char(user_id) user_id,
       to_char(user_id_a) user_id_a,
       product_id,
       product_mode,
       brand_code,
       to_char(inst_id) inst_id,
       to_char(campn_id) campn_id,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       update_staff_id,
       update_depart_id,
       remark,
       rsrv_num1,
       rsrv_num2,
       rsrv_num3,
       to_char(rsrv_num4) rsrv_num4,
       to_char(rsrv_num5) rsrv_num5,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       to_char(rsrv_date1, 'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(rsrv_date2, 'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
       to_char(rsrv_date3, 'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
       rsrv_tag1,
       rsrv_tag2,
       rsrv_tag3
  FROM tf_f_user_product t
 where t.user_id = :USER_ID
   and t.partition_id = mod(to_number(:USER_ID),10000)
   and t.user_id_a = :USER_ID_A
   and t.product_id = :PRODUCT_ID
   and sysdate between start_date and end_date