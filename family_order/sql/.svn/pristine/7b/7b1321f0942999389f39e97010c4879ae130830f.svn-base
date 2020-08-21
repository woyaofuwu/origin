select count(distinct u.user_id) REAL_COUNT
  from tf_f_user u, tf_f_cust_person_other c
 Where u.cust_id = c.cust_id
   and c.partition_id = mod(u.cust_id, 10000)
   and u.remove_tag = '0'
   and u.city_code not in ('HNSJ', 'HNHN')
   and c.use_name = :CUST_NAME
   and c.use_pspt_id = :PSPT_ID
   AND (:SERIAL_NUMBER is null or u.serial_number <> :SERIAL_NUMBER)
   and exists
 (SELECT 1
          FROM tf_F_user_product p
         where p.user_id = u.user_id
           and p.partition_id = mod(u.user_id, 10000)
           and p.main_tag = '1'
           and p.brand_code in ('G001', 'G002', 'G010', 'G005')
           and sysdate between p.start_date and p.end_date)
   and exists
 (select 1
          from tf_f_customer m
         where m.cust_id = c.cust_id
           and m.partition_id = c.partition_id
           and m.remove_tag = '0'
           and m.pspt_type_code in ('D', 'E', 'G', 'L', 'M'))
    and not exists
 (SELECT 1
           FROM tf_f_user_other  a
          where a.user_id = u.user_id
            and a.partition_id = mod(u.user_id, 10000)
            and a.rsrv_value_code = 'HYYYKBATCHOPEN'
            and sysdate between a.start_date and a.end_date)  