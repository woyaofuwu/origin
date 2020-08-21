DECLARE
    iv_user_id    NUMBER(16)      := to_number(:USER_ID);
    iv_rsrv_str10 VARCHAR(500)   := :RSRV_STR10;
BEGIN
    :CODE           := -1;
    :INFO           := 'TradeOk';
    BEGIN
    UPDATE tf_f_user_other SET rsrv_str10=iv_rsrv_str10 
    WHERE partition_id=MOD(iv_user_id,10000) AND user_id=iv_user_id 
    AND rsrv_value_code='SOLC' AND end_date>SYSDATE;
    IF SQL%ROWCOUNT=0 THEN
        INSERT INTO tf_f_user_other(partition_id, user_id,rsrv_value_code,rsrv_str10,start_date,end_date)
        VALUES(MOD(iv_user_id,10000),iv_user_id,'SOLC',iv_rsrv_str10,SYSDATE,to_date('20501231','yyyymmdd'));
    END IF;
    EXCEPTION
        WHEN OTHERS THEN
        :INFO:='更新客户关怀资料出错'||substr(sqlerrm,1,150);
        RETURN;
    END;
    :CODE           := 0;
END;