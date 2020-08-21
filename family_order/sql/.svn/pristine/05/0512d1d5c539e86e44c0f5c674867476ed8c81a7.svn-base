select decode(RSRV_STR4,'0','普通有价值客户','1','特殊身份客户','2','被担保客户','其他') RSRV_STR4,START_DATE,RSRV_STR1,RSRV_STR2,RSRV_STR3
from tf_f_user_other
where user_id = (select user_id from tf_F_user where serial_number =to_char(:SERIAL_NUMBER) AND remove_tag = '0')
and rsrv_value_code = 'CRED'
and sysdate between start_date and end_date