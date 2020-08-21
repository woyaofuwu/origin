DECLARE
iv_trade_eparchy_code CHAR(4):= :TRADE_EPARCHY_CODE;
iv_home_address VARCHAR2(80):= :HOME_ADDRESS;
iv_trade_depart_id CHAR(5):= :TRADE_DEPART_ID;
iv_pay_name    VARCHAR2(50):= :PAY_NAME;
iv_contact_phone VARCHAR2(15):= :CONTACT_PHONE;
iv_contact     VARCHAR2(50):= :CONTACT;
iv_pspt_addr   VARCHAR2(80):= :PSPT_ADDR;
iv_remark       VARCHAR2(100):= :REMARK;
iv_pspt_id     VARCHAR2(20):= :PSPT_ID;
iv_sex         CHAR(1):= :SEX;
iv_trade_staff_id CHAR(8):= :TRADE_STAFF_ID;
iv_pspt_type_code CHAR(1):= :PSPT_TYPE_CODE;
iv_tag         NUMBER:= :X_GETMODE;
iv_cust_name   VARCHAR2(100):= :CUST_NAME;
iv_audit_batch_no VARCHAR2(16):= :AUDIT_BATCH_NO;
iv_trade_id    NUMBER(16):= TO_NUMBER(:TRADE_ID);
iv_curdate  DATE:=SYSDATE;
iv_cust_id NUMBER(16);
iv_acct_id NUMBER(16);
iv_count NUMBER;
BEGIN
:CODE:= -1;
:INFO:= 'TRADE OK!';
SELECT COUNT(1)
INTO iv_count
FROM tf_b_trade_audit
WHERE trade_id=iv_trade_id;
IF iv_count>0 THEN
    :INFO:= '该笔业务已经稽核过了'; 
    RETURN;  
END IF;
INSERT INTO tf_b_trade_audit
(
trade_id,
audit_batch_no,
audit_staff_id,
audit_depart_id,
audit_date,
cust_name,
cust_type,
sex,
pspt_type_code,
pspt_id,
pspt_end_date,
pspt_addr,
home_address,
home_postcode,
contact_phone,
contact,
contact_way,
user_type_code,
subscribe_mode,
credit_class,
basic_credit_value,
pay_name,
pay_mode_code,
bank_code,
bank_acct_no,
contract_no,
debuty_code,
open_date,
rsrv_str1,
rsrv_str2,
rsrv_str3,
rsrv_str4,
rsrv_str5,
rsrv_date1,
rsrv_date2,
remark
)
VALUES
(
iv_trade_id,
iv_audit_batch_no,
iv_trade_staff_id,
iv_trade_depart_id,
iv_curdate,
iv_cust_name,
NULL,
iv_sex,
iv_pspt_type_code,
iv_pspt_id,
NULL,
iv_pspt_addr,
iv_home_address,
NULL,
iv_contact_phone,
iv_contact,
NULL,
NULL,
NULL,
NULL,
NULL,
iv_pay_name,
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
iv_remark
);
SELECT cust_id,acct_id
INTO iv_cust_id,iv_acct_id
FROM tf_bh_trade
WHERE trade_id=iv_trade_id AND cancel_tag='0';
SELECT COUNT(1)
INTO iv_count
FROM tf_bh_trade_detail
WHERE trade_id=iv_trade_id;
IF iv_count>0 AND iv_cust_id IS NOT NULL AND TRIM(iv_cust_name||iv_pspt_type_code||iv_pspt_id||iv_pspt_addr||iv_sex||iv_contact||iv_contact_phone||iv_home_address) IS NOT NULL THEN
    UPDATE tf_f_customer
    SET cust_name=iv_cust_name,
        pspt_type_code=iv_pspt_type_code,
        pspt_id=iv_pspt_id
    WHERE cust_id=iv_cust_id AND partition_id=MOD(iv_cust_id,10000);
    UPDATE tf_f_cust_person
    SET cust_name=iv_cust_name,
        pspt_type_code=iv_pspt_type_code,
        pspt_id=iv_pspt_id,
        pspt_addr=iv_pspt_addr,
        sex=iv_sex,
        contact=iv_contact,
        contact_phone=iv_contact_phone,
        home_address=iv_home_address
    WHERE cust_id=iv_cust_id AND partition_id=MOD(iv_cust_id,10000);        
ELSIF iv_cust_name IS NOT NULL AND iv_cust_id IS NOT NULL THEN
    UPDATE tf_f_customer
    SET cust_name=iv_cust_name
    WHERE cust_id=iv_cust_id AND partition_id=MOD(iv_cust_id,10000);
    UPDATE tf_f_cust_person
    SET cust_name=iv_cust_name
    WHERE cust_id=iv_cust_id AND partition_id=MOD(iv_cust_id,10000);
END IF;
IF iv_pay_name IS NOT NULL AND iv_acct_id IS NOT NULL THEN
    UPDATE tf_f_account
    SET pay_name=iv_pay_name
    WHERE acct_id=iv_acct_id AND partition_id=MOD(iv_acct_id,10000);
END IF;
IF iv_remark IS NOT NULL THEN
    UPDATE tf_bh_trade
    SET remark=SUBSTRB(iv_remark||';'||remark,1,100)
    WHERE trade_id=iv_trade_id;
END IF;
:CODE:= 0;
END;