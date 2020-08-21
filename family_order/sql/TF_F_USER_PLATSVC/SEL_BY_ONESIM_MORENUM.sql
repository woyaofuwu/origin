select serial_number_b 
from tf_f_relation_uu 
where  sysdate between start_date and end_date
and relation_type_code = '30' 
and role_code_b <> '1' 
and serial_number_b = :SERIAL_NUMBER