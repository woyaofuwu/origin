DECLARE
    iv_trade_id    NUMBER(16)      := :TRADE_ID;
    iv_trade_type_code number(4)   := :TRADE_TYPE_CODE;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    SELECT DECODE(iv_trade_type_code,435,CEIL(TO_NUMBER(NVL(rsrv_str10,'0'))/100)*100,436,-CEIL(TO_NUMBER(NVL(rsrv_str10,'0'))/100)*100,0),rsrv_str5,rsrv_str6
    INTO :ADD_CREDIT_VALUE,:START_DATE,:END_DATE
    FROM tf_b_trade_ext
    WHERE trade_id=iv_trade_id;
    :CODE           := 0;
END;