select distinct product_id,package_id   
from 
 (select product_id,package_id   
 from tf_f_user_svc
 where user_id = :USER_ID 
and product_id = :PRODUCT_ID
AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND SYSDATE BETWEEN START_DATE AND END_DATE
union
select product_id,package_id    from tf_f_user_discnt
where user_id = :USER_ID 
and product_id = :PRODUCT_ID
AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND SYSDATE BETWEEN START_DATE AND END_DATE
)