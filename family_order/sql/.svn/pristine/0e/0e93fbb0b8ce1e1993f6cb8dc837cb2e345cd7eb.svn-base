SELECT a.subsys_code,a.param_attr,a.param_code,a.param_name,a.para_code1,a.para_code2,a.para_code3,a.para_code4,a.para_code5,a.para_code6,a.para_code7,a.para_code8,a.para_code9,a.para_code10,a.para_code11,a.para_code12,a.para_code13,a.para_code14,a.para_code15,a.para_code16,a.para_code17,a.para_code18,a.para_code19,a.para_code20,a.para_code21,a.para_code22,a.para_code23,a.para_code24,a.para_code25,to_char(a.para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(a.para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(a.para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(a.para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(a.para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,a.eparchy_code,a.remark,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_commpara a,tf_b_trade b,tf_b_trade_relation c,tf_f_user_discnt d
 WHERE (a.subsys_code=:SUBSYS_CODE
   AND a.param_attr=14
   AND a.param_code=b.rsrv_str3
   AND a.para_code1=c.relation_type_code
   AND sysdate BETWEEN a.start_date AND a.end_date )
   AND (a.eparchy_code=:EPARCHY_CODE
    OR a.eparchy_code='ZZZZ')
   AND a.para_code19 IS NOT NULL 
   AND b.trade_id=TO_NUMBER(:TRADE_ID)
   AND c.trade_id=TO_NUMBER(:TRADE_ID)
   AND d.user_id=TO_NUMBER(b.rsrv_str2)
   AND d.partition_id=MOD(TO_NUMBER(b.rsrv_str2),10000)
   AND d.end_date>=SYSDATE
   AND (a.para_code19=TO_CHAR(d.discnt_code)
	OR a.para_code19 LIKE TO_CHAR(d.discnt_code)||'|%'
	OR a.para_code19 LIKE '%|'||TO_CHAR(d.discnt_code)||'|%'
	OR a.para_code19 LIKE '|%'||TO_CHAR(d.discnt_code))