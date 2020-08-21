Declare
    iv_user_id  NUMBER(16) := TO_NUMBER(:USER_ID);
    iv_city_code CHAR(4) := :RSRV_STR1;
    iv_curdate DATE:=SYSDATE;
    iv_acct_id NUMBER(16):=0;
BEGIN
    :CODE:= -1;
    :INFO:= 'TRADE OK!';
    IF TRIM(iv_city_code) IS NOT NULL THEN
        Begin
            SELECT acct_id INTO iv_acct_id FROM tf_a_payrelation
            WHERE partition_id=MOD(iv_user_id,10000) AND user_id=iv_user_id AND default_tag='1' AND act_tag='1' AND 
            (SELECT acyc_id from td_a_acycpara where sysdate between acyc_start_time and acyc_end_time) between start_acyc_id and end_acyc_id;
        Exception
            WHEN NO_DATA_FOUND THEN
            :CODE := -1;
            :INFO:='没有找到用户对应的默认帐户信息！user_id:'+TO_CHAR(iv_user_id);
            RETURN;
        END;
        UPDATE tf_f_account SET city_code=iv_city_code 
        WHERE partition_id=MOD(iv_acct_id,10000) AND acct_id=iv_acct_id;
        UPDATE tf_f_user SET city_code=NVL(iv_city_code,city_code)
        WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000);
       
        UPDATE tf_f_customer SET city_code=NVL(iv_city_code,city_code)
        WHERE cust_id = (SELECT a.cust_id FROM tf_f_user a WHERE a.user_id=iv_user_id AND a.partition_id=MOD(iv_user_id,10000)) AND partition_id = MOD(cust_id,10000);   
    END IF;
        :CODE:= 0;
END;