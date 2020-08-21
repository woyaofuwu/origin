select a.serial_number para_code1,a.rsrv_str2 para_code2,a.cust_name para_code3,a.trade_staff_id para_code4,a.accept_date para_code5,a.remark para_code6,
'' para_code7,
'' para_code8,
'' para_code9,
'' para_code10,
'' para_code11,
'' para_code12,
'' para_code13,
'' para_code14,
'' para_code15,
'' para_code16,
'' para_code17,
'' para_code18,
'' para_code19,
'' para_code20,
'' para_code21,
'' para_code22,
'' para_code23,
'' para_code24,
'' para_code25,
'' para_code26,
'' para_code27,
'' para_code28,
'' para_code29,
'' start_date,
'' end_date,
'' eparchy_code,
'' remark,
'' update_staff_id,
'' update_depart_id,
'' update_time,
'' subsys_code,
0 param_attr,
'' param_code,
'' param_name from tf_bh_trade a where 
a.accept_date BETWEEN  TO_DATE(:PARA_CODE1,'YYYYMMDD') AND  TO_DATE(:PARA_CODE2,'YYYYMMDD')+1
and a.serial_number=:PARA_CODE3
AND :PARA_CODE4 IS NULL
AND :PARA_CODE5 IS NULL
AND :PARA_CODE6 IS NULL
AND :PARA_CODE7 IS NULL
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL