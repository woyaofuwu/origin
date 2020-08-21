update tf_F_feereg 
 set fee_money = :FEE_MONEY,
     state = :STATE,
     reg_date = to_date(:REG_DATE,'yyyy-mm-dd hh24:mi:ss'),
     reg_staff_id = :REG_STAFF_ID,
     reg_depart_id = :REG_DEPART_ID,
     update_time = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss'),
     update_staff_id = :UPDATE_STAFF_ID,
     update_depart_id = :UPDATE_DEPART_ID,
     remark = :REMARK
 where log_id = :LOG_ID
  and fee_type_code = :FEE_TYPE_CODE