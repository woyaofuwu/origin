SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,product_id,brand_code,old_product_id,old_brand_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_b_trade_product
where user_id =TO_NUMBER(:USER_ID) and trunc(start_date,'mm')= TRUNC(SYSDATE,'mm')