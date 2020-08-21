--IS_CACHE=Y
SELECT package_id,element_type_code_a,element_id_a,element_type_code_b,element_id_b,limit_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark 
  FROM td_b_package_element_limit
 WHERE package_id=:PACKAGE_ID
   AND element_type_code_a=:ELEMENT_TYPE_CODE_A
   AND element_id_a=:ELEMENT_ID_A
   AND limit_tag=:LIMIT_TAG