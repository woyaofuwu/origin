SELECT a.partition_id,to_char(a.user_id) user_id,a.serial_number,a.biz_code,
       a.sp_code,a.product_no,a.biz_type_code,a.org_domain,a.opr_source,a.biz_state_code,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(a.first_date,'yyyy-mm-dd hh24:mi:ss') first_date,to_char(a.first_date_mon,'yyyy-mm-dd hh24:mi:ss') first_date_mon,
       a.gift_serial_number,a.gift_user_id,a.bill_type,a.price,to_char(a.subscribe_id) subscribe_id,
       a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,to_char(a.rsrv_num4) rsrv_num4,to_char(a.rsrv_num5) rsrv_num5,
       a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,
       a.rsrv_str9,a.rsrv_str10,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
       to_char(a.rsrv_date4,'yyyy-mm-dd hh24:mi:ss') rsrv_date4,to_char(a.rsrv_date5,'yyyy-mm-dd hh24:mi:ss') rsrv_date5,
       a.remark,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
       b.sp_name rsrv_str1,c.biz_name rsrv_str2
  FROM tf_f_user_plat_order a,td_m_corporation_sp b,td_m_operation_sp c
 WHERE a.partition_id=mod(to_number(:USER_ID),10000)
   AND a.user_id=to_number(:USER_ID)
   AND a.biz_code = c.biz_code
   AND a.sp_code = c.sp_code
   AND a.sp_code = b.sp_code
   AND a.end_date>=sysdate