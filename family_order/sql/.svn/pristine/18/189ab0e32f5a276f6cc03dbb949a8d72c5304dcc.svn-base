select s.partition_id,
       to_char(s.user_id) user_id,
       s.service_id,
       s.main_tag,
       p.inst_id,
       p.rela_inst_id,
       p.service_code,
       p.billing_type,
       p.usage_state,
       p.start_date,
       p.end_date
from tf_f_user_svc s,tf_f_user_pcrf p 
where s.user_id = p.user_id
and s.inst_id = p.rela_inst_id
and s.partition_id = MOD(TO_NUMBER(:USER_ID), 10000) 
and p.partition_id = MOD(TO_NUMBER(:USER_ID), 10000) 
and s.user_id = :USER_ID
and sysdate <= p.end_date