--IS_CACHE=Y
select a.right_code,b.right_name from TF_M_STAFFFUNCRIGHT a,td_m_funcright b
 where a.right_attr='0' and a.staff_id=:STAFF_ID and a.right_tag='1' and a.rsvalue1 in ('0','2') and a.right_code=b.right_code
 union 
select c.right_code,d.role_name from TF_M_ROLEFUNCRIGHT c,td_m_role d
 where c.role_code in (select right_code from TF_M_STAFFFUNCRIGHT where 
right_attr='1' and  staff_id=:STAFF_ID and right_tag='1')
and rsvalue1 in ('0','2') and c.role_code=d.role_code