SELECT count(*) recordcount
  FROM td_a_ingw_commpara a, tf_b_trade b
 WHERE a.para_attr = 1290
   AND b.trade_id = :TRADE_ID
   AND b.cancel_tag = '0'
   AND to_number(a.para_code) = b.product_id
   AND SYSDATE BETWEEN a.para_date7 AND a.para_date8
   AND to_number(para_code1) <= to_number(para_code2)