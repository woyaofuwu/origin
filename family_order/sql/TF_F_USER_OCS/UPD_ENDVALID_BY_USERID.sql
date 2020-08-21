UPDATE tf_f_user_ocs
   SET end_date = TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),
       update_time = SYSDATE,
       update_staff_id = NVL(:UPDATE_STAFF_ID,update_staff_id)
       update_depart_id = NVL(:UPDATE_DEPART_ID,update_depart_id)
       remark = NVL(:REMARK,remark)
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date > SYSDATE