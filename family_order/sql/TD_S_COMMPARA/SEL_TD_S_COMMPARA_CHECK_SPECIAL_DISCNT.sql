SELECT DISTINCT b.param_code,TO_CHAR(NVL(:TRADE_TYPE_CODE,'0')) para_code1,TO_CHAR(NVL(:ENABLE_TAG,'0')) para_code2,b.subsys_code,b.param_attr,'' param_name,'' para_code3,'' para_code4,'' para_code5,'' para_code6,'' para_code7,'' para_code8,'' para_code9,'' para_code10,'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,'' start_date,'' end_date,b.eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time
  FROM tf_b_trade_discnt a,td_s_commpara b
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND a.modify_tag = '1'
   AND a.discnt_code = b.param_code
   AND b.subsys_code = 'CSM'
   AND b.param_attr = '2084'
   AND sysdate BETWEEN b.start_date AND b.end_date
   AND (b.eparchy_code = :TRADE_EPARCHY_CODE OR b.eparchy_code='ZZZZ')
MINUS
SELECT a.param_code,a.para_code1,NVL(a.para_code2,'0') para_code2,a.subsys_code,a.param_attr,'' param_name,'' para_code3,'' para_code4,'' para_code5,'' para_code6,'' para_code7,'' para_code8,'' para_code9,'' para_code10,'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,'' start_date,'' end_date,a.eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time
  FROM td_s_commpara a
 WHERE a.subsys_code = 'CSM'
   AND a.param_attr = '2084'
   AND sysdate BETWEEN a.start_date AND a.end_date
   AND (a.eparchy_code = :TRADE_EPARCHY_CODE OR a.eparchy_code='ZZZZ')