--IS_CACHE=Y
SELECT staff_id,depart_id,staff_name,
   f_sys_getcodename('depart_id',depart_id,null,null) depart_name
  FROM td_m_staff a
  where exists
    ( select 1
      from td_m_depart b
      WHERE a.depart_id = b.depart_id 
      and b.depart_name like '%'||:DEPART_NAME||'%'
     )
 and DIMISSION_TAG = '0'