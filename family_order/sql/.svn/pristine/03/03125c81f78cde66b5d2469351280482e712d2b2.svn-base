select decode(RSRV_STR5,'0','个人担保','1','集团担保') RSRV_STR5,decode(RSRV_STR4,'0','普通有价值客户','1','特殊身份客户','2','被担保客户') RSRV_STR4,start_date,RSRV_STR2,RSRV_STR6
from tf_f_user_other
where user_id = (select user_id from tf_F_user where serial_number =to_char(:SERIAL_NUMBER) AND remove_tag = '0')
AND rsrv_value_code = 'CRED'
and sysdate between start_date and end_date