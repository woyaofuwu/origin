SELECT a.partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,to_char(nvl(b.leave_real_fee,0))  short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
from tf_f_relation_uu a,tf_o_leaverealfee b
where a.user_id_a>0 and a.user_id_b=b.user_id
and a.relation_type_code='26' and a.role_code_b='9'
and ((:X_GETMODE=1 AND b.leave_real_fee<=:LEAVE_REAL_FEE) OR :X_GETMODE=0)
and a.start_date<sysdate and sysdate<a.end_date
and exists (select 1 from tf_f_user c
    where c.user_id=a.user_id_b and c.eparchy_code=:EPARCHY_CODE and c.city_code=:CITY_CODE)