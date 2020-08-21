SELECT U.USER_ID, GP.GROUP_ID, U.CUST_ID,up.product_id
  FROM TF_F_CUST_GROUP GP, TF_F_USER U ,tf_F_user_product up
 WHERE 1 = 1
   AND GP.GROUP_ID = :GROUP_ID
   AND GP.REMOVE_TAG = '0'
   AND GP.CUST_ID = U.CUST_ID
   AND u.user_id = up.user_id
   AND U.REMOVE_TAG = '0'
   AND up.PRODUCT_ID = :PRODUCT_ID