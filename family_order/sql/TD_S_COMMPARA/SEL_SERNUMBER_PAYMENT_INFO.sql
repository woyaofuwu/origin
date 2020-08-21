SELECT a.serial_number,to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date, a.make_state, a.make_terminal 
FROM TM_B_MAKECHG a, TF_F_USER b
WHERE a.serial_number=b.serial_number
  AND b.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
  AND b.user_id=TO_NUMBER(:USER_ID)
  AND a.MAKE_STATE=:MAKE_STATE
  AND NOT EXISTS(
    select 1 from tm_b_makechg c where c.serial_number=a.serial_number and c.make_state='D' and c.accept_date>a.accept_date
  )