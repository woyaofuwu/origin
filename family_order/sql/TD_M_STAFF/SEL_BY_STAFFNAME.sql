--IS_CACHE=Y
SELECT staff_id,depart_id,staff_name,
f_sys_getcodename('depart_id',depart_id,null,null) depart_name
  FROM td_m_staff
 WHERE staff_name LIKE '%'||:STAFF_NAME||'%'
 and DIMISSION_TAG = '0'