SELECT rent_type_code  para_code1,
       rent_type_name  para_code2,
       rent_serial_number  para_code3,
       rent_fee_code  para_code4,
       NATIONALITY_AREACODE  para_code5,
       rent_time  para_code7,
       rent_imei  para_code8,
       city_code para_code11
  FROM td_s_commpara a
 WHERE param_attr = TO_NUMBER(:PARAM_ATTR)
   AND subsys_code = 'CSM'
   AND rent_type_code = :RENT_TYPE_CODE
   AND para_code3 = :PARA_CODE
   AND city_code = :PARA_CODE11
   AND not exists (select 1
          from tf_f_user_rent
         where rent_serial_number = a.rent_serial_number
           AND rent_tag = '1')
   AND not exists (select 1
          from tf_f_user_rent
         where rent_serial_number = a.rent_serial_number
           AND end_date > sysdate)