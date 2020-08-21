UPDATE tf_b_res_quality
   SET time_in=sysdate,staff_id_in=:STAFF_ID_IN,deal_state_code=:DEAL_STATE_CODE,bad_type_code=:BAD_TYPE_CODE,declare_info=:DECLARE_INFO,remark=:REMARK  
 WHERE log_id=TO_NUMBER(:LOG_ID)