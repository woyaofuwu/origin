Select a.city_code, a.staff_id, a.staff_name, a.depart_code, a.depart_name,a.serial_number
  From TD_M_STAFF_B a
 Where 1=1
   And a.serial_number = :SERIAL_NUMBER
   And a.staff_id = :STAFF_ID
   And a.staff_name Like '%'||:STAFF_NAME||'%'
   And a.city_code = :CITY_CODE
   Order By a.depart_code