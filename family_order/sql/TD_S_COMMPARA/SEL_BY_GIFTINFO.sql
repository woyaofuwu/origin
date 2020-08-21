select 
a.subsys_code subsys_code,a.param_attr param_attr,
a.param_code param_code,a.param_name param_name,a.para_code1 para_code1,a.para_code2 para_code2,
a.para_code3 para_code3,a.para_code4 para_code4,
a.para_code5 para_code5,a.para_code6 para_code6,
a.para_code7 para_code7,a.para_code8 para_code8,
c.param_name para_code9,b.param_name para_code10,
a.para_code11 para_code11,a.para_code12 para_code12,
a.para_code13 para_code13,a.para_code14 para_code14,
a.para_code15 para_code15,a.para_code16 para_code16,
a.para_code17 para_code17,a.para_code18 para_code18,
a.para_code19 para_code19,a.para_code20 para_code20,
a.para_code21 para_code21,a.para_code22 para_code22,
a.para_code23 para_code23,a.para_code24 para_code24,
a.para_code25 para_code25,'' para_code26,
'' para_code27,'' para_code28,
'' para_code29,'' para_code30,
to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date, to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,a.eparchy_code,a.remark,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
FROM td_s_commpara a ,td_s_commpara b ,td_s_commpara c
 WHERE a.param_attr='1226'
   AND b.param_attr='1227'
   AND c.param_attr = '1225'
   AND b.param_code = a.para_code1
   AND a.param_code = c.param_code
   AND SYSDATE BETWEEN a.start_date AND a.end_date
   AND SYSDATE BETWEEN b.start_date AND b.end_date
   AND SYSDATE BETWEEN c.start_date AND c.end_date
   AND a.subsys_code = b.subsys_code
   AND a.subsys_code = c.subsys_code
   AND a.subsys_code = :SUBSYS_CODE
   AND a.eparchy_code = b.eparchy_code
   AND a.eparchy_code = c.eparchy_code
   AND (a.eparchy_code=:TRADE_EPARCHY_CODE
   OR a.eparchy_code='ZZZZ')
   AND EXISTS(
       SELECT 1 FROM tf_f_user_other d
         WHERE d.rsrv_value_code = 'TSCE'
           AND d.user_Id = TO_number(:USER_ID)
           AND d.partition_id=MOD(TO_number(:USER_ID),10000)
           AND SYSDATE BETWEEN d.start_date AND d.end_date
           AND to_number(d.rsrv_str3)<to_number(d.rsrv_str4)
           AND d.rsrv_value = c.param_code
   )