DECLARE
v_serial_number VARCHAR2(15):=:SERIAL_NUMBER;
v_rsrv_str1 VARCHAR2(50):=:RSRV_STR1;
v_rsrv_str2 VARCHAR2(50):=:RSRV_STR2;
v_rsrv_str3 VARCHAR2(50):=:RSRV_STR3;
v_rsrv_str4 VARCHAR2(50):=:RSRV_STR4;
v_rsrv_str5 VARCHAR2(50):=:RSRV_STR5;
v_rsrv_str6 VARCHAR2(50):=:RSRV_STR6;
v_user_id NUMBER(16);
v_count NUMBER(1):=0;
v_remove_tag CHAR(1);
v_acct_id NUMBER(16);
v_acyc_id NUMBER(6);
v_eparchy_code CHAR(4);
BEGIN
:CODE:=-1;
BEGIN
SELECT MAX(user_id) INTO v_user_id FROM tf_f_user WHERE serial_number = v_serial_number AND brand_code like 'GS%';
v_count:=1;
EXCEPTION
WHEN NO_DATA_FOUND THEN
v_count:=0;
WHEN OTHERS THEN
v_count:=0;
END;
IF v_count > 0 THEN
SELECT remove_tag,eparchy_code INTO v_remove_tag,v_eparchy_code FROM tf_f_user WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id AND brand_code like 'GS%';
IF v_remove_tag <> '0' THEN
UPDATE tf_f_user_file SET remove_tag = '0' WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id;
UPDATE tf_f_user_infochange SET end_date = TO_DATE('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss') WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id 
AND end_date = (select MAX(end_date) from tf_f_user_infochange where partition_id = MOD(v_user_id,10000) and user_id = v_user_id);
UPDATE tf_f_user_validchange SET end_date = TO_DATE('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss') WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id;
UPDATE tf_f_user_brandchange SET end_date = TO_DATE('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss') WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id 
AND end_date = (select MAX(end_date) from tf_f_user_brandchange where partition_id = MOD(v_user_id,10000) and user_id = v_user_id);
UPDATE tf_f_user_svc SET end_date = TO_DATE('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss') WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id 
AND end_date = (select MAX(end_date) from tf_f_user_svc where partition_id = MOD(v_user_id,10000) and user_id = v_user_id);
UPDATE tf_f_user_res SET end_date = TO_DATE('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss') WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id 
AND end_date = (select MAX(end_date) from tf_f_user_res where partition_id = MOD(v_user_id,10000) and user_id = v_user_id);
UPDATE tf_f_user_discnt SET end_date = TO_DATE('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss') WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id 
AND end_date = (select MAX(end_date) from tf_f_user_discnt where partition_id = MOD(v_user_id,10000) and user_id = v_user_id);
UPDATE tf_a_payrelation SET end_acyc_id = 481 WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id;
SELECT acct_id INTO v_acct_id FROM tf_a_payrelation WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id; 
SELECT COUNT(*) INTO v_count FROM tf_f_accountdeposit WHERE partition_id = MOD(v_acct_id,10000) AND acct_id = v_acct_id AND deposit_code = 80;
IF v_count = 0 THEN
SELECT MAX(acyc_id) INTO v_acyc_id FROM td_a_acycpara WHERE sysdate BETWEEN acyc_start_time AND acyc_end_time;
INSERT INTO tf_f_accountdeposit VALUES(v_eparchy_code,MOD(TO_NUMBER(v_acct_id),10000),v_acct_id,80,0,0,0,0,0,0,0,0,v_acyc_id,481,sysdate);
ELSE
UPDATE tf_f_accountdeposit SET end_acyc_id = 481,update_time = sysdate WHERE partition_id = MOD(v_acct_id,10000) AND acct_id = v_acct_id AND deposit_code = 80;
END IF;END IF;
UPDATE tf_f_user SET remove_tag = '0',user_state_codeset = '0',update_time = sysdate WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id;
UPDATE tf_f_user_svcstate SET end_date = sysdate WHERE partition_id = MOD(v_user_id,10000) AND user_id = v_user_id AND end_date > sysdate;
INSERT INTO tf_f_user_svcstate VALUES (MOD(TO_NUMBER(v_user_id),10000),v_user_id,1001,'1','0',sysdate,TO_DATE('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'),sysdate);
END IF;
:CODE:=0;:INFO:='OK';:RSRV_STR10:=:INFO; 
END;