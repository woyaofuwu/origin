UPDATE tf_f_receipts
   SET stock_id=:STOCK_ID  
 WHERE receipts_id=TO_NUMBER(:RECEIPTS_ID)