SELECT count(1) recordcount
FROM  TF_FH_CUST_GROUP a
 WHERE a.remove_tag = '0'
   AND backmonth=:PARAM16
   AND (cust_name like '%'||:PARAM0||'%' OR :PARAM0 IS NULL)
   AND (class_id=:PARAM1 OR :PARAM1 IS NULL)
   AND (client_status=:PARAM2 OR :PARAM2 IS NULL)
   AND (company_address like '%'||:PARAM3||'%' OR :PARAM3 IS NULL)
   AND (enterprise_scope=:PARAM4 OR :PARAM4 IS NULL)
   AND (calling_type_code=:PARAM5 OR :PARAM5 IS NULL)
   AND (calling_sub_type_code=:PARAM6
         OR :PARAM6 IS NULL)
   AND (enterprise_type_code=:PARAM7
          OR :PARAM7 IS NULL)
   AND (enterprise_size_code=:PARAM8
         OR :PARAM8 IS NULL)
   AND (prevaluen1>=:PARAM9 OR :PARAM9 IS NULL)
   AND (super_group_id=:PARAM10 OR :PARAM10 IS NULL)
   AND (manager_staff_id=:PARAM11 OR :PARAM11 IS NULL)
   AND (subclass_id=:PARAM12 or :PARAM12 IS NULL)
   AND (group_attr=:PARAM13 or :PARAM13 IS NULL)
   AND not exists
     ( select 1
       from tf_f_user b,tf_f_user_vpmn c
       WHERE b.user_id = c.user_id
       and a.cust_id = b.cust_id
     )
   AND  :PARAM17 IS NULL
  and
   ((exists
      ( select 1
        from td_m_staff e,td_m_depart f
        where a.manager_staff_id = e.staff_id
        and e.depart_id = f.depart_id
        and f.depart_frame like (
           select t.depart_frame from td_m_depart t where t.depart_id= :PARAM14)||'%'
         and (f.parent_depart_id = :PARAM15 OR :PARAM15 IS NULL ) )
   ) or :PARAM14 = '49999' )