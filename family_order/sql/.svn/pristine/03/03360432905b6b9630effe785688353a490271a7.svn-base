SELECT count(1) recordcount
  FROM tf_b_trade a
 WHERE a.trade_id=TO_NUMBER(:TRADE_ID)
   AND a.accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND SUBSTR(a.process_tag_set,19,1) = '1'
   AND NOT EXISTS (SELECT 1 FROM td_s_commpara b
   					WHERE b.subsys_code = 'CSM'
   					  AND b.param_attr = 243
   					  AND b.param_code = '1'
   					  AND TRIM(b.para_code1) = a.product_id
   					  AND SYSDATE < b.end_date
   					  AND b.eparchy_code = :EPARCHY_CODE)