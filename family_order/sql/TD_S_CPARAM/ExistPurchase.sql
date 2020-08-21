SELECT /*+ ordered use_nl(c,s,u) FIRST_ROWS(1)*/COUNT(1) recordcount
 from   tf_f_customer c, tf_f_user u,tf_f_user_sale_active s
 where  s.user_id  = u.user_id
 and    c.cust_id = u.cust_id
 AND    c.partition_id = MOD(u.cust_id,10000)
 AND    s.partition_id = MOD(u.user_id,10000)
 and    c.remove_tag = '0'
 and    c.pspt_id = :PSPT_ID
 and    s.product_id = :PRODUCT_ID
 and    s.process_tag = '0'
 and    s.end_date > sysdate
 and    rownum < 2