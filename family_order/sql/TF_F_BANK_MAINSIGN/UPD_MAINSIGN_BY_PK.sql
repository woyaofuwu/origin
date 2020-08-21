UPDATE Tf_f_Bank_Mainsign
   SET pay_type         = :PAY_TYPE,
       update_date      = to_date(:UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss'),
       rech_threshold   = to_number(:RSRV_STR18),
       rech_amount      = to_number(:RSRV_STR19),
       remark           = :REMARK,
       update_staff_id  = :UPDATE_STAFF_ID,
       update_depart_id = :UPDATE_DEPART_ID
 WHERE USER_TYPE = :RSRV_STR12
   AND USER_VALUE = :RSRV_STR13
   AND start_date = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')