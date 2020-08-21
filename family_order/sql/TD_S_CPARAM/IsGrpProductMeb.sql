select count(1) recordcount 
from tf_f_relation_uu  uu
where 1=1
and uu.partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
and uu.user_id_b = TO_NUMBER(:USER_ID_B)
 AND uu.end_date > last_day(trunc(sysdate))+1-1/24/3600
and exists (
    select 1 from tf_f_user u
    where 1=1  
    and u.partition_id = mod(uu.user_id_a,10000)
    and u.user_id = uu.user_id_a
    and u.product_id = :PRODUCT_ID
    and u.remove_tag = '0' 
    and exists (
        select 1 from td_b_attr_biz bz
        where 1=1
        and bz.id = u.product_id
        and bz.id_type = 'P'
        and bz.attr_obj = 'AddMem'
        and bz.attr_code = 'AddMemOnlyOne'  
        and bz.end_date > sysdate
    )
)