SELECT serial_number para_code1,
carrier_id para_code2,
TO_CHAR(fee/100) para_code3,
TO_CHAR(accept_time,'YYYY-MM-DD HH24:MI:SS') para_code4,
DECODE(cancel_tag,'0','正常','返销') para_code5,
cancel_depart_code para_code6,
cancel_staff_id para_code7,
TO_DATE(cancel_time,'YYYY-MM-DD HH24:MI:SS') para_code8,
'' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tl_b_tradelog a,tf_f_user b
WHERE  carrier_code=12
AND carrier_id=:PARA_CODE1
AND b.partition_id = mod(a.user_id,10000)
AND b.user_id=a.user_id
--AND a.eparchy_code=:PARA_CODE2
AND :PARA_CODE2 IS NOT NULL
AND :PARA_CODE3 IS NULL
AND :PARA_CODE4 IS NULL
AND :PARA_CODE5 IS NULL
AND :PARA_CODE6 IS NULL
AND :PARA_CODE7 IS NULL
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL