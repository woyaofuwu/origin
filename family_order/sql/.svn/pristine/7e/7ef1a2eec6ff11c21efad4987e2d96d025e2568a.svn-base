DECLARE
    iv_user_id     NUMBER(16)      := TO_NUMBER(:USER_ID);
    iv_id_type     CHAR(1)         := :ID_TYPE;
    iv_score_type_code CHAR(2)     := :SCORE_TYPE_CODE;
    iv_trade_type_code NUMBER(4)   := :TRADE_TYPE_CODE;
    iv_trade_id    NUMBER(16)      := TO_NUMBER(:TRADE_ID);
    iv_acyc_id     NUMBER(4)       := :ACYC_ID;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    FOR i IN (SELECT * FROM tf_b_trade_score WHERE trade_id=iv_trade_id) LOOP
	IF iv_id_type='2' THEN
		UPDATE tf_f_user_newscore
		SET score=score+i.score_changed
		WHERE user_id=i.user_id AND partition_id=MOD(i.user_id,10000) AND id_type=iv_id_type
		AND acyc_id=(SELECT MAX(acyc_id)
FROM tf_f_user_newscore
WHERE user_id=i.user_id AND partition_id=MOD(i.user_id,10000) AND id_type=2);
	END IF;
	END LOOP;
    :CODE           := 0;
END;