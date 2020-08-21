select count(*) recordcount
from tf_f_user_discnt a
where user_id=to_number(:USER_ID)
 and partition_id=mod(to_number(:USER_ID),10000)
 and end_date>sysdate
 and start_date<end_date
 and exists (select 1 from td_s_commpara
              where param_attr=31
                and subsys_code='CSM'
                and param_code=a.discnt_code
                and para_code1='0'
                and (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                and SYSDATE BETWEEN start_date AND end_date)