SELECT COUNT(1) recordcount FROM tf_f_user_purchase
WHERE user_id=:USER_ID
   AND (purchase_mode=:PURCHASE_MODE OR :PURCHASE_MODE='**')
   AND process_tag=:PROCESS_TAG
   AND FOREGIFT_CODE = :FOREGIFT_CODE
   AND end_date > sysdate