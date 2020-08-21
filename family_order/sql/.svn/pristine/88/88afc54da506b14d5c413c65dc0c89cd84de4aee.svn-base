DECLARE
    iv_user_id     NUMBER(16)      := TO_NUMBER(:USER_ID);
    TYPE tab_acct_id            IS TABLE OF tf_f_account.acct_id%TYPE;
    iv_tab_acct_id              tab_acct_id;
    iv_cur_acyc_id              NUMBER(6);
    iv_count                    NUMBER;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    :CHECK_TAG      := '1';
    SELECT acyc_id
    INTO iv_cur_acyc_id
    FROM td_a_acycpara
    WHERE SYSDATE BETWEEN acyc_start_time AND acyc_end_time+1/24/3600;
    SELECT acct_id BULK COLLECT
    INTO iv_tab_acct_id
    FROM tf_a_payrelation a
    WHERE partition_id=MOD(iv_user_id,10000) AND  user_id=iv_user_id AND act_tag='1'
        AND iv_cur_acyc_id BETWEEN start_acyc_id AND end_acyc_id and default_tag ='1';
    iv_count:=iv_tab_acct_id.COUNT;
    IF iv_count=1 THEN
        SELECT count(1)
        INTO iv_count
        FROM tf_a_payrelation a
        WHERE acct_id=iv_tab_acct_id(1) AND act_tag='1'
            AND iv_cur_acyc_id BETWEEN start_acyc_id AND end_acyc_id 
              AND  exists (select 1 from tf_f_user b
                 WHERE a.user_id =b.user_id
                 AND b.brand_code LIKE 'G0%');
        IF iv_count>1 THEN
            :CHECK_TAG:='0';
        END IF;
    ELSIF iv_count>1 then
        :CHECK_TAG:='0';
    END IF;
    :CODE           := 0;
END;