--IS_CACHE=Y
select 1
    from (select  b.data_code
      from tf_m_staffdataright a, tf_m_roledataright b
      where
      a.data_type = 'K'
      and a.right_attr = 1
      and a.right_tag = 1
      and a.data_code = b.role_code
      and a.staff_id = :STAFF_ID
      union
      select distinct a.data_code
      from tf_m_staffdataright a
      where
      a.data_type = 'K'
      and a.right_attr = 0
      and a.right_tag = 1
      and a.staff_id = :STAFF_ID) tmp
   where tmp.data_code = to_char(:PACKAGE_ID)