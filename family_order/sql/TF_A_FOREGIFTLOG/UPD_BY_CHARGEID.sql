UPDATE tf_a_foregiftlog
   SET cancel_tag=:CANCEL_TAG,cancel_time=TO_DATE(:CANCEL_TIME, 'YYYY-MM-DD HH24:MI:SS'),cancel_staff_id=:CANCEL_STAFF_ID,cancel_depart_id=:CANCEL_DEPART_ID  
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)