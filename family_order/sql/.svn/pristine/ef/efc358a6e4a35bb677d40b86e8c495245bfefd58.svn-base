SELECT  a.serial_number PARA_CODE1,
b.cust_name PARA_CODE2,
TRIM(TO_CHAR(a.score_value,'99999990.00')) PARA_CODE3,
TO_CHAR(a.open_date,'YYYY-MM-DD HH24:MI:SS') PARA_CODE4,
 '' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_f_user a,tf_f_customer b
WHERE a.cust_id=b.cust_id
AND TRIM(a.open_date) BETWEEN TO_DATE(:PARA_CODE3,'YYYYMMDD') AND TO_DATE(:PARA_CODE4,'YYYYMMDD')+1
AND TRIM(a.city_code)=:PARA_CODE7
AND a.score_value BETWEEN TO_NUMBER(:PARA_CODE5) AND TO_NUMBER(:PARA_CODE6)
AND a.serial_number BETWEEN :PARA_CODE1 AND :PARA_CODE2
AND a.remove_tag='0'
AND a.open_mode!='1'
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL