SELECT partition_id,
       to_char(user_id) user_id,
       to_char(acct_id) acct_id,
       payitem_code,
       acct_priority,
       user_priority,
       to_char(addup_months) addup_months,
       addup_method,
       bind_type,
       default_tag,
       act_tag,
       limit_type,
       to_char(limit) limit,
       complement_tag,
       to_char(inst_id) inst_id,
       start_cycle_id,
       end_cycle_id,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       update_staff_id,
       update_depart_id,
       remark,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       rsrv_str6,
       rsrv_str7,
       rsrv_str8,
       rsrv_str9,
       rsrv_str10
  FROM tf_a_payrelation a
 WHERE default_tag = '1'
   and TO_CHAR(SYSDATE, 'YYYYMMDD') between start_cycle_id and end_cycle_id
   and act_tag = '1'
   and a.user_id in  (select t.user_id
          from tf_f_user t
         where t.serial_number= :SERIAL_NUMBER
           AND remove_tag = '0')