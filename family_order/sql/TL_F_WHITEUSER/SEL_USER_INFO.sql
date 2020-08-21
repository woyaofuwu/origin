select serial_number, user_attr, user_level, province_code
  from tl_f_whiteuser
 where 1 = 1
   and serial_number = :USER_ID
   and (:PROV_CODE is null or province_code = :PROV_CODE )