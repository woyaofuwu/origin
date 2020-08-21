SELECT TO_CHAR(a.trade_id) para_code1,
audit_batch_no para_code2,
serial_number para_code3,
a.cust_name para_code4,
F_SYS_GETCODENAME('trade_type_code',trade_type_code,:PARA_CODE5,NULL) para_code5,
trade_staff_id para_code6,
TO_CHAR(accept_date,'YYYY-MM-DD HH24:MI:SS') para_code7,
audit_staff_id para_code8,
TO_CHAR(audit_date,'YYYY-MM-DD HH24:MI:SS') para_code9,
b.remark para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_bh_trade a,tf_b_trade_audit b
WHERE a.trade_id=b.trade_id
AND a.cancel_tag IN ('0','1')
AND a.serial_number LIKE NVL(:PARA_CODE1,'%')
AND b.audit_batch_no LIKE NVL(:PARA_CODE2,'%')
AND accept_date BETWEEN TO_DATE(:PARA_CODE3,'YYYYMMDD') and TO_DATE(:PARA_CODE4,'YYYYMMDD')+1
AND :PARA_CODE6 IS NULL
AND :PARA_CODE7 IS NULL
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL