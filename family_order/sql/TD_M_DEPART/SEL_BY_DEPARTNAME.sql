--IS_CACHE=Y
SELECT depart_id,depart_code,depart_name,depart_frame,area_code,parent_depart_id 
  FROM td_m_depart
 WHERE depart_name LIKE '%'||:DEPART_NAME||'%'
 AND validflag='0'