DECLARE
iv_curdate  DATE:=SYSDATE;
iv_brand_code  CHAR(4);
iv_state_code  CHAR(1);
iv_sim_card_no VARCHAR2(20);
iv_imsi VARCHAR2(15);
iv_user_id NUMBER(16):=TO_NUMBER(:USER_ID);
BEGIN
:CODE           := -1;
:INFO           := 'TRADE OK!';
:BRAND_CODE    := ' ';
:STATE_CODE    := ' ';
:SIM_CARD_NO   := ' ';
:IMSI          := ' ';
BEGIN
SELECT brand_code
INTO iv_brand_code
FROM tf_f_user_infochange
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000)
AND iv_curdate BETWEEN start_date AND end_date AND ROWNUM<2;
EXCEPTION
    WHEN OTHERS THEN
        SELECT brand_code
        INTO iv_brand_code
        FROM tf_f_user
        WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000);
END; 
BEGIN
SELECT state_code
INTO iv_state_code
FROM tf_f_user_svcstate
WHERE user_id=iv_user_id and partition_id=MOD(iv_user_id,10000)
AND service_id=0
AND iv_curdate BETWEEN start_date AND end_date AND ROWNUM<2;
EXCEPTION
    WHEN OTHERS THEN
        SELECT SUBSTRB(user_state_codeset,-1)
        INTO iv_state_code
        FROM tf_f_user
        WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000);
END; 
SELECT res_code,res_info1
INTO iv_sim_card_no,iv_imsi
FROM tf_f_user_res
WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000)
AND res_type_code='1' AND iv_curdate BETWEEN start_date AND end_date AND ROWNUM<2;
:BRAND_CODE    := iv_brand_code;
:STATE_CODE    := iv_state_code;
:SIM_CARD_NO   := iv_sim_card_no;
:IMSI          := iv_imsi;
:CODE           := 0;   
END;