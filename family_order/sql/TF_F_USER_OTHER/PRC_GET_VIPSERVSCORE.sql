DECLARE
    iv_user_id     NUMBER(16)      := TO_NUMBER(:USER_ID);
    iv_type_code   CHAR(1);
    iv_score       VARCHAR(11);
    iv_curdate     DATE            :=SYSDATE;
    iv_count       NUMBER;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    SELECT COUNT(1)
    INTO iv_count
    FROM tf_f_cust_vip
    WHERE user_id=iv_user_id
        AND vip_type_code IN ('1','2')
        AND remove_tag='0';
    IF iv_count=0 THEN
        :INFO := '没有该用户的大客户信息';
        RETURN;
    END IF;
    BEGIN
        SELECT rsrv_value,rsrv_str1
        INTO iv_type_code,iv_score
        FROM tf_f_user_other
        WHERE rsrv_value_code='FWJF'
            AND iv_curdate BETWEEN start_date and end_date
            AND partition_id=MOD(iv_user_id,10000) AND user_id=iv_user_id;
    EXCEPTION
        WHEN OTHERS THEN
             iv_type_code:='0';
             iv_score:='0';
    END;
    :SCORE          := iv_score;
    :TYPE_CODE      := iv_type_code;
    :CODE           := 0;
END;