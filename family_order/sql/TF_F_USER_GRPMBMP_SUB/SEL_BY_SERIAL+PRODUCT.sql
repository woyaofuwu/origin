SELECT a.partition_id,to_char(a.user_id) user_id,to_char(a.serial_number) serial_number,a.biz_code,a.biz_name,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(a.ec_user_id) ec_user_id,to_char(a.ec_serial_number) ec_serial_number,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,to_char(a.rsrv_num4) rsrv_num4,to_char(a.rsrv_num5) rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,a.remark,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time 

  FROM tf_f_user_grpmbmp_sub a,tf_f_user b

 WHERE b.serial_number=:SERIAL_NUMBER

   AND b.product_id=:PRODUCT_ID

   AND b.remove_tag = '0'

   AND b.user_id=a.user_id

   and a.end_date>SYSDATE