DECLARE
    iv_trade_id     NUMBER(16) := TO_NUMBER(:TRADE_ID);
    iv_user_id      NUMBER(16) := TO_NUMBER(:USER_ID);
    iv_trade_type_code    NUMBER(4);
    iv_trade_type    VARCHAR2(20);
    iv_brand         CHAR(10);
    iv_eparchy_code  CHAR(4);
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    BEGIN
        SELECT distinct(trade_type_code),eparchy_code INTO iv_trade_type_code,iv_eparchy_code from tf_b_trade WHERE trade_id=iv_trade_id;
    EXCEPTION
    WHEN OTHERS THEN
    :INFO:='获取返销业务类型出错'||substr(sqlerrm,1,150);
    RETURN;
    END;   
    SELECT trade_type INTO iv_trade_type from td_s_tradetype WHERE trade_type_code=iv_trade_type_code and eparchy_code=iv_eparchy_code; 
    IF iv_trade_type  is not null  THEN
          select decode(rsrv_str1,'1','全球通','3','动感地带','神州行') into iv_brand from tf_f_user_brandchange 
               where partition_id=mod(iv_user_id,10000) and user_id=iv_user_id and sysdate between start_date and end_date;
        :NOTICE_CONTENT := '尊敬的'||Trim(iv_brand)||'品牌客户，您预约办理的'||iv_trade_type||'业务已取消成功，谢谢使用。';
    ELSE
        :NOTICE_CONTENT := 'X';
    END IF;
    :CODE           := 0;
END;