update tf_f_user_vpn_meb v set v.remove_tag = '0',v.remove_date=''
 where v.user_id = :USER_ID
   and v.partition_id = mod(to_number(:USER_ID), 10000)
   and  exists (select 1
          from tf_b_trade_vpn_meb t
         where t.trade_id = :TRADE_ID
           and t.user_id = v.user_id
           and t.user_id_a = v.user_id_a
           and exists (select 1
                  from tf_f_cust_group g, tf_f_user u
                 where u.user_id = t.user_id_a
                   and u.partition_id = mod(to_number(t.user_id_a), 10000)
                   and g.cust_id = u.cust_id
                   and g.remove_tag = '0'))