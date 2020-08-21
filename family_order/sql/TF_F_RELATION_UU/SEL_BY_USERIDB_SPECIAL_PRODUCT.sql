select distinct a.product_id
  from tf_f_user a, tf_f_relation_uu b, tf_f_cust_group c
 where a.user_id = b.user_id_a
   and a.partition_id = mod(b.user_id_a, 10000)
   and a.cust_id = c.cust_id
   and b.end_date > sysdate
   and b.user_id_b = to_number(:USER_ID)
   and b.partition_id = mod(to_number(:USER_ID), 10000)
   and a.remove_tag = '0'
   and c.remove_tag = '0'
   and not exists
 (select 1
          from td_s_commpara t
         where t.param_code = a.product_id
           and t.subsys_code = :SUBSYS_CODE
           and t.param_attr = :PARAM_ATTR
           and (t.eparchy_code = :EPARCHY_CODE or t.eparchy_code = 'ZZZZ'))