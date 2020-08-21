INSERT INTO tf_fh_user_grpmbmp_sub(partition_id,user_id,serial_number,biz_code,biz_name,start_date,end_date,ec_user_id,ec_serial_number,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,update_depart_id,update_time)

SELECT

a.partition_id,a.user_id,a.serial_number,a.biz_code,a.biz_name,a.start_date,a.end_date,a.ec_user_id,a.ec_serial_number,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,a.remark,a.update_staff_id,a.update_depart_id,a.update_time

 FROM tf_f_user_grpmbmp_sub a

where a.user_id=to_number(:USER_ID)

  and a.end_date>sysdate