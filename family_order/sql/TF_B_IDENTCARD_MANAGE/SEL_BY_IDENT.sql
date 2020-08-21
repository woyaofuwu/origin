select t.*,
       (case
         when t.end_date >= sysdate then
          'VALID'
        else
          'EXPIRE'
       end) as TAG from TF_B_IDENTCARD_MANAGE t where t.ident_code = :IDENT_CODE and t.serial_number = :SERIAL_NUMBER