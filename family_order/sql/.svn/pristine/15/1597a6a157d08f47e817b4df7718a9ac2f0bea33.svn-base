Update tf_b_trade_sale_goods  Set   remark='手机换机修改新的IMEI号,老IMEI号:'||res_code , res_code=:NEW_IMEI 
Where trade_id =:TRADE_ID
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))