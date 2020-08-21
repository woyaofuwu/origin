UPDATE tf_a_bankpacket
   SET serial_number=:SERIAL_NUMBER,make_state=:MAKE_STATE,make_terminal=:MAKE_TERMINAL,cancel_staff_id=:CANCEL_STAFF_ID,cancel_depart_id=:CANCEL_DEPART_ID,remark=:REMARK,end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') 
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND end_date>sysdate