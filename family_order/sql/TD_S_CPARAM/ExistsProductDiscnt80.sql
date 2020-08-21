select sum(recordcount) recordcount
from (select count(*) recordcount
	from tf_f_user_infochange a
	where partition_id=mod(to_number(:USER_ID),10000)
	  and user_id=to_number(:USER_ID)
	  and sysdate between start_date and end_date
	  and exists (select 1 from td_s_commpara
	              where param_attr=47
	                and subsys_code='CSM'
                    and sysdate between start_date and end_date
                    and para_code1='0'
                    and param_code=to_char(a.product_id))
	union all
	select count(*) recordcount
	from tf_f_user_discnt b
	where partition_id=mod(to_number(:USER_ID),10000)
	  and user_id=to_number(:USER_ID)
	  and sysdate between start_date and end_date
	  and exists (select 1 from td_s_commpara
	              where param_attr=47
	                and subsys_code='CSM'
                    and sysdate between start_date and end_date
                    and para_code1='1'
                    and param_code=to_char(b.discnt_code)))