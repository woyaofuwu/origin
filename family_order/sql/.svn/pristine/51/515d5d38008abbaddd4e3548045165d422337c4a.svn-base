DECLARE
   IV_PARA                     tf_b_trade%ROWTYPE;
BEGIN
    :RESULTINFO:='TRADE OK!';
    :RESULTCODE:='0';
    BEGIN
    P_OCS_CHECK(:SERIAL_NUMBER,:RSRV_STR8,:RESULTCODE,:RESULTINFO);
    if (:RESULTCODE <> 0 )then
        return;
    end if ;
    P_OCS_MAIN_SUB(:SERIAL_NUMBER,:RSRV_STR8,IV_PARA,:RESULTCODE,:RESULTINFO);  
    EXCEPTION
        WHEN OTHERS THEN
        :RESULTCODE:='-1';
        :RESULTINFO:= '存储过程异常:'||substr(sqlerrm,1,170);
        return ;
    END;
END;