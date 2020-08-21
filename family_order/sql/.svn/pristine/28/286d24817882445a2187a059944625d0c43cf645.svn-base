--IS_CACHE=Y
SELECT t.subsys_code,t.param_attr,t.param_code,t.param_name,t.para_code1,t.para_code2,t.para_code3,t.para_code4,t.para_code5,t.para_code6,t.para_code7,to_char(t1.para_code2) para_code8,t.para_code9,t.para_code10,t.para_code11,t.para_code12,t.para_code13,t.para_code14,t.para_code15,t.para_code16,t.para_code17,t.para_code18,t.para_code19,t.para_code20,t.para_code21,t.para_code22,t.para_code23,t.para_code24,t.para_code25,to_char(t.para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(t.para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(t.para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(t.para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(t.para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(t.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(t.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,t.eparchy_code,t.remark,t.update_staff_id,t.update_depart_id,to_char(t.update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_commpara t,td_s_commpara t1
 WHERE t.subsys_code=t1.subsys_code
   AND t.subsys_code='CSM'
   AND t.param_attr=716
   AND t.param_attr=t1.param_attr    
   AND t.param_code='03'
   AND t1.param_code='04'
   AND t.para_code1=:PARA_CODE1
   AND t.para_code1=t1.para_code1