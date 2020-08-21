SELECT b.serial_number
FROM TM_B_MAKECHG a, TF_F_USER b
WHERE a.serial_number=b.serial_number
  AND a.make_state=:STATE_CODE
  AND b.partition_id=MOD(:USER_ID,10000)
  AND b.user_id=:USER_ID
  AND NOT EXISTS(
    select 1 from tm_b_makechg c where c.serial_number=a.serial_number and c.make_state='D' and c.accept_date>a.accept_date
  )