select * from tf_f_user_family_circle t 
where t.status = :STATUS and 
(t.main_msisdn = :MSISDN or t.target_msisdn = :MSISDN) and 
t.group_type = :GROUP_TYPE 
order by t.target_role desc
