select 1
  from tf_f_user t,TF_F_USER_PRODUCT b
 where t.serial_number = :SERIAL_NUMBER
   and t.user_id=b.user_id
   and t.remove_tag='0'
	 and b.product_id = TO_NUMBER(:PRODUCT_ID)
	 and sysdate < b.end_date