UPDATE tf_b_trade_svc a
   SET modify_tag='9',end_date = start_date
 WHERE trade_id=:TRADE_ID
   AND accept_month = :ACCEPT_MONTH
   AND modify_tag='0'
   AND EXISTS (SELECT 1 FROM td_b_attr_itema
                WHERE id=a.service_id
                 AND attr_can_null='2'               
                 AND (eparchy_code='ZZZZ' OR eparchy_code =:EPARCHY_CODE))   
   AND (NOT EXISTS (SELECT 1 FROM tf_b_trade_attr
                     WHERE trade_id =:TRADE_ID
                       AND user_id = a.user_id
                       AND accept_month = :ACCEPT_MONTH
                       AND inst_id = a.inst_id
                       AND inst_type='S')
        OR EXISTS (SELECT 1 FROM tf_b_trade_attr
                    WHERE trade_id =:TRADE_ID
                    AND user_id = a.user_id
                    AND accept_month = :ACCEPT_MONTH
                    AND inst_id = a.inst_id
                    AND inst_type='S'
                    AND trim(attr_value) is null))