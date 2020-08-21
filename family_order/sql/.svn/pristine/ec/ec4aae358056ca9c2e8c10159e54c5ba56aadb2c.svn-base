INSERT INTO tf_f_user_other(partition_id,user_id,rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,start_date,end_date,update_time, inst_id)
select mod(to_number(:USER_ID),10000),to_number(:USER_ID),rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,start_date,end_date,sysdate, inst_id 
from tf_b_trade_other
where trade_id=:TRADE_ID
  and modify_tag='0'