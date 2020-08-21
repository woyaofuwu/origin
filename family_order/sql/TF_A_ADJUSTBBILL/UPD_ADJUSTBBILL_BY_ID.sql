UPDATE tf_a_adjustbbill
   SET recv_tag=:RECV_TAG,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),adjust_depart_id=:ADJUST_DEPART_ID,adjust_staff_id=:ADJUST_STAFF_ID  
 WHERE bill_id=TO_NUMBER(:BILL_ID)