DECLARE
    iv_user_id NUMBER(16):=TO_NUMBER(:USER_ID);
    iv_old_accept_date DATE;
BEGIN
    :code:=-1;
    :info:='TRADE OK';
    :olcom_tag:='1';
    :next_deal_tag:='1';
    SELECT MAX(accept_date)
    INTO iv_old_accept_date
    FROM tf_b_trade
    WHERE trade_type_code BETWEEN 1450 AND 1453
        AND user_id=iv_user_id AND cancel_tag='0';
    IF  iv_old_accept_date IS NOT NULL THEN
        UPDATE tf_b_trade
            SET olcom_tag='0'
        WHERE trade_type_code=1450
            AND next_deal_tag='3'
            AND olcom_tag='1'
            AND user_id=iv_user_id
            AND accept_date+0=iv_old_accept_date 
            AND cancel_tag='0' AND ROWNUM<2 RETURNING olcom_tag,'0' INTO :olcom_tag,:next_deal_tag;           
    END IF;
    BEGIN
        SELECT '0','0'
        INTO :olcom_tag,:next_deal_tag
        FROM tf_f_relation_uu
        WHERE relation_type_code='20' 
            AND  user_id_b=iv_user_id AND  partition_id=MOD(iv_user_id,10000) 
            AND  SYSDATE BETWEEN start_date AND end_date AND ROWNUM<2;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;
    END;
    :CODE:=0;
END;