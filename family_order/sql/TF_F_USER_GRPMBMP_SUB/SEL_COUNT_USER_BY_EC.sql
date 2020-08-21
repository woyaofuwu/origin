SELECT 0 partition_id,0 user_id,0 serial_number,'' biz_code,'' biz_name,sysdate start_date,sysdate end_date,0 ec_user_id,0 ec_serial_number,

count(*) rsrv_num1,0 rsrv_num2,0 rsrv_num3,0 rsrv_num4,0 rsrv_num5,'' rsrv_str1,'' rsrv_str2,'' rsrv_str3,'' rsrv_str4,

'' rsrv_str5,SYSDATE rsrv_date1,SYSDATE rsrv_date2,SYSDATE rsrv_date3,'' remark,'' update_staff_id,'' update_depart_id,

SYSDATE update_time 

  FROM tf_f_user_grpmbmp_sub

 WHERE ec_user_id=TO_NUMBER(:EC_USER_ID)

   and rsrv_str1=:BIZ_CODE

   AND SYSDATE BETWEEN start_date AND end_date