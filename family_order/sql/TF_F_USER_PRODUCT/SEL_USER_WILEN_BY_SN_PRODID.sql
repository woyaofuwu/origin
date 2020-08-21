select t.product_id,t1.serial_number
  from TF_F_USER_PRODUCT t, tf_F_user t1
 where t.user_id = t1.user_id
   and t1.remove_tag = '0'
	 AND SYSDATE < T.END_DATE
   and t.product_id = :PRODUCT_ID
	 and t1.serial_number=:SERIAL_NUMBER
