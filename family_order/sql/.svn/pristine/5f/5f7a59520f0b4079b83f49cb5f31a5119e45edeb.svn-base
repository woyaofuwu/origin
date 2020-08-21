INSERT INTO tf_b_trade_relation
(trade_id,accept_month,relation_type_code,user_id_a,user_id_b,role_code_a,role_code_b,orderno,short_code,start_date,end_date,modify_tag,remark)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(substr(:TRADE_ID, 5, 2)),relation_type_code,user_id_a,
user_id_b,role_code_a,role_code_b,orderno,short_code,TRUNC(SYSDATE),end_date,'0',null 
from tf_b_trade_relation_uu_bak
where trade_id = TO_NUMBER(:OLDTRADE_ID) 
 AND accept_month = TO_NUMBER(SUBSTR(:OLDTRADE_ID,5,2))
 AND end_date > SYSDATE