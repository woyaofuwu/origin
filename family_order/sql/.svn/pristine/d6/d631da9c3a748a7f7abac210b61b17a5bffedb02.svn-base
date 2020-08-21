SELECT DISTINCT a.user_id_a
  FROM tf_f_relation_uu a, tf_f_user_product b
 WHERE a.partition_id=MOD(a.USER_ID_B,10000)
   AND a.user_id_a=b.USER_ID
   AND a.END_DATE >= sysdate
   AND b.product_id = :PRODUCT_ID
   AND b.main_tag = '1'
