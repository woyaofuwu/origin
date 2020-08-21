select a.brand_code,
       to_char(a.start_date, 'yyyy-mm-dd') start_date,
       to_char(a.end_date, 'yyyy-mm-dd') end_date
  from tf_f_user_product a
 where a.user_id = :USER_ID
   and a.product_mode = '00'
   and a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   and sysdate between a.start_date and a.end_date