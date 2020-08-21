SELECT t.trade_id para_code1,t.user_id para_code2,
(SELECT nvl(cust_name,' ') FROM tf_f_customer WHERE cust_id=(SELECT cust_id FROM tf_f_user WHERE user_id=t.user_id)) para_code3,
decode(t.trade_type_code,1025,'集团成员新增',1028,'集团成员修改',1029,'集团成员注销') para_code4,t.rsrv_str3 para_code5,
(SELECT nvl(cust_name,' ') FROM tf_f_customer WHERE cust_id=(SELECT cust_id FROM tf_f_user WHERE user_id=v.user_id)) para_code6,
decode(v.scp_code,'00','虚拟集团','智能网集团') para_code7,decode(v.scp_code,'00','虚拟SCP','01','华为SCP','东信SCP') para_code8,r.short_code para_code9,o.rsrv_str1 para_code10,
nvl(to_char(to_number(o.rsrv_str2)/100.00),'0') para_code11,o.rsrv_str8 para_code12,nvl(to_char(to_number(o.rsrv_str3)/100.00),'0') para_code13,t.trade_staff_id para_code14,
t.accept_date start_date,t.finish_date end_date,
t.exec_time para_code15,'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,
'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_bh_trade t, tf_b_trade_relation r,tf_f_user_vpmn v,tf_b_trade_other o
WHERE t.trade_id=r.trade_id AND t.rsrv_str1=v.user_id AND t.trade_id=o.trade_id(+)
AND t.accept_date BETWEEN to_date(:PARA_CODE2,'yyyy-mm-dd hh24:mi:ss') AND to_date(:PARA_CODE3,'yyyy-mm-dd hh24:mi:ss')
AND t.trade_type_code IN (1025,1028,1029)
AND t.serial_number=:PARA_CODE1
UNION ALL
SELECT t.trade_id para_code1,t.user_id para_code2,
(SELECT nvl(cust_name,' ') FROM tf_f_customer WHERE cust_id=(SELECT cust_id FROM tf_f_user WHERE user_id=t.user_id)) para_code3,
decode(t.trade_type_code,1025,'集团成员新增',1028,'集团成员修改',1029,'集团成员注销') para_code4,t.rsrv_str3 para_code5,
(SELECT nvl(cust_name,' ') FROM tf_f_customer WHERE cust_id=(SELECT cust_id FROM tf_f_user WHERE user_id=v.user_id)) para_code6,
decode(v.scp_code,'00','虚拟集团','智能网集团') para_code7,decode(v.scp_code,'00','虚拟SCP','01','华为SCP','东信SCP') para_code8,r.short_code para_code9,o.rsrv_str1 para_code10,
nvl(to_char(to_number(o.rsrv_str2)/100.00),'0') para_code11,o.rsrv_str8 para_code12,nvl(to_char(to_number(o.rsrv_str3)/100.00),'0') para_code13,t.trade_staff_id para_code14,
t.accept_date start_date,t.finish_date end_date,
t.exec_time para_code15,'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,
'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_b_trade t, tf_b_trade_relation r,tf_f_user_vpmn v,tf_b_trade_other o
WHERE t.trade_id=r.trade_id AND t.rsrv_str1=v.user_id AND t.trade_id=o.trade_id(+)
AND t.accept_date BETWEEN to_date(:PARA_CODE2,'yyyy-mm-dd hh24:mi:ss') AND to_date(:PARA_CODE3,'yyyy-mm-dd hh24:mi:ss')
AND t.trade_type_code IN (1025,1028,1029)
AND t.serial_number=:PARA_CODE1
AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)