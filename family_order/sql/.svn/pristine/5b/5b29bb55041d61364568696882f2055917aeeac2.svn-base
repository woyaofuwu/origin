UPDATE tf_f_spanopen
   SET (subscribe_state,confirm_open_area,confirm_open_date,dest_staff_name,
   dest_staff_id,dest_staff_mobile,dest_staff_phone,dest_staff_fax,dest_staff_email,
   confirm_spec_num) = 
   (SELECT '1', confirm_open_area,confirm_open_date,dest_staff_name,
   dest_staff_id,dest_staff_mobile,dest_staff_phone,dest_staff_fax,dest_staff_email,
   confirm_spec_num
   FROM tf_b_trade_spanopen WHERE trade_id=TO_NUMBER(:TRADE_ID))
 WHERE booking_id=(SELECT booking_id FROM tf_b_trade_spanopen WHERE trade_id=TO_NUMBER(:TRADE_ID))