DECLARE
    iv_user_id     NUMBER(16);
    iv_trade_id    NUMBER(16)      := TO_NUMBER(:trade_id);
    iv_partition_id NUMBER(4);
    iv_curdate     DATE            := SYSDATE;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    BEGIN
	    SELECT user_id,MOD(user_id,10000)
	    INTO iv_user_id,iv_partition_id
	    FROM tf_b_trade
	    WHERE trade_id=iv_trade_id AND cancel_tag='0'
	    AND INSTR('G010|G002',brand_code)>0 AND rsrv_str1='G001';
	    UPDATE tf_f_user
	    SET prepay_tag='2',credit_value=0
	    WHERE user_id=iv_user_id AND partition_id=iv_partition_id AND prepay_tag='0';
	    UPDATE tf_f_postinfo
	    SET end_date=iv_curdate
	    WHERE id_type='1' AND id=iv_user_id AND end_date>iv_curdate;
	    FOR i in (SELECT vip_id
	    FROM tf_f_cust_vip
	    WHERE user_id=iv_user_id AND remove_tag='0'
	    AND vip_type_code IN ('0','2','3')) LOOP
	    DELETE FROM tf_f_cust_vipsimbak
		WHERE vip_id=i.vip_id
		AND act_tag='0';
	    END LOOP;
    EXCEPTION
    	WHEN NO_DATA_FOUND THEN
    		NULL;
    END;
    :CODE           := 0;
END;