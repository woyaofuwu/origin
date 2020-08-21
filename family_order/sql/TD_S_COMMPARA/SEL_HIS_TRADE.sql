SELECT to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') para_code1,remark para_code2,serial_number para_code3,
cust_name para_code4,
F_SYS_GETCODENAME('brand_code',brand_code,NULL,NULL) para_code5,
F_SYS_GETCODENAME('STAFF_ID',trade_staff_id,NULL,NULL) para_code6,
F_SYS_GETCODENAME('DEPART_ID',TRADE_DEPART_ID,NULL,NULL) para_code7,
'' para_code8,'' para_code9,'' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_bh_trade
 WHERE trade_type_code=TO_NUMBER(:PARA_CODE1)
and serial_number=:PARA_CODE2
and accept_date >= TRUNC(TO_DATE(:PARA_CODE3, 'YYYYMMDD'))
and accept_date <TRUNC(TO_DATE(:PARA_CODE4, 'YYYYMMDD'))+1
AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)