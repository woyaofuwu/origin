SELECT distinct /*+ use_nl(a,b,c,d) ordered index(b,IDX_TF_BH_TRADE_ACCEPTDATE) */ b.serial_number para_code1,a.action_code para_code2,c.action_name para_code3,
b.accept_date para_code4, b.trade_staff_id para_code5,b.trade_depart_id para_code6,d.trade_type para_code7,
f_sys_getcodename('staff_id',b.trade_staff_id,NULL,NULL) para_code8,
 uop_crm1.f_sys_getcodename('depart_id',b.trade_depart_id,NULL,NULL) para_code9,
 a.action_count para_code10,
decode(b.cancel_tag,'0','正常','1','正常','2','返销') para_code11,
decode(b.cancel_tag,'0','','1','','2',cancel_staff_id) para_code12,
decode(b.cancel_tag,'0','','1','','2',to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss'))  para_code13,
'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_bh_trade_staff b,tf_b_trade_scoresub a,ucr_cen1.td_b_score_action c,ucr_cen1.td_s_tradetype d
 WHERE a.trade_id = b.trade_id
   AND a.accept_month = b.accept_month
   AND a.action_code = c.action_code
   AND d.trade_type_code = b.trade_type_code
   AND b.accept_date >= to_date(:PARA_CODE1,'yyyy-mm-dd hh24:mi:ss')
   AND b.accept_date <= to_date(:PARA_CODE2,'yyyy-mm-dd hh24:mi:ss')
   AND ((TRIM(:PARA_CODE3) IS NULL) OR b.trade_staff_id >= TRIM(:PARA_CODE3))
   AND ((TRIM(:PARA_CODE5) IS NULL) OR b.trade_staff_id <= TRIM(:PARA_CODE5))
   AND ((TRIM(:PARA_CODE4) IS NULL) OR b.trade_depart_id = TRIM(:PARA_CODE4))
   AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)