UPDATE tf_f_user_plat_order
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-1/24/3600,remark=:REMARK  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') between start_date and end_date
   AND biz_type_code IN (SELECT biz_type_code FROM tf_b_trade_plat_register
                          WHERE partition_id = MOD(:TRADE_ID,10000)
                            AND trade_id = :TRADE_ID
                            AND oper_code = '80'
                            AND open_tag = '1111')