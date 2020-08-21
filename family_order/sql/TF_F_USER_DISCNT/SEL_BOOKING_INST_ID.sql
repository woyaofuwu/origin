SELECT inst_id
  FROM tf_f_user_discnt a
 WHERE a.user_id = :USER_ID
   and a.discnt_code = :DISCNT_CODE
   and a.product_id = :PRODUCT_ID