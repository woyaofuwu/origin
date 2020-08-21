--IS_CACHE=Y
SELECT eparchy_code,para_code1,para_code2,code_length,fix_tag,fix_rule_code,para_name,rsvalue1,rsvalue2,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_s_rescoderule_para
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_code1=:PARA_CODE1
   AND (:PARA_CODE2 IS NULL OR para_code2=:PARA_CODE2)