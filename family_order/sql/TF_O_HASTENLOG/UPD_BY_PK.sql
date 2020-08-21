UPDATE tf_o_hastenlog
   SET hasten_mode_id=:HASTEN_MODE_ID,hasten_result_id=:HASTEN_RESULT_ID,remark=:REMARK,update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE staff_id=:STAFF_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND hasten_time=TO_DATE(:HASTEN_TIME, 'YYYY-MM-DD HH24:MI:SS')