INSERT INTO tf_b_trade_svcstate(trade_id,accept_month,user_id,service_id,state_code,modify_tag,start_date,end_date)
select TO_NUMBER(:TRADE_ID),:ACCEPT_MONTH,TO_NUMBER(:USER_ID),0,STATE_CODE,'1',TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
from tf_f_user_svcstate
where PARTITION_ID=mod(TO_NUMBER(:USER_ID),10000)
 and user_id=TO_NUMBER(:USER_ID)
 and SERVICE_ID=0
 and STATE_CODE in ('5','7','A','B','C','D')
 and end_date>=sysdate