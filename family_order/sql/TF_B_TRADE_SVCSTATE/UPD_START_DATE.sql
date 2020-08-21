update tf_b_trade_svcstate set start_date = TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') 
 	where trade_id = TO_NUMBER(:TRADE_ID)  
	AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) 
	AND modify_tag = '0' 
	and user_id = TO_NUMBER(:USER_ID)