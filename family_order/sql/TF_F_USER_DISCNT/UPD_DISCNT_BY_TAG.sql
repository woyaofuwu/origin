UPDATE tf_f_user_discnt a 
SET a.end_date =(SELECT b.end_date FROM tf_b_trade_discnt_bak b
                 WHERE b.trade_id=TO_NUMBER(:TRADE_ID)
                 AND b.accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                 AND b.discnt_code=(:DISCNT_CODE)
                 AND b.start_date =to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS')),
                 update_time=sysdate
WHERE USER_ID=TO_NUMBER(:USER_ID)
AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)
AND a.discnt_code =:DISCNT_CODE
AND a.start_date = to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS')