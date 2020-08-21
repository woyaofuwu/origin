--IS_CACHE=Y
SELECT subsys_code,param_attr,param_code,param_name,para_code1,para_code2,para_code3,para_code4,para_code5,para_code6,para_code7,para_code8,para_code9,para_code10,para_code11,para_code12,para_code13,para_code14,para_code15,para_code16,para_code17,para_code18,para_code19,para_code20,para_code21,para_code22,para_code23,para_code24,para_code25,to_char(para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_commpara
 WHERE (subsys_code='CSM'
   AND param_attr=716   
   AND param_code='01'   
   AND sysdate BETWEEN start_date AND end_date
   AND (para_code3='1')                   --标识活动有效
   AND (:PARA_CODE1  IS NULL OR para_code1  =:PARA_CODE1)          --活动编码
   AND (:PARA_CODE2  IS NULL OR para_code2  LIKE :PARA_CODE2)     --活动名称
   AND (:PARA_CODE23 IS NULL OR para_code23 LIKE :PARA_CODE23))   --活动描述