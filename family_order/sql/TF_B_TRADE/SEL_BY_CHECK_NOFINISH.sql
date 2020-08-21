SELECT COUNT(1) recordcount
  FROM tf_b_trade a
 WHERE  a.cust_id = TO_NUMBER(:CUST_ID)
   and a.user_id = TO_NUMBER(:USER_ID)
   and a.trade_type_code = TO_NUMBER(:TRADE_TYPE_CODE)
   AND a.cancel_tag = '0'
   AND ROWNUM <  2