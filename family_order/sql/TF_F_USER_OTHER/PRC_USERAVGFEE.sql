DECLARE
	iv_user_id  NUMBER(16):=TO_NUMBER(:USER_ID);
	iv_months NUMBER(4):=:MONTHS;--平均月数,<1取所有月平均
	iv_partition_id  NUMBER(4):=MOD(iv_user_id,10000);
	iv_start_acyc_id NUMBER(6);
	iv_end_acyc_id NUMBER(6);
	iv_curdate DATE:=SYSDATE;
	iv_fee  VARCHAR(20);
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
	SELECT DECODE(SIGN(iv_months),1,acyc_id-iv_months,0),acyc_id-1
	INTO iv_start_acyc_id,iv_end_acyc_id
	FROM td_a_acycpara
	WHERE acyc_start_time <= iv_curdate AND iv_curdate < acyc_end_time;
	SELECT TRIM(TO_CHAR(NVL(AVG(NVL(FEE/100,0)),0),'9999999990.00'))
	INTO iv_fee
	FROM tf_o_credit_userbill
	WHERE acyc_id BETWEEN iv_start_acyc_id AND iv_end_acyc_id AND user_id=iv_user_id AND partition_id=iv_partition_id;
	:FEE            := iv_fee;
    :CODE           := 0;
END;