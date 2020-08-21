DECLARE
    iv_serial_number VARCHAR2(15)    := :SERIAL_NUMBER;
    iv_user_id NUMBER(16);
    iv_type_code VARCHAR2(10);
    iv_score_value NUMBER(11);
    iv_staff_id  CHAR(8);
    iv_depart_id CHAR(5);
    iv_start_date VARCHAR2(20);
    iv_end_date VARCHAR2(20);
    iv_curdate DATE:=SYSDATE;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    BEGIN
		SELECT user_id
		INTO iv_user_id
		FROM tf_f_user
		WHERE serial_number=iv_serial_number AND remove_tag='0';
		SELECT TRIM(rsrv_value),rsrv_str1,rsrv_str6,rsrv_str7,TO_CHAR(start_date,'YYYY-MM-DD HH24:MI:SS'),TO_CHAR(end_date,'YYYY-MM-DD HH24:MI:SS')
		INTO iv_type_code,iv_score_value,iv_staff_id,iv_depart_id,iv_start_date,iv_end_date
		FROM tf_f_user_other
		WHERE user_id=iv_user_id
		AND partition_id=MOD(iv_user_id,10000)
		AND rsrv_value_code='FWJF'
		AND iv_curdate BETWEEN start_date AND end_date;
	EXCEPTION
		WHEN NO_DATA_FOUND THEN
			:INFO:='没有找到用户信息';
			RETURN;
	END;
    :DEPART_ID     := iv_depart_id;
    :START_DATE    := iv_start_date;
    :STAFF_ID      := iv_staff_id;
    :TYPE_CODE     := iv_type_code;
    :SCORE_VALUE   := iv_score_value;
    :END_DATE      := iv_end_date;
    :CODE           := 0;
END;