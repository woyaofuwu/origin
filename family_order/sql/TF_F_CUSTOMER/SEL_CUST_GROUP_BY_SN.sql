select A.serial_number,B.cust_name,C.group_id,C.group_cust_name,D.CITY_CODE,D.CUST_MANAGER_ID 
  from tf_f_user A 
  left join tf_f_customer B on(A.cust_id=B.cust_id) 
  left join tf_f_cust_groupmember C on(A.user_id=C.user_id) 
  left join tf_f_cust_group D on(C.cust_id=D.cust_id)
 where A.serial_number=:SERIAL_NUMBER