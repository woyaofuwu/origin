INSERT INTO tf_f_user_grpmbmp_sub

            (partition_id,user_id,serial_number,biz_code,biz_name,start_date,end_date,ec_user_id,

             ec_serial_number,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,

             rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,

             update_depart_id,update_time)

      SELECT mod(to_number(user_id),10000),user_id,serial_number,biz_code,biz_name,sysdate,end_date,ec_user_id,

             ec_serial_number,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,

             rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,

             update_depart_id,sysdate

  FROM tf_b_trade_grpmbmp_sub

 WHERE trade_id=to_char(:TRADE_ID)

   and modify_tag <> '1'