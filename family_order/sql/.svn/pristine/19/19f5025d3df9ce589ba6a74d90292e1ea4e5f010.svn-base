SELECT TO_CHAR(a.batch_task_id) batch_task_id,
       TO_CHAR(a.batch_id) batch_id,
       a.batch_oper_type,
       TO_CHAR(a.accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,
       a.staff_id,
       a.depart_id,
       a.city_code,
       a.eparchy_code,
       a.term_ip,
       a.in_mode_code,
       a.batch_count,
	   a.remove_tag,
       a.active_flag,
       TO_CHAR(a.active_time,'YYYY-MM-DD HH24:MI:SS') active_time,
       a.audit_state,
       a.audit_remark,
	   TO_CHAR(a.audit_date,'YYYY-MM-DD HH24:MI:SS') audit_date,
       a.audit_staff_id,
       a.audit_depart_id,
       a.audit_info
  FROM tf_b_trade_bat a
 WHERE a.accept_date >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND a.accept_date <  TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')+1
   AND a.staff_id >= :X_START_STAFF_ID
   AND a.staff_id <= :X_END_STAFF_ID
   AND (a.batch_oper_type = :BATCH_OPER_TYPE OR :BATCH_OPER_TYPE IS NULL)