SELECT partition_id, to_char(user_id_a) user_id_a, serial_number_a, to_char(user_id_b) user_id_b, serial_number_b, relation_type_code, role_type_code, role_code_a, role_code_b, orderno, short_code, inst_id, to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date, update_time, update_staff_id, update_depart_id, remark, rsrv_num1, rsrv_num2, rsrv_num3, rsrv_num4, rsrv_num5, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3
  FROM tf_f_relation_uu
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND RELATION_TYPE_CODE=:RELATION_TYPE_CODE
   AND end_date > SYSDATE
   AND user_id_b <> TO_NUMBER(:USER_ID_B)