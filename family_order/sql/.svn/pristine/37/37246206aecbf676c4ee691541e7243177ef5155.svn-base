SELECT count(*) recordcount
  FROM td_a_transmode_limit T
 WHERE ID in
       (select OLD_PRODUCT_ID
          from tf_b_trade_product a, tf_f_user_trans b
         where a.trade_id = to_number(:TRADE_ID)
           AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND a.user_id = b.user_id
           and b.para_code='3003'
           and b.process_tag='0'
           and b.user_id = to_number(:USER_ID)
           AND T.PARA_CODE = B.PARA_CODE)
   AND ID_TYPE = '0'
   AND LIMIT_TAG = '2'