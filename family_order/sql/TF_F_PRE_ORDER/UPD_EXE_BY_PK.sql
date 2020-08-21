UPDATE tf_f_pre_order
   SET order_status=:ORDER_STATUS,exe_staff_id=:EXE_STAFF_ID,exe_depart_id=:EXE_DEPART_ID,exe_date=TO_DATE(:EXE_DATE, 'YYYY-MM-DD HH24:MI:SS'),exe_result=:EXE_RESULT  
 WHERE pre_order_id=:PRE_ORDER_ID