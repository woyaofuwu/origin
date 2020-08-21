DECLARE
iv_trade_id      NUMBER(16):= TO_NUMBER(:TRADE_ID);
iv_user_id       NUMBER(16):= TO_NUMBER(:USER_ID);
iv_changed_score NUMBER(11):=TO_NUMBER(:CHANGED_SCORE);
iv_level_code    CHAR(1):= :REVENUE_LEVEL_CODE;
iv_level_value   NUMBER(11);
iv_eparchy_code  CHAR(4);
iv_type_code     CHAR(1);
iv_serv_score    NUMBER(11);
iv_curdate       DATE:=SYSDATE;
iv_serial_number VARCHAR2(15);
iv_score_value   NUMBER(11);
iv_user_changescore NUMBER(11):=0;
iv_serv_changescore NUMBER(11):=0;
iv_tag           CHAR(1):='1';
BEGIN
    :CODE := -1;
    :INFO := 'TRADE OK!';
    SELECT serial_number,score_value,eparchy_code
    INTO iv_serial_number,iv_score_value,iv_eparchy_code
    FROM tf_f_user
    WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000);
    BEGIN
        SELECT rsrv_value,TO_NUMBER(NVL(rsrv_str1,'0'))
        INTO iv_type_code,iv_serv_score
        FROM tf_f_user_other
        WHERE rsrv_value_code='FWJF'
            AND iv_curdate BETWEEN start_date and end_date
            AND partition_id=MOD(iv_user_id,10000) AND user_id=iv_user_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            iv_type_code := '1';
            iv_serv_score:=0;
            iv_tag:='0';
    END;
    BEGIN
        SELECT TO_NUMBER(para_code1)
        INTO iv_level_value
        FROM td_s_commpara
        WHERE subsys_code='CSM'
            AND param_attr=161
            AND param_code=NVL(iv_level_code,'1')
            AND iv_curdate BETWEEN start_date AND end_date
            AND (eparchy_code='ZZZZ' OR eparchy_code=iv_eparchy_code);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
                iv_level_value:=0;
    END;
    BEGIN
    SELECT ( NVL( SUM( TO_NUMBER( RSRV_VALUE )  ), 0 )+1) * iv_level_value
    INTO iv_changed_score
    FROM tf_b_trade_other
    WHERE rsrv_value_code='06' AND trade_id=iv_trade_id;
    EXCEPTION
        WHEN OTHERS THEN
            NULL;
    END;
    IF iv_type_code = '0' THEN
        IF iv_score_value>=ABS(iv_changed_score) THEN
            iv_user_changescore:=-ABS(iv_changed_score);
        ELSIF iv_score_value>0 THEN
            iv_user_changescore:=-iv_score_value;
            iv_serv_changescore:=-ABS(iv_changed_score)+iv_score_value;
        ELSE
            iv_serv_changescore:=-ABS(iv_changed_score);
        END IF;
    ELSIF iv_type_code = '1' THEN
        IF iv_serv_score>=ABS(iv_changed_score) THEN
            iv_serv_changescore:=-ABS(iv_changed_score);
        ELSIF iv_serv_score>0 THEN
            iv_serv_changescore:=-iv_serv_score;
            iv_user_changescore:=-ABS(iv_changed_score)+iv_serv_score;
        ELSE
            iv_user_changescore:=-ABS(iv_changed_score);
        END IF;
    END IF;
    IF iv_tag = '1' THEN
        UPDATE tf_f_user_other
        SET rsrv_str1=TO_CHAR(TO_NUMBER(NVL(rsrv_str1,'0'))+iv_serv_changescore)
        WHERE rsrv_value_code='FWJF'
            AND iv_curdate BETWEEN start_date and end_date
            AND partition_id=MOD(iv_user_id,10000) AND user_id=iv_user_id;
        INSERT INTO tf_b_trade_other
            (
                trade_id,
                rsrv_value_code,
                rsrv_value,
                rsrv_str1,
                rsrv_str2,
                rsrv_str3,
                rsrv_str4,
                start_date
            )
            VALUES
            (
                iv_trade_id,
                'VIPS',
                iv_type_code,
                iv_serv_score,
                iv_serv_changescore,
                iv_serial_number,
                iv_user_id,
                iv_curdate
            );
    END IF;
    :RSRVSTR2:=iv_user_changescore;
    :CODE := 0;
END;