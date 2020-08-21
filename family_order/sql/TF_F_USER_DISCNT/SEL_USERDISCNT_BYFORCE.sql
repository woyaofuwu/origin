SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
from tf_f_user_discnt 
where user_id = TO_NUMBER(:USER_ID) and end_date > sysdate
and discnt_code in(select discnt_code from td_b_product_discnt 
                   where product_id = :PRODUCT_ID and (force_tag = :FORCE_TAG or forcegroup_tag = :FORCE_TAG) 
                   and end_date > sysdate)
AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)