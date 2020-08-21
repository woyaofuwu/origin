--IS_CACHE=Y
select a.data_code,b.data_name data_name from TF_M_STAFFDATARIGHT a,td_m_dataright b
 where a.right_attr='0' and a.staff_id=:STAFF_ID and a.right_tag='1' and a.rsvalue1 in ('0','2') and a.data_code=b.data_code
 union
select c.data_code,d.role_name data_name from TF_M_ROLEDATARIGHT c,td_m_role d
 where c.role_code in (select data_code from TF_M_STAFFDATARIGHT where
             right_attr='1' and  staff_id=:STAFF_ID and right_tag='1')
   and c.rsvalue1 in ('0','2')
   and c.role_code=d.role_code