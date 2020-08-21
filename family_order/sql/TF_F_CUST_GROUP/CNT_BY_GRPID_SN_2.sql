select count(*)
  from  tf_f_user u, tf_f_relation_uu r, tf_f_user_discnt ud
where   r.user_id_b = u.user_id
  and   u.partition_id = mod(u.user_id,10000)
  and   u.remove_tag = '0'
  and   ud.user_id = u.user_id 
  and   ud.user_id_a > -1
  and   r.start_date < sysdate 
  and   r.end_date > sysdate 
  and   u.serial_number = :SERIAL_NUMBER
  and   exists (
      select  1
        from  tf_f_user u,tf_f_cust_group g
       where  u.cust_id = g.cust_id  and r.user_id_a = u.user_id and u.partition_id = mod(r.user_id_a, 10000)
         and  group_id =:GROUP_ID 
         and  g.remove_tag='0' 
         and  u.remove_tag = '0' 
         and  u.partition_id = mod(u.user_id,10000)
         and  g.remove_tag = '0'
  )