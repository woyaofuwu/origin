update tf_f_user set open_date = to_date(:OPEN_DATE,'yyyymmddhh24miss') where serial_number = :SERIAL_NUMBER and remove_tag='0'