--IS_CACHE=Y
select a.STAFF_ID, a.right_code, :JOB_CODE as job_code from TF_M_STAFFFUNCRIGHT a, td_m_staff c
where a.right_attr='0' and a.right_tag='1' and a.rsvalue1 in ('0','2')
and a.staff_id = c.staff_id
and c.job_code=:JOB_CODE 
and c.dimission_tag='0'
union 
select c.staff_id, b.right_code, :JOB_CODE as job_code from TF_M_ROLEFUNCRIGHT b, td_m_staff c, TF_M_STAFFFUNCRIGHT a
where b.role_code =a.right_code
and a.right_attr='1' 
and a.staff_id=c.staff_id
and a.right_tag='1'
and b.rsvalue1 in ('0','2')
and c.job_code=:JOB_CODE 
and c.dimission_tag='0'