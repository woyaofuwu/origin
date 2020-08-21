INSERT INTO tf_f_user_infochange(partition_id,user_id,product_id,brand_code,serial_number,imsi,start_date,end_date,update_time) 
SELECT mod(a.user_Id, 10000), a.User_Id, a.product_id, a.brand_code, a.serial_number, a.imsi, a.start_date, a.end_date ,sysdate
FROM tf_b_trade_infochange_bak a 
WHERE trade_id = :TRADE_ID