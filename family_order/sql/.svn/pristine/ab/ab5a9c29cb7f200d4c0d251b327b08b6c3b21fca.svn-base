DECLARE
iv_user_id NUMBER := TO_NUMBER(:USER_ID);
iv_trade_type_code NUMBER(4) := :TRADE_TYPE_CODE;
iv_trade_id NUMBER(16) := TO_NUMBER(:TRADE_ID);
iv_brand_code CHAR(4) := :BRAND_CODE;
iv_curdate DATE := SYSDATE;
iv_start_date DATE;
iv_count NUMBER;
BEGIN
:CODE := -1;
:INFO := 'TRADE OK!';
IF iv_trade_type_code=470 THEN
IF iv_brand_code IN ('G001','G010') THEN
:INFO := '用户已经积分计划内的品牌了';
RETURN;
END IF;
SELECT COUNT(1)
INTO iv_count
FROM tf_f_user_other
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000) AND rsrv_value_code='ScRg' AND iv_curdate BETWEEN start_date AND end_date;
IF iv_count>0 THEN
:INFO := '用户已经开通积分计划';
RETURN;
END IF;
SELECT COUNT(1)
INTO iv_count
FROM tf_f_user_other
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000) AND rsrv_value_code='2005' AND iv_curdate BETWEEN start_date AND end_date;
IF iv_count=0 THEN
:INFO := '用户上年10-12月话费没有达到标准';
RETURN;
END IF;
INSERT INTO tf_b_trade_other (trade_id,rsrv_value_code,rsrv_value,modify_tag,start_date,end_date)
VALUES (iv_trade_id,'ScRg','申请积分计划','0',iv_curdate,TO_DATE('2050','YYYY'));
ELSIF  iv_trade_type_code=471 THEN
BEGIN
SELECT start_date
INTO iv_start_date
FROM tf_f_user_other
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000) AND rsrv_value_code='ScRg' AND iv_curdate BETWEEN start_date AND end_date;
EXCEPTION
WHEN OTHERS THEN
:INFO := '用户没有申请积分计划';
RETURN;
END;
INSERT INTO tf_b_trade_other (trade_id,rsrv_value_code,rsrv_value,modify_tag,start_date,end_date)
VALUES (iv_trade_id,'ScRg','申请积分计划','2',iv_start_date,iv_curdate);
END IF;
:CODE := 0;
END;