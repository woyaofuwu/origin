SELECT /*+ use_nl(a,b,c,d)*/ b.serial_number para_code1,a.acct_id para_code2,a.acct_id_b para_code3,
a.payitem_code para_code4, f_sys_getcodename('payitem_code',a.PAYITEM_CODE,b.EPARCHY_CODE,NULL) para_code5, a.start_cycle_id para_code6, 
a.end_cycle_id para_code7, a.update_staff_id para_code8, a.update_time para_code9, decode(a.limit_type,'0','全部费用','1','部分费用','2','限定比例') para_code10,
a.limit/100 para_code11,c.vpn_no para_code12,c.vpn_name para_code13,d.cust_name para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_f_user_specialepay a,tf_f_user b,tf_f_user_vpn c,tf_f_customer d
WHERE a.user_id = b.user_id
AND a.partition_id = b.partition_id
AND b.remove_tag = '0'
AND a.user_id_a = c.user_id
AND b.cust_id = d.cust_id
AND b.serial_number = :PARA_CODE2
AND (c.remove_date > sysdate or c.remove_date is null)
AND (:PARA_CODE1 IS NOT NULL OR :PARA_CODE1 IS NULL)
AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)