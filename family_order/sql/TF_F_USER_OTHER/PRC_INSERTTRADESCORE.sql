DECLARE
iv_trade_id    NUMBER(16):= TO_NUMBER(:TRADE_ID);
iv_user_id     NUMBER(16):= TO_NUMBER(:USER_ID);
iv_score_changed NUMBER(11):= TO_NUMBER(:SCORE_CHANGED);
iv_remark      VARCHAR2(100):= :REMARK;
iv_accept_month NUMBER(2):=substr(:TRADE_ID,5,2);
iv_trade_staff_id      VARCHAR2(8):= :TRADE_STAFF_ID;
iv_trade_depart_id      VARCHAR2(5):= :TRADE_DEPART_ID;
iv_trade_city_code      VARCHAR2(4):= :TRADE_CITY_CODE;
iv_trade_eparchy_code      VARCHAR2(4):= :TRADE_EPARCHY_CODE;
iv_partition_id NUMBER(4):=MOD(iv_user_id,10000);
iv_score_value NUMBER(11);
iv_score_type_code char(1);
iv_serial_number VARCHAR2(15);
iv_cust_id     NUMBER(16);
iv_acyc_id     NUMBER(6);
iv_bcyc_id     char(4);
iv_brand_code char(4);
BEGIN
:CODE:= -1;
:INFO:='TRADE OK!';
IF :TRADE_ID IS NULL OR  :USER_ID IS NULL OR :SCORE_CHANGED IS NULL OR :TRADE_STAFF_ID IS null or :TRADE_DEPART_ID IS null or :TRADE_CITY_CODE IS null or :TRADE_EPARCHY_CODE IS null then
:INFO:='输入参数不全';
RETURN;
END IF;
BEGIN
SELECT brand_code,score_value,serial_number INTO iv_brand_code,iv_score_value,iv_serial_number FROM tf_f_user
WHERE user_id=iv_user_id AND partition_id=iv_partition_id;
EXCEPTION
WHEN OTHERS THEN
:INFO:='获取用户积分台帐异常';
RETURN;
END;
IF iv_brand_code = 'G010' THEN
iv_score_type_code :='e';
ELSE
iv_score_type_code :='b';
END IF;
INSERT INTO tf_b_trade_scoresub(trade_id,accept_month,user_id,action_code,score_type_code,score_changed_sub,remark)
values(iv_trade_id,iv_accept_month,iv_user_id,-1,iv_score_type_code,iv_score_changed,iv_remark);
INSERT INTO tf_b_trade_score
(trade_id,accept_month,user_id,serial_number,score,score_changed,value_changed,remark)
VALUES
(iv_trade_id,iv_accept_month,iv_user_id,iv_serial_number,iv_score_value,iv_score_changed,0,iv_remark);
insert into tf_b_trade(trade_id,subscribe_id,trade_type_code,in_mode_code,priority,
subscribe_state,product_id,user_id,cust_id,serial_number,
accept_date,accept_month,trade_staff_id,trade_depart_id,trade_city_code,
trade_eparchy_code,eparchy_code,exec_time,finish_date,fee_state,cancel_tag,
process_tag_set)
values(iv_trade_id,iv_trade_id,360,'3',250,
'9',-1,iv_user_id,iv_cust_id,iv_serial_number,
sysdate,iv_accept_month,iv_trade_staff_id,iv_trade_depart_id,iv_trade_city_code,
iv_trade_eparchy_code,iv_trade_eparchy_code,sysdate,sysdate,'0','0',
'0');
:CODE:= 0;
END;