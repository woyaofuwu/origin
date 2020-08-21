UPDATE tf_f_receipts
   SET remove_tag=:REMOVE_TAG  
 WHERE receipts_id=TO_NUMBER(:RECEIPTS_ID)