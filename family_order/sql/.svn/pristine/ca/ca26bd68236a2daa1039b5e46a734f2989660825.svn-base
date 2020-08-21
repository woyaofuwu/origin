SELECT a.vpmn_group_id,a.vpmn_group_name,a.max_users,f_sys_getcodename('discnt_code',a.discnt_code,NULL,NULL) discnt_name,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date
FROM tf_f_vpmn_group a,tf_f_user_vpn b 
WHERE a.user_id=b.user_id AND b.vpn_no=:SERIAL_NUMBER AND SYSDATE BETWEEN a.start_date AND a.end_date