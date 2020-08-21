SELECT COUNT(1)  recordcount FROM ( 
SELECT a.*
  FROM tf_b_trade a,tf_f_user b,td_b_product_comp_rela c
 WHERE a.cust_id_b = b.cust_id
   AND b.partition_id = MOD(a.user_id_b,10000)
   AND b.user_id = a.user_id_b
   AND a.cust_id_b = :CUST_ID_B
   AND c.product_id_a = :PRODUCT_ID_B
   AND b.product_id = c.product_id_b    
   AND a.cust_id = TO_NUMBER(:CUST_ID)
   AND b.remove_tag='0'
   AND a.trade_type_code in ('3514','3515','3517') 
   AND (:LOCATION IS NULL OR 'COME' != :LOCATION)
UNION ALL
SELECT d.*
  FROM tf_b_trade d,tf_f_user e 
 WHERE d.user_id_b = e.user_id 
   AND e.partition_id = MOD(d.user_id_b,10000)
   AND d.cust_id_b = TO_NUMBER(:CUST_ID_B)
   AND d.cust_id = TO_NUMBER(:CUST_ID)
   AND e.product_id = :PRODUCT_ID_B
   AND e.remove_tag = '0'
   AND d.trade_type_code in ('3514','3515','3517')
   AND (:LOCATION IS NULL OR 'COME' != :LOCATION)
)