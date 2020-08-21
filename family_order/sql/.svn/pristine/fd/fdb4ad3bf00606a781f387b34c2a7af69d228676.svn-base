select g.cust_id,u.user_id,g.cust_name,g.group_id from tf_f_cust_group g, tf_f_user u
 where u.user_id = :USER_ID
   and u.partition_id = mod(u.user_id,10000)
   and u.cust_id = g.cust_id
   and g.remove_tag= :REMOVE_TAG