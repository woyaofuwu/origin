SELECT count(1) recordcount
  FROM tf_b_trade_product a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND EXISTS(SELECT 1 FROM td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=2001
                AND param_code=:PARAM_CODE
                AND para_code1=to_char(a.product_id)
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)
   AND (decode(modify_tag, '4', '0','5','1', modify_tag) = :MODIFY_TAG OR :MODIFY_TAG = '*')