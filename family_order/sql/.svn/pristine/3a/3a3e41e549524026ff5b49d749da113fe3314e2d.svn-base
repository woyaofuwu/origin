SELECT subsys_code,param_attr,param_code,param_name,para_code1,para_code2,para_code3,para_code4,para_code5,para_code6,para_code7,para_code8,para_code9,para_code10,para_code11,para_code12,para_code13,para_code14,para_code15,para_code16,para_code17,para_code18,para_code19,para_code20,para_code21,para_code22,para_code23,para_code24,para_code25,to_char(para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_commpara
 WHERE subsys_code='CSM'
   AND param_attr=970
   AND param_code='0'
   AND para_code1=:PURCHASE_MODE
   AND para_code2=:PURCHASE_ATTR
   AND para_code3=to_char(:PRODUCT_ID)
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE
    OR eparchy_code='ZZZZ') 
UNION 
SELECT subsys_code,param_attr,param_code,param_name,para_code1,para_code2,para_code3,para_code4,para_code5,para_code6,para_code7,para_code8,para_code9,para_code10,para_code11,para_code12,para_code13,para_code14,para_code15,para_code16,para_code17,para_code18,para_code19,para_code20,para_code21,para_code22,para_code23,para_code24,para_code25,to_char(para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_commpara a,tf_f_user_discnt b 
 WHERE a.subsys_code='CSM'
   AND a.param_attr=970
   AND a.param_code='2'
   AND a.para_code1=:PURCHASE_MODE
   AND a.para_code2=:PURCHASE_ATTR
   AND a.para_code3=b.discnt_code
   AND sysdate BETWEEN a.start_date AND a.end_date
   AND (a.eparchy_code=:EPARCHY_CODE
    OR a.eparchy_code='ZZZZ')
   AND SYSDATE BETWEEN b.start_date AND b.end_date
   AND b.user_id = to_number(:USER_ID)
   AND b.partition_id = MOD(to_number(:USER_ID), 10000) 
UNION 
SELECT subsys_code,param_attr,param_code,param_name,para_code1,para_code2,para_code3,para_code4,para_code5,para_code6,para_code7,para_code8,para_code9,para_code10,para_code11,para_code12,para_code13,para_code14,para_code15,para_code16,para_code17,para_code18,para_code19,para_code20,para_code21,para_code22,para_code23,para_code24,para_code25,to_char(para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_commpara 
 WHERE subsys_code='CSM'
   AND param_attr=970
   AND param_code='3'
   AND para_code1=:PURCHASE_MODE
   AND para_code2=:PURCHASE_ATTR
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE
    OR eparchy_code='ZZZZ')