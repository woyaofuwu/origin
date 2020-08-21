--IS_CACHE=Y
SELECT depart_id,depart_name,parent_depart_id,depart_level
  FROM td_m_depart
 WHERE parent_depart_id=:PARENT_DEPART_ID 
 and (:DEPART_LEVEL is null or depart_level=:DEPART_LEVEL)
 and (:AREA_CODE is null or area_code=:AREA_CODE)
 and validflag='0'
 and (start_date is null or sysdate>=start_date) 
 and (end_date is null or sysdate<=end_date)