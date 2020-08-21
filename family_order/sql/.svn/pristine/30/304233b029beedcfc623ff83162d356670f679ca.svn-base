SELECT count(*) recordcount
FROM tf_b_trade_svc  a
WHERE trade_id=to_number(:TRADE_ID)
AND a.modify_tag='0'
AND a.service_id=22
and EXISTS (SELECT 1
               FROM tf_f_user b
               WHERE a.user_id=b.user_id
                 AND b.remove_Tag='0'
                 AND b.user_state_codeset not in('0','N')
              )