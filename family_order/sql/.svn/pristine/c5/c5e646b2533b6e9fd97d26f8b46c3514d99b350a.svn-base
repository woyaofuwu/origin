INSERT INTO tf_b_trade_res(trade_id,accept_month,res_type_code,res_code,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),res_type_code,res_code,'1',start_date,TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss')
  FROM tf_f_user_res a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date > SYSDATE
   AND end_date > start_date
   AND NOT EXISTS (SELECT 1 FROM tf_b_trade_res
                    WHERE trade_id = TO_NUMBER(:TRADE_ID)
                      AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                      AND res_type_code = a.res_type_code
                      AND res_code = a.res_code)