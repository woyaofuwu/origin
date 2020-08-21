DECLARE
iv_user_id     NUMBER(16)      := TO_NUMBER(:USER_ID);
iv_rsrv_value  VARCHAR2(50)    := :RSRV_VALUE;
iv_start_date  DATE            := TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS');
iv_rsrv_value_code VARCHAR2(4)     := :RSRV_VALUE_CODE;
iv_serial_number VARCHAR(15)     := :SERIAL_NUMBER;
iv_bind_serial_number VARCHAR2(30)    := :BIND_SERIAL_NUMBER;
iv_end_date    DATE            := TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS');
BEGIN
:CODE           := -1;
:INFO           := 'TRADE OK!';
UPDATE tf_f_user_bind
SET bind_serial_number=NVL(iv_bind_serial_number,bind_serial_number),
end_date=NVL(iv_end_date,end_date)
WHERE user_id=iv_user_id AND start_date=iv_start_date AND rsrv_value_code=iv_rsrv_value_code;
IF SQL%ROWCOUNT=0 THEN
INSERT INTO tf_f_user_bind
(
user_id,
serial_number,
bind_serial_number,
rsrv_value_code,
rsrv_value,
start_date,
end_date
)
values
(
iv_user_id,
iv_serial_number,
iv_bind_serial_number,
iv_rsrv_value_code,
iv_rsrv_value,
iv_start_date,
iv_end_date
);
END IF;
:CODE           := 0;
END;