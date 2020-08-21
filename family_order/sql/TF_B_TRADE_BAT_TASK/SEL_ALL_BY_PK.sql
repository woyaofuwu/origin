SELECT TO_CHAR(a.batch_task_id) batch_task_id,
       a.batch_task_name,       
       a.batch_oper_code,
       a.batch_oper_name,
       TO_CHAR(a.start_date,'YYYY-MM-DD HH24:MI:SS') start_date,
       TO_CHAR(a.end_date,'YYYY-MM-DD HH24:MI:SS') end_date,
       TO_CHAR(a.create_time,'YYYY-MM-DD HH24:MI:SS') create_time,
       a.create_staff_id,
       a.create_depart_id,
       a.create_city_code,
       a.create_eparchy_code,
       a.sms_flag,
       a.audit_no,
       a.remark,
       a.coding_str1,a.coding_str2,a.coding_str3,a.coding_str4,a.coding_str5,
	   a.condition1,a.condition2,a.condition3,a.condition4,a.condition5
  FROM tf_b_trade_bat_task a
 WHERE a.batch_task_id = TO_NUMBER(:BATCH_TASK_ID)
   and a.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_TASK_ID, 5, 2))