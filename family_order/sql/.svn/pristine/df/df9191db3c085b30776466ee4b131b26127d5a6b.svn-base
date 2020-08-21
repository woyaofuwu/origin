--IS_CACHE=Y
SELECT subsys_code,param_attr,param_code,param_name,para_code1,start_date,end_date,eparchy_code,para_code2,para_code3,'' para_code3,'' para_code4,'' para_code5,'' para_code6,'' para_code7,'' para_code8,'' para_code9,'' para_code10,'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,'' remark,'' update_staff_id,'' update_depart_id,'' update_time
  FROM td_s_commpara
 WHERE param_attr = '3005'
   AND param_code = :PARAM_CODE
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code = :TRADE_EPARCHY_CODE OR eparchy_code='ZZZZ')