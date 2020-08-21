SELECT count(1) recordcount
  FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND discnt_code in (SELECT to_number(para1) FROM tp_o_credit_temppara  WHERE paracode=:PARACODE)
   AND (modify_tag = :MODIFY_TAG OR :MODIFY_TAG = '*')