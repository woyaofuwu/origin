select /*+ first_rows(1) */count(*) recordcount
  from TF_F_USER_SALE_ACTIVE
 where partition_id = mod(to_number(:USER_ID), 10000)
 and user_id = to_number(:USER_ID)
 and (product_id = :PRODUCT_ID or :PRODUCT_ID = '-1')
 and process_tag = '0'
 and end_date > sysdate
 and rownum < 2