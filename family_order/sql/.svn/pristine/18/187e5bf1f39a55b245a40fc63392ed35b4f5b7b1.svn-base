DECLARE
iv_serial_number VARCHAR2(15) :=:SERIAL_NUMBER;
iv_trade_id NUMBER(16):=f_sys_getseqid(:EPARCHY_CODE,'seq_trade_id');
iv_curdate DATE:=SYSDATE;
iv_user_id NUMBER(16);
iv_cust_id NUMBER(16);
iv_acyc_id NUMBER(6);
iv_cust_name VARCHAR2(100);
iv_rsrv_value_code VARCHAR2(4):='HWJK';
BEGIN
:CODE:=-1;
:INFO:='TRADE OK!';
:TRADE_ID:=NVL(TO_CHAR(iv_trade_id),-1);
:RSRV_VALUE_CODE:=iv_rsrv_value_code;
IF :EPARCHY_CODE IS NULL OR iv_serial_number IS NULL THEN
:INFO:='输入参数不全!';
RETURN;
END IF;
BEGIN
SELECT cust_id,user_id
INTO iv_cust_id,iv_user_id
FROM tf_f_user
WHERE serial_number=iv_serial_number AND remove_tag='0';
EXCEPTION
WHEN OTHERS THEN
NULL;
END;
BEGIN
SELECT cust_name
INTO iv_cust_name
FROM tf_f_customer
WHERE cust_id=iv_cust_id AND partition_id=MOD(iv_cust_id,10000);
EXCEPTION
WHEN OTHERS THEN
NULL;
END;
SELECT acyc_id
INTO iv_acyc_id
FROM td_a_acycpara
WHERE iv_curdate BETWEEN acyc_start_time AND acyc_end_time-1/24/3600;
FOR i IN (SELECT *
FROM tf_f_relation_uu
WHERE user_id_b=iv_user_id AND partition_id=MOD(iv_user_id,10000) AND relation_type_code IN ('20','21','22','24','26','30')
AND iv_curdate BETWEEN start_date AND end_date) LOOP
FOR j IN(SELECT *
FROM tf_a_payrelation a
WHERE user_id=i.user_id_b  AND partition_id=MOD(i.user_id_b,10000)
AND iv_acyc_id BETWEEN start_acyc_id AND end_acyc_id
AND act_tag='1'
AND EXISTS (SELECT 1 FROM tf_a_payrelation WHERE user_id=i.user_id_a AND partition_id=MOD(i.user_id_a,10000)
AND iv_acyc_id BETWEEN start_acyc_id AND end_acyc_id
AND act_tag='1' AND default_tag='1' AND acct_id=a.acct_id)) LOOP
INSERT INTO tf_b_trade_other
(trade_id,rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9)
SELECT iv_trade_id,iv_rsrv_value_code,iv_serial_number,j.user_id,iv_cust_id,i.serial_number_a,i.user_id_a,i.relation_type_code,acct_id,pay_name,j.payitem_code,iv_cust_name
FROM tf_f_account
WHERE acct_id=j.acct_id AND partition_id=MOD(j.acct_id,10000);
END LOOP;
END LOOP;
:CODE:=0;
END;