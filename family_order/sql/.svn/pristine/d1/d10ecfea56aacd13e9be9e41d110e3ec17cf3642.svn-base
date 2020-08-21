Declare
    iv_trade_id NUMBER(16):=TO_NUMBER(:TRADE_ID);
    iv_user_id      NUMBER(16) := TO_NUMBER(:USER_ID);
     iv_rsrv_value_code VARCHAR2(4) := :RSRV_VALUE_CODE;
     iv_start_date    DATE;
BEGIN
    :CODE:= -1;
    :INFO:= 'TRADE OK!';
    
    IF TRIM(iv_trade_id) IS NOT NULL AND TRIM(iv_user_id) IS NOT NULL THEN 
       BEGIN
        SELECT start_date INTO iv_start_date from tf_b_trade_other WHERE trade_id=iv_trade_id;
          EXCEPTION
           WHEN OTHERS THEN
         :INFO:='获取other工单开始时间出错'||substr(sqlerrm,1,150);
          RETURN;
       END; 
       BEGIN
         UPDATE tf_f_user_other SET end_date=iv_start_date
               WHERE user_id=iv_user_id AND start_date<iv_start_date AND sysdate <= end_date+0 
                        AND  partition_id = mod(iv_user_id, 10000) AND iv_rsrv_value_code='ONSN';
        EXCEPTION
           WHEN OTHERS THEN
         :INFO:='获取other表数据出错'||substr(sqlerrm,1,150);
          RETURN;
       END; 
        
    END IF;
    :CODE:= 0;
END;