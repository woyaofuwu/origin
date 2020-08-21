INSERT INTO tf_b_trade_discnt
(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),substr(:TRADE_ID,5,2),TO_NUMBER(:USER_ID),'1',TO_NUMBER(:DISCNT_CODE),'0',start_date,end_date
FROM tf_f_user_svc
WHERE service_id+0=TO_NUMBER(:SERVICE_ID)
AND user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND end_date+0>SYSDATE AND ROWNUM<2
AND NOT EXISTS (SELECT 1 FROM tf_b_trade_svc WHERE trade_id=TO_NUMBER(:TRADE_ID) AND service_id=TO_NUMBER(:SERVICE_ID))
AND NOT EXISTS (SELECT 1 FROM tf_b_trade_discnt WHERE trade_id=TO_NUMBER(:TRADE_ID) AND discnt_code=TO_NUMBER(:DISCNT_CODE))
AND NOT EXISTS (SELECT 1 FROM tf_f_user_discnt WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND discnt_code+0=TO_NUMBER(:DISCNT_CODE) AND end_date+0>SYSDATE)