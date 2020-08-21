select distinct u.serial_number as RES_COUNT,u.serial_number,u.user_id
   from tf_f_user partition(PAR_TF_F_USER_6) u, tf_f_customer c
  Where u.cust_id = c.cust_id
      and c.partition_id = mod(u.cust_id, 10000)
    and u.remove_tag = '0'
    and u.city_code not in ('HNSJ', 'HNHN')
    and c.cust_name = :CUST_NAME
    and c.pspt_id = :PSPT_ID
    and c.remove_tag = '0'
    and c.is_real_name = '1'
    and exists
  (SELECT 1
           FROM tf_F_user_product partition(PAR_TF_F_USER_PRODUCT_6) p
          where p.user_id = u.user_id
            and p.partition_id = mod(u.user_id, 10000)
            and p.main_tag = '1'
            and p.brand_code in ('G001', 'G002', 'G010', 'G005')
            and sysdate between p.start_date and p.end_date)
   and not exists
    (SELECT 1
           FROM tf_f_user_other partition(PAR_TF_F_USER_OTHER_6) a
          where a.user_id = u.user_id
            and a.partition_id = mod(u.user_id, 10000)
            and a.rsrv_value_code = 'HYYYKBATCHOPEN'
            and sysdate between a.start_date and a.end_date)  