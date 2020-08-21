SELECT partition_id, user_id, acct_id, payitem_code, acct_priority, user_priority, addup_months, addup_method, bind_type, default_tag, act_tag, limit_type, limit, complement_tag, inst_id, start_cycle_id, end_cycle_id, update_time, update_staff_id, update_depart_id, remark, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10  
  FROM tf_a_payrelation
 WHERE user_id=to_number(:USER_ID)
   AND partition_id=MOD(to_number(:USER_ID),10000)
   AND default_tag='1'
   AND act_tag='1'
   AND end_cycle_id > to_number(to_char(sysdate, 'yyyymmdd'))