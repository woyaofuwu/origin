DECLARE
iv_user_id NUMBER(16):=TO_NUMBER(:USER_ID);
iv_acct_id NUMBER(16):=TO_NUMBER(:ACCT_ID);
iv_cust_id NUMBER(16):=TO_NUMBER(:CUST_ID);
iv_cust_name VARCHAR2(100):=:CUST_NAME;
iv_trade_type_code NUMBER:=:TRADE_TYPE_CODE;
iv_trade_eparchy_code CHAR(4):=:TRADE_EPARCHY_CODE;
BEGIN
:CODE:=-1;
:INFO:='TRADE OK!';
FOR i IN (SELECT 1 FROM td_s_tradetype
WHERE trade_type_code=iv_trade_type_code
AND INSTR('ZZZZ'||eparchy_code,iv_trade_eparchy_code)>0
AND SUBSTRB(tag_set,-1)='1') LOOP
UPDATE tf_f_user
SET cust_id=iv_cust_id,/*usecust_id=iv_cust_id,*/open_mode=DECODE(open_mode,'1','2',open_mode)
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000);
/*
UPDATE tf_f_account
SET cust_id=iv_cust_id,pay_name=SUBSTRB(iv_cust_name,1,50)
WHERE acct_id=iv_acct_id AND partition_id=MOD(iv_acct_id,10000);
*/
END LOOP;
:CODE:=0;
END;