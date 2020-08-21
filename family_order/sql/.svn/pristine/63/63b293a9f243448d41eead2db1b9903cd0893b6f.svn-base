--IS_CACHE=Y
select b.staff_id STAFF_ID, b.staff_Name STAFF_NAME, b.city_code CITY_CODE, b.eparchy_code EPARCHY_CODE, b.depart_id DEPART_ID, c.depart_name DEPART_NAME, b.serial_number SERIAL_NUMBER1
       from TF_M_STAFFFUNCRIGHT  a,
         td_m_staff b,
         td_m_depart c
       where b.staff_id= a.staff_id
             and c.depart_id = b.depart_id
             and a.right_code = :RIGHT_CODE
             and a.right_attr = '1'
             and ( :DEPART_ID IS NULL OR  b.depart_id = :DEPART_ID)
             and ( :EPARCHY_CODE IS NULL OR b.eparchy_code = :EPARCHY_CODE )
             and ( :STAFF_NAME IS NULL OR b.staff_name like '%'||:STAFF_NAME||'%')
order by b.staff_id