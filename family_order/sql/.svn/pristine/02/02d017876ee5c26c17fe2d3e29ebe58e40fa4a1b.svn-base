SELECT COUNT(1) recordcount  FROM tf_b_trade_svc a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND  a.MODIFY_TAG = '0'
   AND EXISTS (SELECT 1 FROM td_b_element_limit_np
              WHERE element_type_code='S'
                AND limit_tag='0'
                AND element_id=to_char(a.service_id)
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)