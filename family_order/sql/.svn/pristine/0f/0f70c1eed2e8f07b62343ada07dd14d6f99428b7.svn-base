select COUNT(1) recordcount
from   (select count(*) count_purl
         from   tf_f_user_other a
         where  a.user_id = to_number(:USER_ID)
         and    a.partition_id = mod(to_number(:USER_ID), 10000)
         and    a.rsrv_value_code = 'PUPP'
         and    a.end_date > sysdate
         and    exists (
                 select 1
                 from   tf_f_user_sale_active b
                 where  a.user_id = b.user_id
                 and    b.partition_id = a.partition_id
                 and    a.rsrv_str1 = b.product_id
                 and    a.rsrv_str2 = b.package_id
                 and    b.end_date > sysdate)
         ) tabtemp
where  count_purl >= :NUM