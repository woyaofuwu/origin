DECLARE
    iv_trade_id     NUMBER(16) := TO_NUMBER(:TRADE_ID);
    iv_user_id      NUMBER(16) := TO_NUMBER(:USER_ID);
    iv_count        NUMBER;
    iv_exec_time    DATE;
    iv_brand         CHAR(10);
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    iv_count        := 0;
    BEGIN
        SELECT exec_time INTO iv_exec_time from tf_b_trade WHERE trade_id=iv_trade_id;
    EXCEPTION
    WHEN OTHERS THEN
    :INFO:='获取工单执行时间出错'||substr(sqlerrm,1,150);
    RETURN;
    END;    
    IF iv_exec_time > sysdate THEN
          select decode(rsrv_str1,'1','全球通','3','动感地带','神州行') into iv_brand from tf_f_user_brandchange 
               where partition_id=mod(iv_user_id,10000) and user_id=iv_user_id and sysdate between start_date and end_date;
        :NOTICE_CONTENT := '尊敬的'||Trim(iv_brand)||'品牌客户，您预约办理的开机业务已受理成功，手机将在'||to_char(iv_exec_time,'yyyy-mm-dd hh24:mi:ss')||'开通，谢谢使用。';
    ELSE
        :NOTICE_CONTENT := 'X';
    END IF;
    :CODE           := 0;
END;