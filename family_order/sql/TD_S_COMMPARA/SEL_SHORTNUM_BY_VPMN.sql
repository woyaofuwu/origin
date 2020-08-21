SELECT
A.short_code para_code1,
c.Vpn_Name para_code2,
A.serial_number_a para_code3,
'' para_code4,'' para_code5,'' para_code6,'' para_code7, '' para_code8,
'' para_code9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,
'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,
'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,
'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_f_relation_uu a,tf_f_user b,tf_f_user_vpn c
WHERE 
b.serial_number = :PARA_CODE1
AND LENGTH(A.short_code) = :PARA_CODE2
AND SUBSTR(A.short_code,2,:PARA_CODE3) = :PARA_CODE4
AND a.serial_number_a = b.serial_number
AND a.user_id_a = b.user_id
AND b.serial_number = c.Vpn_No
AND a.relation_type_code = '20'
AND a.end_date > sysdate
AND b.remove_tag = '0'
AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)