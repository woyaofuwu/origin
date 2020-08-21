SELECT count(1) recordcount
  FROM tf_f_cust_group a
 WHERE a.remove_tag='0'
   AND (:PARAM0 is null  or a.group_id =:PARAM0)
  AND (:PARAM1  is null  or a.prevalued2 >=to_date(:PARAM1,'yyyy-mm-dd'))
  AND (:PARAM2 is null or a.prevalued2 <=to_date(:PARAM2,'yyyy-mm-dd'))
  AND (:PARAM3 is null or a.client_status =:PARAM3)
  AND (:PARAM4 is null or a.calling_type_code =:PARAM4)
  AND (:PARAM5 is null or a.calling_sub_type_code =:PARAM5)
  AND (:PARAM6 is NULL  or a.manager_staff_id=:PARAM6)
  AND ( EXISTS
      ( select 1
         from td_m_staff e,td_m_depart f
         where a.manager_staff_id = e.staff_id
         and e.depart_id = f.depart_id
         and f.depart_frame like (
           select t.depart_frame from td_m_depart t where t.depart_id= :PARAM7)||'%'
         and (f.parent_depart_id = :PARAM8 OR :PARAM8 IS NULL )
      ) or :PARAM7 is null )