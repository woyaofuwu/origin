select v.vpn_no from tf_f_relation_uu u,tf_f_user_vpn v  
where 1=1 
and v.vpn_user_code = '2'
and v.remove_tag = '0'
and v.user_id = u.user_id_a 
and v.PARTITION_ID = mod(u.user_id_a,10000)
and u.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND u.user_id_b=TO_NUMBER(:USER_ID)
AND u.relation_type_code in ('20','25','E1')
AND SYSDATE between u.start_date and u.end_date
and rownum < 2