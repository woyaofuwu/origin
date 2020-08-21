select count(distinct u.user_id) REAL_COUNT
   from tf_f_user u, tf_f_customer c
  Where u.cust_id = c.cust_id
    and c.partition_id = mod(u.cust_id, 10000)
    and u.remove_tag = '0'
    and u.NET_TYPE_CODE =:NET_TYPE_CODE
    and c.cust_name = :CUST_NAME
    and c.pspt_id = :PSPT_ID
    and c.pspt_type_code in ('0','1')
