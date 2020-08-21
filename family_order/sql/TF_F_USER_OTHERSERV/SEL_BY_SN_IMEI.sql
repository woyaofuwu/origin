SELECT a.partition_id,to_char(a.user_id) user_id,a.service_mode,a.serial_number,a.process_info,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_str1,b.imei rsrv_str2,b.purchase_info  rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,a.process_tag,a.staff_id,a.depart_id,to_char(trunc(a.start_date-1),'yyyy-mm-dd hh24:mi:ss') start_date,to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,a.remark 
  FROM tf_f_user_otherserv a,tf_f_user_purchase b
 WHERE a.service_mode=:SERVICE_MODE
   AND a.serial_number=:SERIAL_NUMBER
   AND b.serial_number=:SERIAL_NUMBER
   AND a.serial_number=b.serial_number
   AND a.user_id=b.user_id
   AND b.process_tag='0'