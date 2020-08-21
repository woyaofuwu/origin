Declare
    iv_serial_number  VARCHAR2(15) := :SERIAL_NUMBER;
    iv_develop_depart_id CHAR(5) := :DEVELOP_DEPART_ID;
BEGIN
    :CODE:= -1;
    :INFO:= 'TRADE OK!';
    IF TRIM(iv_serial_number) IS NOT NULL THEN
        UPDATE tf_r_mphonecode_idle SET agent_id=iv_develop_depart_id WHERE serial_number=iv_serial_number;
    END IF;
        :CODE:= 0;
END;