SELECT  to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,product_id,brand_code,old_product_id,
old_brand_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') 
start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
 FROM TF_B_TRADE_PRODUCT 
 WHERE TRADE_ID=TO_NUMBER(:TRADE_ID)
 AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
 AND LAST_DAY(END_DATE)=trunc(END_DATE)