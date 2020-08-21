SELECT COUNT(1) recordcount
  FROM tf_b_trade a
 WHERE a.trade_type_code in(10,11)
   AND a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   --AND (substr(a.process_tag_set,9,1) = '1'
   -- OR substr(a.process_tag_set,9,1) = '3')
   AND (EXISTS(SELECT 1 FROM tf_b_trade
                WHERE a.serial_number = serial_number
                  --AND trade_type_code = a.trade_type_code
                  AND trade_id != a.trade_id)
    OR EXISTS(SELECT 1 FROM tf_f_user
               WHERE a.serial_number = serial_number
                 AND remove_tag = '0'))