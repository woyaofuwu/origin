UPDATE tf_f_user_rent
   SET end_date=sysdate,rent_tag='0',open_staff_id=:OPEN_STAFF_ID,open_depart_id=:OPEN_DEPART_ID  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND rent_serial_number=:RENT_SERIAL_NUMBER
   AND nationality_areacode=:NATIONALITY_AREACODE
   AND end_date > sysdate