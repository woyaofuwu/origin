select /*+ first_rows(1)*/count(1) recordcount
  from tf_f_user_sale_active
 where user_id = :USER_ID
   and partition_id = mod(to_number(:USER_ID), 10000)
   and product_id = :PRODUCT_ID
   and sysdate between start_date and end_date
   and rownum < 2