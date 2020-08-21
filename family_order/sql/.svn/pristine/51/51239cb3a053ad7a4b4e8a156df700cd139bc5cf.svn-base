Select count(1) recordcount
FROM tf_f_user T,tf_f_customer t1
 where t.cust_id=t1.cust_id
 and T.user_id = TO_NUMBER(:USER_ID)
 AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) 
 AND t.remove_tag='0'
 and (t1.is_real_name='0' or t1.is_real_name is null)