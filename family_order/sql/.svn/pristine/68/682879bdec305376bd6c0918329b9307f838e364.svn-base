SELECT A.SERIAL_NUMBER, a.cust_id, a.user_id, B.PSPT_ID, C.Product_Id
  FROM TF_F_CUST_PERSON B, TF_F_USER A, tf_f_user_product C
 WHERE A.CUST_ID = B.CUST_ID
   and a.user_id = C.user_id
   AND B.PSPT_ID = :PSPT_ID
   and c.product_id = :PRODUCT_ID
   and a.remove_tag = '0'
   and b.remove_tag = '0'
   and c.end_date > sysdate
   and c.main_tag='1'