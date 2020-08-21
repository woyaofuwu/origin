SELECT a.partition_id,a.user_id,a.serial_number,a.biz_code,a.biz_name,a.start_date,a.end_date,a.ec_user_id,a.ec_serial_number,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,a.remark,a.update_staff_id,a.update_depart_id,a.update_time
  FROM tf_f_user_grpmbmp_sub a
 WHERE a.partition_id =MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id =TO_NUMBER(:USER_ID)
   AND SYSDATE BETWEEN a.start_date AND a.end_date
   AND EXISTS(
             SELECT 1 FROM td_s_commpara b 
              WHERE b.subsys_code='CSM'
                AND b.param_attr=910
                AND b.para_code1 =:PARA_CODE1
                AND b.para_code2 =:PARA_CODE2
                AND b.para_code3=a.rsrv_str1
                AND sysdate BETWEEN b.start_date AND b.end_date)