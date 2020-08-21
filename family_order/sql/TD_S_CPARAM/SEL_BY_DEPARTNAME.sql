--IS_CACHE=Y
SELECT count(1) recordcount
  FROM td_m_staff a,td_m_depart b
 WHERE a.depart_id = b.depart_id
 and b.depart_name like '%'||:PARAM0||'%'
 and DIMISSION_TAG = '0'