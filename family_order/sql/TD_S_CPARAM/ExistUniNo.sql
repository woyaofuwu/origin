SELECT COUNT(1) recordcount
FROM tf_f_user_purchase
WHERE process_tag = '0'
AND end_date > SYSDATE
AND rsrv_str10 = (SELECT rsrv_str10 FROM tf_b_trade_purchase
                  WHERE trade_id = TO_NUMBER(:TRADE_ID))