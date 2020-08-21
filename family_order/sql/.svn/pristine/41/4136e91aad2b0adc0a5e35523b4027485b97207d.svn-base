SELECT COUNT(1) recordcount
  FROM tf_f_user_purchase
 WHERE user_id=:USER_ID
   AND (purchase_mode=:PURCHASE_MODE OR :PURCHASE_MODE='**')
   AND process_tag=:PROCESS_TAG
   AND (start_date>to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') OR :START_DATE IS NULL)