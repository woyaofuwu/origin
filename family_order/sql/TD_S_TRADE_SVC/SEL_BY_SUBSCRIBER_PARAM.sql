SELECT DISTINCT a.param_code,'' param_value,a.must_tag
FROM TD_O_SUBSCRIBER_SVC_VAR a, td_o_sUBSCRIBER_VAR b,
     td_s_trade_svc c
WHERE a.service_id = to_char(c.service_id)
AND  a.param_code = b.param_code
AND c.trade_type_code = :TRADE_TYPE_CODE
AND (c.brand_code=:BRAND_CODE OR c.brand_code='ZZZZ')
AND (c.product_id=:PRODUCT_ID OR c.product_id=-1)
AND (c.eparchy_code=:EPARCHY_CODE OR c.eparchy_code='ZZZZ')
AND EXISTS
(SELECT 1 FROM tf_f_user_svc
                WHERE user_id = TO_NUMBER(:USER_ID)
                  AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                  AND service_id = c.service_id
                  AND SYSDATE BETWEEN start_date AND end_date)
AND NOT EXISTS (SELECT 1 FROM Tf_b_Trade_Svc d
                   WHERE d.trade_id = TO_NUMBER(:TRADE_ID)
                   and d.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) 
                   AND d.service_id = c.service_id                   
                   AND d.modify_tag IN ('0','1'))