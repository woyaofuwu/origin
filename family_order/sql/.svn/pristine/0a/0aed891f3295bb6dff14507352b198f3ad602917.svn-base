SELECT a.user_id,a.product_id,a.cust_id,a.serial_number
  FROM tf_f_user a
 WHERE product_id = :PRODUCT_ID
   AND cust_id = TO_NUMBER(:CUST_ID)
   AND NOT EXISTS (SELECT 1 FROM td_s_commpara b
                    WHERE b.subsys_code='CGM'
                      AND b.param_attr='5'
                      AND (:PRODUCT_ID = b.param_code))
   AND remove_tag = '0'