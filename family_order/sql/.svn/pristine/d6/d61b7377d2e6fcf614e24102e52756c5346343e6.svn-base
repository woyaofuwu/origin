SELECT DISTINCT partition_id,
                to_char(user_id) user_id,
                to_char(user_id_a) user_id_a,
                service_id,
                main_tag,
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
                rsrv_num4,
                rsrv_num5,
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
  FROM tf_f_user_svc
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND campn_id = TO_NUMBER(:CAMPN_ID)
   AND SYSDATE BETWEEN start_date + 0 AND end_date + 0
   AND (rsrv_str1 = TO_CHAR(:RELATION_TRADE_ID) or rsrv_str1 is null)
