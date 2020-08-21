DECLARE
iv_count NUMBER;
iv_trade_id NUMBER(16):=TO_NUMBER(:TRADE_ID);
iv_accept_month NUMBER(2):=SUBSTR(:TRADE_ID,5,2);
iv_o_discnt_code NUMBER(8);
iv_n_discnt_code NUMBER(8);
iv_user_id_a NUMBER(16);
iv_o_start_date DATE;
iv_n_start_date DATE;
iv_o_end_date DATE;
iv_n_end_date DATE;
BEGIN
:CODE:=-1;
:INFO:='TRADE OK!';
SELECT COUNT(1)
INTO iv_count
FROM tf_b_trade_discnt
WHERE trade_id=iv_trade_id
AND accept_month=iv_accept_month
AND modify_tag='1';
IF iv_count=1 THEN
	SELECT COUNT(1)
	INTO iv_count
	FROM tf_b_trade_discnt
	WHERE trade_id=iv_trade_id
	AND accept_month=iv_accept_month
	AND modify_tag='0';
	IF iv_count=1 THEN
		SELECT discnt_code,id,start_date,end_date
		INTO iv_o_discnt_code,iv_user_id_a,iv_o_start_date,iv_o_end_date
		FROM tf_b_trade_discnt
		WHERE trade_id=iv_trade_id
		AND accept_month=iv_accept_month
		AND modify_tag='1';
		SELECT discnt_code,id,start_date,end_date
		INTO iv_n_discnt_code,iv_user_id_a,iv_n_start_date,iv_n_end_date
		FROM tf_b_trade_discnt
		WHERE trade_id=iv_trade_id
		AND accept_month=iv_accept_month
		AND modify_tag='0';
		IF iv_n_discnt_code<>iv_o_discnt_code THEN
			FOR i IN (SELECT ROWID,a.* FROM tf_f_user_discnt a
				 WHERE user_id_a = iv_user_id_a
				   AND discnt_code+0 = iv_o_discnt_code
				   AND spec_tag='2'
				   AND end_date > iv_o_end_date ) LOOP
				BEGIN
				INSERT INTO tf_f_user_discnt
					(partition_id,user_id,user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date)
				VALUES
					(i.partition_id,i.user_id,i.user_id_a,iv_n_discnt_code,i.spec_tag,i.relation_type_code,iv_n_start_date,iv_n_end_date);
				EXCEPTION
					WHEN OTHERS THEN
						NULL;
				END;
				UPDATE tf_f_user_discnt
				SET end_date=iv_o_end_date
				WHERE ROWID=i.ROWID;
				DELETE FROM tf_f_user_discnt
				WHERE ROWID=i.ROWID AND start_date>end_date;
			END LOOP;
		END IF;
	END IF;
END IF;
:CODE:=0;
END;