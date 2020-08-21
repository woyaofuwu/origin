DECLARE
    iv_user_id     NUMBER(16):= TO_NUMBER(:USER_ID);
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    FOR i IN (SELECT trade_id FROM tf_b_trade WHERE exec_time>SYSDATE AND user_id=iv_user_id AND trade_type_code IN (110,111)) LOOP
    UPDATE tf_b_trade_svc
    SET modify_tag='4'
    WHERE trade_id=i.trade_id
    AND service_id BETWEEN 13 AND 19;
    END LOOP;
    :CODE           := 0;
END;