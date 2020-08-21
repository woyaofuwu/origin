SELECT b.trade_type para_code1,a.cust_name para_code2,a.serial_number para_code3,
to_char(a.accept_date,'YYYY-MM-DD HH24:MI') para_code4,COUNT(a.trade_id) para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_bh_trade a ,td_s_tradetype b
 WHERE a.trade_type_code=b.trade_type_code
   AND a.trade_staff_id BETWEEN :PARA_CODE1 AND :PARA_CODE2
   AND a.accept_date BETWEEN TRUNC(TO_DATE(:PARA_CODE3,'YYYY-MM-DD HH24:MI:SS')) AND TRUNC(TO_DATE(:PARA_CODE4,'YYYY-MM-DD HH24:MI:SS'))+1
   AND a.trade_type_code IN (1060,1061,1062,1063,1070,1071,1080,1081,1090,1210,1211,1212,1213,1214)
   AND b.eparchy_code=:PARA_CODE5
   AND a.trade_eparchy_code = :PARA_CODE5
   AND :PARA_CODE6 IS NULL   
   AND :PARA_CODE7 IS NULL
   AND :PARA_CODE8 IS NULL
   AND :PARA_CODE9 IS NULL
   AND :PARA_CODE10 IS NULL
   GROUP BY b.trade_type,a.cust_name,a.serial_number,to_char(a.accept_date,'YYYY-MM-DD HH24:MI')