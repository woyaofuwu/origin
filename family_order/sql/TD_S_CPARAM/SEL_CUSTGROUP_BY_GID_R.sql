SELECT count(1) recordcount
  FROM tf_f_cust_group a
 WHERE group_id=:PARAM0
 and remove_tag = '0'
 AND (manager_staff_id=:PARAM1 or :PARAM1 IS NULL)
  AND EXISTS
      ( select 1
         from td_m_staff e,td_m_depart f
         where a.manager_staff_id = e.staff_id
         and e.depart_id = f.depart_id
         and (f.depart_id = :PARAM2 OR :PARAM2 IS NULL)
         and (f.parent_depart_id = :PARAM3 OR :PARAM3 IS NULL ) )