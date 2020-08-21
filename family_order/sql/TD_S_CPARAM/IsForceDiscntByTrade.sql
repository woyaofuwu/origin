SELECT COUNT(1) recordcount
  FROM tf_b_trade_discnt a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND (decode(modify_tag, '4', '0','5','1', modify_tag) = :MODIFY_TAG OR :MODIFY_TAG = '*')
   AND user_id=to_number(:USER_ID)
   AND EXISTS (SELECT 1 FROM td_b_product_discnt WHERE a.discnt_code=discnt_code
    AND product_id=to_number(:PRODUCT_ID) AND (force_tag = '1' OR forcegroup_tag = '1') AND end_date>SYSDATE)