DECLARE
iv_trade_id    NUMBER(16):= TO_NUMBER(:TRADE_ID);
iv_accept_month NUMBER(2):=substr(:TRADE_ID,5,2);
BEGIN
:CODE:= -1;
:INFO:='TRADE OK!';
:USER_ID:=' ';
IF :TRADE_ID IS NULL THEN
:INFO:='输入参数不全';
RETURN;
END IF;
update tf_bh_trade
set cancel_tag='1'
where trade_id=iv_trade_id and accept_month=iv_accept_month and cancel_tag='0';
IF SQL%ROWCOUNT=0 THEN
:INFO:='没有找到用户主台帐';
RETURN;
END IF;
select user_id into :USER_ID 
from tf_bh_trade
where trade_id=iv_trade_id and accept_month=iv_accept_month and cancel_tag='1';
insert into tf_bh_trade(trade_id,trade_type_code,in_mode_code,priority,
subscribe_state,product_id,user_id,cust_id,serial_number,
accept_date,accept_month,trade_staff_id,trade_depart_id,trade_city_code,
trade_eparchy_code,exec_time,finish_date,fee_state,cancel_tag,
process_tag_set)
select trade_id,trade_type_code,in_mode_code,priority,
subscribe_state,product_id,user_id,cust_id,serial_number,
accept_date,accept_month,trade_staff_id,trade_depart_id,trade_city_code,
trade_eparchy_code,exec_time,finish_date,fee_state,'2',
process_tag_set from tf_bh_trade
where trade_id=iv_trade_id;
:CODE:= 0;
END;