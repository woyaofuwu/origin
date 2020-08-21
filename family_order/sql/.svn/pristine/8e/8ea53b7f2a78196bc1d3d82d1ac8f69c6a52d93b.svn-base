DECLARE
iv_user_id NUMBER := TO_NUMBER(:USER_ID);
iv_trade_type_code NUMBER(4) := :TRADE_TYPE_CODE;
iv_brand_code CHAR(4) := :BRAND_CODE;
iv_curdate DATE := SYSDATE;
iv_start_date DATE;
iv_count NUMBER;
BEGIN
:CODE := -1;
:INFO := 'TRADE OK!';
:X_TAG := 1;--1可以办理
IF iv_trade_type_code=470 THEN
IF iv_brand_code IN ('G001','G010') THEN
:X_TAG := 0;
GOTO lable;
END IF;
SELECT COUNT(1)
INTO iv_count
FROM tf_f_user_other
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000) AND rsrv_value_code='ScRg' AND iv_curdate BETWEEN start_date AND end_date;
IF iv_count>0 THEN    
:X_TAG := 0;
GOTO lable;
END IF;
SELECT COUNT(1)
INTO iv_count
FROM tf_f_user_other
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000) AND rsrv_value_code='2005' AND iv_curdate BETWEEN start_date AND end_date;
IF iv_count=0 THEN
:X_TAG := 0;
GOTO lable;
END IF;
ELSIF  iv_trade_type_code=471 THEN
BEGIN
SELECT start_date
INTO iv_start_date
FROM tf_f_user_other
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000) AND rsrv_value_code='ScRg' AND iv_curdate BETWEEN start_date AND end_date;
EXCEPTION
WHEN OTHERS THEN
:X_TAG := 0;
GOTO lable;
END;
END IF;
<<lable>>
:CODE := 0;
END;