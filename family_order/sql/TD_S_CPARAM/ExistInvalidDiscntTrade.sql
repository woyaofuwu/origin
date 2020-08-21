SELECT count(1) recordcount
  FROM tf_b_trade_discnt a
 WHERE a.trade_id=TO_NUMBER(:TRADE_ID)
   AND a.accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND a.modify_tag = '0'
   AND a.user_id = (SELECT user_id FROM tf_b_trade WHERE trade_id = TO_NUMBER(:TRADE_ID))
   AND EXISTS (SELECT 1 FROM tf_b_trade c
           WHERE c.trade_id = TO_NUMBER(:TRADE_ID)
             AND SUBSTR(c.process_tag_set,19,1) = '0')
   AND EXISTS (SELECT 1 FROM td_s_commpara b
             WHERE b.subsys_code = 'CSM'
               AND b.param_attr = 8859
               AND b.param_code = 'PRODUCT'
               AND b.para_code1 = '0'
               AND TRIM(b.para_code2) = :PRODUCT_ID
               AND SYSDATE < b.end_date
               AND b.eparchy_code = :EPARCHY_CODE)
   AND EXISTS  (SELECT 1 FROM td_b_product_package c,td_b_package_element d
             WHERE c.package_id = d.package_id
               AND c.product_id = :PRODUCT_ID
               AND d.element_id = a.discnt_code
               AND c.force_tag  = '1'
               AND d.Force_Tag  = '0')