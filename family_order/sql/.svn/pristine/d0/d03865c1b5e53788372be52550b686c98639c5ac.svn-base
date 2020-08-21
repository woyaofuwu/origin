--IS_CACHE=Y
SELECT a.depart_id,depart_code,depart_name,depart_kind_code,depart_frame,area_code,parent_depart_id,b.staff_id,b.staff_name
  FROM td_m_depart a,td_m_staff b
 WHERE a.depart_id = b.depart_id
 and a.depart_name like '%'||:DEPART_NAME||'%'
 and a.validflag = '0' AND b.dimission_tag = '0'