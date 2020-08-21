update tf_f_user_blackwhite b
   set b.end_date = to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')
 where b.user_id = :USER_ID
   and b.PARTITION_ID = mod(to_number(:USER_ID), 10000)
   and EXISTS (select 1
          from tf_b_trade_blackwhite t
         where t.trade_id = :TRADE_ID
           and t.user_id = b.user_id
           and t.group_id = b.group_id
           and EXISTS (SELECT 1
                  FROM TF_F_CUST_GROUP G
                 WHERE g.group_id = t.group_id
                   AND G.REMOVE_TAG = '0'))