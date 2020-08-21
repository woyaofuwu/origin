--IS_CACHE=Y
select product_id,
       service_id,
       main_tag,
       default_tag,
       force_tag,
       start_date,
       end_date
  from td_b_product_svc where product_id = :PRODUCT_ID