SELECT
distinct a.subscribe_id para_code1,
b.batch_oper_name para_code2,
a.staff_id para_code3,
to_char(a.accept_date,'yyyy-mm-dd') para_code4,
'' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_b_trade_batdeal a,td_b_opertype b
 WHERE a.batch_oper_type = b.batch_oper_type
   AND (a.batch_oper_type = :PARA_CODE1 OR :PARA_CODE1 IS NULL)
   AND a.staff_id = :PARA_CODE2
   AND a.accept_date>=to_date(:PARA_CODE3 || ' 00:00:00','yyyy-mm-dd hh24:mi:ss')
   AND a.accept_date<=to_date(:PARA_CODE4 || ' 23:59:59','yyyy-mm-dd hh24:mi:ss')
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)