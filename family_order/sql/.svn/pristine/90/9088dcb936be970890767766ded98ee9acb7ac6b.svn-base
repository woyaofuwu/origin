SELECT count(1) recordcount
  FROM tf_b_trade_discnt a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND (decode(modify_tag, '4', '0','5','1', modify_tag) = :MODIFY_TAG OR :MODIFY_TAG = '*')
   AND user_id_a=to_number(:USER_ID)
   AND NOT EXISTS (SELECT 1 FROM td_b_product_discnt b WHERE a.discnt_code=b.discnt_code AND b.product_id=to_number(:PRODUCT_ID))
   AND NOT EXISTS (SELECT 1 FROM td_s_commpara c WHERE c.param_attr=258
                    AND a.discnt_code=to_number(c.para_code1) AND (SYSDATE BETWEEN c.start_date AND c.end_date)
                    AND (c.eparchy_code=:EPARCHY_CODE OR :EPARCHY_CODE='ZZZZ'))