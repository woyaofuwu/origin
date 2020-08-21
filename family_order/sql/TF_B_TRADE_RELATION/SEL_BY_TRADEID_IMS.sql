select t1.USER_ID_A,t1.SERIAL_NUMBER_A,t1.USER_ID_B,t1.SERIAL_NUMBER_B,t1.RELATION_TYPE_CODE,t1.ROLE_CODE_A
,t1.ROLE_CODE_B,t1.ORDERNO,t1.SHORT_CODE
from tf_b_trade_relation t,tf_f_relation_uu t1 
where t.user_id_b = t1.user_id_a and t.relation_type_code = '04' and t1.relation_type_code='03'
and t.trade_id =TO_NUMBER(:TRADE_ID)
and t1.START_DATE<=SYSDATE   
and t1.END_DATE>SYSDATE