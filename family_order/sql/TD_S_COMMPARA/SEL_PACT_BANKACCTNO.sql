SELECT a.serial_number_b para_code1,TO_CHAR(a.start_date,'yyyy-mm-dd hh24:mi:ss') para_code2,
'' para_code3,'' para_code4,F_SYS_GETCODENAME('pay_mode_code',e.pay_mode_code,:PARA_CODE2,NULL) para_code5,
c.rsrv_str1 para_code6,e.rsrv_str2 para_code7,
a.user_id_b para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_f_user c,tf_f_relation_uu a,
(SELECT DISTINCT b.contract_no contract_no,b.pay_mode_code pay_mode_code,b.rsrv_str2 rsrv_str2 FROM tf_f_account b
WHERE b.remove_tag='0' AND b.bank_acct_no=:PARA_CODE1
)e
WHERE c.product_id='7000'
AND c.remove_tag='0'
AND c.rsrv_str1=e.contract_no
AND c.user_id=a.user_id_a
AND a.relation_type_code='70'
AND SYSDATE BETWEEN a.start_date  AND a.end_date
AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)