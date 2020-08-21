DECLARE
iv_user_id NUMBER(16):=TO_NUMBER(:USER_ID);
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
SET cust_id=usecust_id
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000);
END LOOP;
:CODE:=0;
END;