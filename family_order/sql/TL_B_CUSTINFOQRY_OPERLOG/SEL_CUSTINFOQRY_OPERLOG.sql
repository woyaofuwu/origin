SELECT (decode(a.check_mode,'0',decode(a.audit_flag,'0','false','true'),'true')) BOOL_DISABLED,oper_id, accept_month, serial_number, user_id, cust_name, query_mode, check_mode, query_cause, audit_flag, to_char(query_time,'yyyy-mm-dd hh24:mi:ss') query_time, query_staff_id, query_depart_id, to_char(audit_time,'yyyy-mm-dd hh24:mi:ss') audit_time, audit_staff_id, audit_depart_id, subsys_code, remark, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3
FROM tl_b_custinfoqry_operlog a
WHERE 1=1
   AND a.query_staff_id BETWEEN :START_STAFF_ID AND :END_STAFF_ID
   AND a.query_time BETWEEN to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') AND to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND a.query_mode = :QUERY_MODE
   AND a.audit_flag = :AUDIT_FLAG
   AND a.check_mode = :CHECK_MODE
ORDER BY a.query_time DESC