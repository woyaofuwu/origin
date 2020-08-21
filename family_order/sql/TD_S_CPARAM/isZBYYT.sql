--IS_CACHE=Y
SELECT count(*) recordcount 
  FROM TD_M_STAFF t, td_m_depart c
  WHERE t.staff_id =:STAFF_ID
   and t.depart_id = c.depart_id
   and c.depart_kind_code = 100