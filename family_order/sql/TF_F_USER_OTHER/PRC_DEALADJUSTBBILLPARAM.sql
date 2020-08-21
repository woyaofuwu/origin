DECLARE
    iv_trade_id    NUMBER(16) := TO_NUMBER(:TRADE_ID);
    iv_cancel_tag  CHAR(1) := :CANCEL_TAG;
    iv_bill_id     NUMBER(16) := TO_NUMBER(TRIM(:BILL_ID_I));
    iv_curdate     DATE:=SYSDATE;
BEGIN
    :CODE := -1;
    :INFO := 'TRADE OK!';
    :X_TAG:=0;
    :END_DATE := ' ';
    :START_DATE := ' ';
    :EFFECT_VALUE := ' ';
    :RECV_TAG := ' ';
    :BILL_ID_O := ' ';
    IF iv_bill_id IS NULL THEN
    	IF iv_cancel_tag='0' THEN
    		BEGIN
	    		SELECT TO_CHAR(fee),TO_CHAR(iv_curdate,'YYYYMM'),'201208'
	    		INTO :EFFECT_VALUE,:START_DATE,:END_DATE
	    		FROM tf_b_tradefee_giftfee
	    		WHERE trade_id=iv_trade_id AND fee_mode='3';
    		EXCEPTION
    			WHEN NO_DATA_FOUND THEN
    				:CODE:=0;
    				RETURN;
    		END;
    		:X_TAG:=1;
    		:RECV_TAG:='0';
    	ELSE
    		BEGIN
    		SELECT rsrv_value
    		INTO :BILL_ID_O
    		FROM tf_b_trade_other
    		WHERE trade_id=iv_trade_id AND rsrv_value_code='ADJB';
    		EXCEPTION
    			WHEN NO_DATA_FOUND THEN
    				:CODE:=0;
    				RETURN;
    		END;
                :X_TAG:=1;
    		:RECV_TAG:='2';
    	END IF;
    ELSE
    	IF iv_cancel_tag='0' THEN
    		INSERT INTO tf_b_trade_other
    		(trade_id,rsrv_value_code,rsrv_value)
    		VALUES
    		(iv_trade_id,'ADJB',iv_bill_id);
    	END IF;
    END IF;
    :CODE:= 0;
END;