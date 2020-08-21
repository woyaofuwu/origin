SELECT COUNT(1) recordcount
from tf_f_user_discnt a
where user_id=to_number(:USER_ID)
    and partition_id=mod(to_number(:USER_ID),10000)
    and user_id_a=-1
    and end_date>sysdate
    and exists (select 1 from td_s_commpara
                where subsys_code='CSM'
                  and param_attr=80
                  and eparchy_code=:EPARCHY_CODE
                  and sysdate between start_date and end_date
                  and para_code10 is not null
                  and para_code10=to_char(a.discnt_code))