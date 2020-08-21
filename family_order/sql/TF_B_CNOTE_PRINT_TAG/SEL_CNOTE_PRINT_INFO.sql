select *
  from TF_B_CNOTE_PRINT_TAG b
  where 1=1
  and (b.serial_number = :SERIAL_NUMBER or b.serial_number = 'KD_'||:SERIAL_NUMBER)
   and b.accept_date + 0 >= TO_DATE(:START_DATE, 'YYYY-MM-DD')
   and b.accept_date + 0 <= TO_DATE(:END_DATE, 'YYYY-MM-DD')
   and b.update_staff_id = :STAFF_ID 