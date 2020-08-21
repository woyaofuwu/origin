--IS_CACHE=Y
SELECT package_id,element_type_code,element_id,limit_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark 
  FROM td_b_element_package_limit
 WHERE (eparchy_code = :EPARCHY_CODE or eparchy_code = 'ZZZZ')
   and package_id=:PACKAGE_ID
   AND element_type_code=:ELEMENT_TYPE_CODE
   AND element_id=:ELEMENT_ID
   AND limit_tag=:LIMIT_TAG