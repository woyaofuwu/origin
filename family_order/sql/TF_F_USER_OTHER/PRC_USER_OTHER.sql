DECLARE
    iv_user_id    NUMBER(16):=TO_NUMBER(:USER_ID);
    iv_partion_id NUMBER(4):=MOD(TO_NUMBER(:USER_ID),10000);
    iv_tag        CHAR(1):=TRIM(:RSRV_STR2);
    iv_score      VARCHAR2(11):=TRIM(:RSRV_STR1);
    iv_curdate    DATE:=SYSDATE;
    iv_staff_id   CHAR(8):=:TRADE_STAFF_ID;
    iv_depart_id  CHAR(5):=:TRADE_DEPART_ID;
BEGIN
    UPDATE tf_f_user_other
    SET end_date=iv_curdate,
        rsrv_str4=iv_staff_id,
        rsrv_str5= iv_depart_id
    WHERE rsrv_value_code='FWJF'
        AND user_id=iv_user_id
        AND partition_id=iv_partion_id
        AND iv_curdate BETWEEN start_date AND end_date;
    IF iv_tag IN ('0','1','2') THEN
        INSERT INTO tf_f_user_other
        (
            partition_id,
            user_id,
            rsrv_value_code,
            rsrv_value,
            rsrv_str1,
            rsrv_str6,
            rsrv_str7,
            start_date,
            end_date
        )
        VALUES
        (
            iv_partion_id,
            iv_user_id,
            'FWJF',
            iv_tag,
            iv_score,
            iv_staff_id,
            iv_depart_id,
            iv_curdate,
            TO_DATE('2050','YYYY')
        );
    END IF;
    :CODE:=0;
    :INFO:='TRADE OK';
END;