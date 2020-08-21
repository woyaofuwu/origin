UPDATE tf_f_cust_vip
   SET destroy_date=TO_DATE(:DESTROY_DATE, 'YYYY-MM-DD HH24:MI:SS'),destroy_reason=:DESTROY_REASON,destroy_staff_id=:DESTROY_STAFF_ID,remove_tag=:REMOVE_TAG,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND remove_tag=:REMOVE_TAG