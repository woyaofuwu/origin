SELECT b.subsys_code,b.param_attr,b.param_code,b.param_name,b.para_code1,b.para_code2,b.para_code3,b.para_code4,a.trade_id para_code5,a.rsrv_str1 para_code6,a.rsrv_str1 para_code7,b.para_code8,b.para_code9,b.para_code10,b.para_code11,b.para_code12,b.para_code13,b.para_code14,b.para_code15,b.para_code16,b.para_code17,b.para_code18,b.para_code19,b.para_code20,b.para_code21,b.para_code22,b.para_code23,b.para_code24,b.para_code25,TO_CHAR(b.para_code26, 'yyyy-mm-dd hh24:mi:ss') para_code26,TO_CHAR(b.para_code27, 'yyyy-mm-dd hh24:mi:ss') para_code27,TO_CHAR(b.para_code28, 'yyyy-mm-dd hh24:mi:ss') para_code28,TO_CHAR(b.para_code29, 'yyyy-mm-dd hh24:mi:ss') para_code29,TO_CHAR(b.para_code30, 'yyyy-mm-dd hh24:mi:ss') para_code30,TO_CHAR(b.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,TO_CHAR(b.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,b.eparchy_code,b.remark,b.update_staff_id,b.update_depart_id,TO_CHAR(b.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time
  FROM tf_b_trade a, td_s_commpara b
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.rsrv_str5 = b.param_code
   AND b.subsys_code = 'CSM'
   AND b.param_attr = 89
   AND (b.eparchy_code = :EPARCHY_CODE OR b.eparchy_code = 'ZZZZ')
   AND SYSDATE BETWEEN b.start_date AND b.end_date