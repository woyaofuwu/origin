SELECT rent_type_code,
       rent_type  para_code2,
       rent_serial_number  para_code3,
       rent_cost  para_code4,
       NATIONALITY_AREACODE  para_code5,
       rent_in_time  para_code7,
       rent_imei  para_code8,
       city_code para_code11,
       rsrv_date1 rsrv_date
  FROM ti_o_rentcode a
 WHERE rent_type_code = :RENT_TYPE_CODE
   AND RENT_DEVICE_NO = :PARA_CODE
   AND city_code = :CITY_CODE
   AND not exists (select 1
          from tf_f_user_rent
         where rent_serial_number = a.rent_serial_number
           AND rent_tag = '1')
   AND not exists (select 1
          from tf_f_user_rent
         where rent_serial_number = a.rent_serial_number
           AND end_date > sysdate)