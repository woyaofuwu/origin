SELECT c.group_id,
       c.cust_name,
       to_char(u.user_id) user_id,
       u.serial_number,
       up.product_id,
       u.remove_tag,
       u.open_date,
       u.in_date,
       up.brand_code,
       u.eparchy_code,
       u.in_date AS START_DATE,
       u.develop_date AS END_DATE,
       p.acct_id,
       u.user_state_codeset
  FROM tf_f_cust_group   c,
       tf_f_user         u,
       tf_f_user_product up,
       tf_a_payrelation  p
 WHERE 1 = 1
   AND c.group_id = :GROUP_ID
   AND c.cust_id = u.cust_id
   AND u.partition_id = mod(u.user_id, 10000)
   AND u.remove_tag = :REMOVE_TAG
   AND up.partition_id = u.partition_id
   AND up.user_id = u.user_id
   AND p.user_id =u.user_id
   AND p.partition_id = u.partition_id
   AND p.default_tag = '1'
   AND p.act_tag = '1'
   AND TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN p.start_cycle_id AND
       p.end_cycle_id