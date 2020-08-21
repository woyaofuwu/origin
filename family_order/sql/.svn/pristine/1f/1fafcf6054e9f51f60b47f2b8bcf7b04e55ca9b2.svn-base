SELECT b.service_id ,b.open_tag modify_tag
  FROM td_s_trade_svc b
 WHERE b.trade_type_code=:TRADE_TYPE_CODE
   AND (b.brand_code=:BRAND_CODE OR b.brand_code='ZZZZ')
   AND (b.product_id=:PRODUCT_ID OR b.product_id=-1)
   AND (b.eparchy_code=:EPARCHY_CODE OR b.eparchy_code='ZZZZ')
   AND NOT EXISTS (SELECT 1 FROM Tf_b_Trade_Svc c
                   WHERE c.trade_id = TO_NUMBER(:TRADE_ID)
                   AND c.service_id = b.service_id
                   AND c.modify_tag IN ('0','1'))
   AND EXISTS (SELECT 1 FROM tf_f_user_svc
                WHERE user_id = TO_NUMBER(:USER_ID)
                  AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                  AND service_id = b.service_id
                  AND SYSDATE BETWEEN start_date AND end_date)