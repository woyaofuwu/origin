select count(1) recordcount
from tf_b_trade_other where trade_id = to_number(:TRADE_ID) and rsrv_str2 in(
select user_id_b from tf_f_relation_uu where end_date>sysdate and user_id_a =(
select user_id_a from tf_f_relation_uu where user_id_b = to_number(:USER_ID)
and role_code_b = '1' and relation_type_code = :RELATION_TYPE_CODE and end_date>sysdate))