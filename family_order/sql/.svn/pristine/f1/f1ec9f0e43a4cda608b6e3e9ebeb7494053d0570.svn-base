SELECT b.sp_id para_code1,c.sp_name para_code2,b.sp_svc_id para_code3,
b.biz_type_code para_code4,d.biz_type para_code5,d.biz_desc para_code6,
DECODE(b.modify_tag,'0','定制','1','退订','2','修改') para_code7,
to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') para_code8,
to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') para_code9,
to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') para_code10,
a.trade_staff_id para_code11 ,e.staff_name para_code12,a.trade_id para_code13,
'' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,
'' para_code19,'' para_code20,'' para_code21,'' para_code22,'' para_code23,
'' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,
'' para_code29,'' para_code30,'' start_date,'' end_date,'' eparchy_code,
'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,
0 param_attr,'' param_code,'' param_name
  FROM tf_bh_trade a,tf_b_trade_mbmp_sub b,td_m_spfactory c,td_m_spservice d,td_m_staff e
 WHERE a.serial_number = :PARA_CODE1
   AND (b.modify_tag = :PARA_CODE2 OR :PARA_CODE2 = '*')
   AND a.trade_id=b.trade_id
   AND b.sp_id=c.sp_id
   AND b.sp_id=d.sp_id
   AND b.sp_svc_id=d.sp_svc_id
   AND a.trade_staff_id=e.staff_id
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)