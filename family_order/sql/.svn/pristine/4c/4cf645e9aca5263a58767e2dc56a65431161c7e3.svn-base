DECLARE
iv_serial_number VARCHAR2(15):=:SERIAL_NUMBER;
iv_user_id NUMBER(16);
iv_curdate DATE:=SYSDATE;
BEGIN
:CODE:=-1;
:INFO:='TRADE OK!';
:NUM:=0;
BEGIN
SELECT user_id
INTO iv_user_id
FROM tf_f_user
WHERE serial_number=iv_serial_number and remove_tag='0' AND ROWNUM<2;
SELECT user_id
INTO iv_user_id
FROM tf_f_user_class
WHERE id=iv_user_id AND id_type='1'
AND iv_curdate BETWEEN start_date AND end_date  AND ROWNUM<2;
SELECT COUNT(1)
INTO :NUM
FROM tf_f_user_pclass
WHERE user_id_a=iv_user_id
AND iv_curdate BETWEEN start_date AND end_date;
EXCEPTION
WHEN OTHERS THEN
NULL;
END;
:CODE:=0;
END;