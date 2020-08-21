SELECT inst_id
  FROM tf_f_user_svc a
 WHERE a.user_id = :USER_ID
   and a.service_id = :SERVICE_ID
   and a.product_id = :PRODUCT_ID