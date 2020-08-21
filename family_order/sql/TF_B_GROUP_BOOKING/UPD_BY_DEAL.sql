UPDATE tf_b_group_booking
   SET deal_tag=:DEAL_TAG,deal_staff_id=:DEAL_STAFF_ID,deal_time=TO_DATE(:DEAL_TIME, 'YYYY-MM-DD HH24:MI:SS'),deal_desc=:DEAL_DESC  
 WHERE command=:COMMAND
   AND apply_phone=:APPLY_PHONE
   AND apply_time=TO_DATE(:APPLY_TIME, 'YYYY-MM-DD HH24:MI:SS')