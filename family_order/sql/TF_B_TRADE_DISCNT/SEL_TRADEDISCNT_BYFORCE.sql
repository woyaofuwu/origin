SELECT to_char(trade_id) trade_id,accept_month,to_char(USER_ID) USER_ID,discnt_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
from tf_b_trade_discnt 
where (modify_tag = :MODIFY_TAG or :MODIFY_TAG = '*') and trade_id = TO_NUMBER(:TRADE_ID)
and discnt_code in(select discnt_code from td_b_product_discnt 
                   where product_id = :PRODUCT_ID and (forcegroup_tag = :FORCE_TAG) 
                   and end_date > sysdate)
                   AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))