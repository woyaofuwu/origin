select t.ticket_value,t.ticket_state
  from TL_B_USER_COUPONS t
  where t.serial_number= :SERIAL_NUMBER
  and  t.ticket_code= :DISCOUNTNO
  and  t.ticket_end_date > sysdate 
  and t.ticket_state = '0'