select 1
  from tf_f_user u
 where (exists
 (select 1 from  tf_f_cust_vip v where  u.user_id=v.user_id ) or exists
 (select 1 from tf_o_reduser r where u.user_id=r.user_id ) or exists
 (select 1 from tf_o_nodealuser n where  u.user_id=n.user_id)or exists 
 (select 1 from tf_f_account f where  u.cust_id=f.cust_id and f.pay_mode_code='1' ))
and u.serial_number = :SERIAL_NUMBER
and remove_tag= '0'