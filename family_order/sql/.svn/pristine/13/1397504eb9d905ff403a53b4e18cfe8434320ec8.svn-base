UPDATE Tf_f_Bank_Subsign
   SET end_date         = to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'),
       update_date      = to_date(:UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss'),
       SUB_SIGN_TYPE    = :RSRV_STR16,
       MAIN_SIGN_TYPE   = :RSRV_STR13,
       update_staff_id  = :UPDATE_STAFF_ID,
       update_depart_id = :UPDATE_DEPART_ID
 WHERE SUB_USER_TYPE = :RSRV_STR14
   AND SUB_USER_VALUE = :RSRV_STR15
   AND start_date = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')