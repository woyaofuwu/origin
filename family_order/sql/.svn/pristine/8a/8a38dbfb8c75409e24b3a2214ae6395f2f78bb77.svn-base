SELECT 
       integrate_item_code,
       integrate_item,
       to_char(sum(fee)) fee
  FROM ts_a_usergroupbill
 WHERE user_id IN (SELECT user_id
                     from tf_a_payrelation
                    where acct_id = :ACCT_ID 
                      and start_acyc_id <= :ACYC_ID
                      AND :ACYC_ID <= END_ACYC_ID
                      AND default_tag = '1')
   AND acyc_id = :ACYC_ID  group by integrate_item_code,integrate_item