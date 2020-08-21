DECLARE
    iv_serial_number VARCHAR(15):=:SERIAL_NUMBER;
    iv_trade_id NUMBER(16):=TO_NUMBER(:TRADE_ID);
    iv_trade_type_code NUMBER(4):=:TRADE_TYPE_CODE;
    iv_flag  CHAR(1);
    iv_tag   VARCHAR2(2);
    iv_batch_id  NUMBER(16);
    iv_eparchy_code  CHAR(4);
    iv_staff_id  CHAR(8);
    iv_depart_id  CHAR(5);
    iv_accept_date  DATE;
    iv_enable_tag  CHAR(1);
    iv_count  NUMBER;
BEGIN
    :CODE:=-1;
    :INFO:='TRADE OK!';
    SELECT COUNT(1)
    INTO iv_count
    FROM td_a_ingw_commpara
    WHERE eparchy_code='ZZZZ'
        AND para_attr=5004
        AND para_code=TO_CHAR(iv_trade_type_code);
    IF iv_count>0 THEN
    BEGIN
        SELECT para_code1,para_code2
        INTO iv_flag,iv_enable_tag
        FROM td_a_ingw_commpara
        WHERE eparchy_code='ZZZZ'
            AND para_attr=5004
            AND para_code=TO_CHAR(iv_trade_type_code);
        SELECT SIGN(COUNT(1))
        INTO iv_tag
        FROM tf_b_ocs_batdeal
        WHERE serial_number=iv_serial_number AND flag='0';
        SELECT iv_tag||SIGN(COUNT(1))
        INTO iv_tag
        FROM tf_b_ocs_batdeal
        WHERE serial_number=iv_serial_number AND flag='1';
        BEGIN
        IF iv_tag='00' THEN
            SELECT trade_id,f_sys_getseqid(trade_eparchy_code,'seq_trade_id'),serial_number,trade_eparchy_code,trade_staff_id,trade_depart_id,sysdate
            INTO iv_batch_id,iv_trade_id,iv_serial_number,iv_eparchy_code,iv_staff_id,iv_depart_id,iv_accept_date
            FROM tf_b_trade
            WHERE trade_id=iv_trade_id AND cancel_tag IN ('0','2');
            INSERT INTO tf_b_ocs_batdeal
                (
                batch_id,
                trade_id,
                serial_number,
                eparchy_code,
                flag,
                staff_id,
                depart_id,
                accept_date,
                rsrv_str1,
                rsrv_str2,
                rsrv_num3,
                rsrv_num4,
                rsrv_dat5,
                rsrv_dat6
                )
            VALUES
                (
                iv_batch_id,
                iv_trade_id,
                iv_serial_number,
                iv_eparchy_code,
                iv_flag,
                iv_staff_id,
                iv_depart_id,
                iv_accept_date,
                iv_enable_tag,
                NULL,
                NULL,
                NULL,
                NULL,
                NULL
                );
        ELSIF iv_tag='11' OR iv_tag='01' AND iv_flag='0' OR iv_tag='10' AND iv_flag='1' THEN
            DELETE FROM tf_b_ocs_batdeal WHERE serial_number=iv_serial_number;
        END IF;
        EXCEPTION
        	WHEN OTHERS THEN
        		NULL;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            NULL;
    END;
    END IF;
    :CODE:=0;
END;