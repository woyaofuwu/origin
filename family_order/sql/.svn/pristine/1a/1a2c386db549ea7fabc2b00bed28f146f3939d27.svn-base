DECLARE
    iv_user_id NUMBER(16) := to_number(:USER_ID);
    iv_trade_id NUMBER(16) := to_number(:TRADE_ID);    
BEGIN     
    BEGIN
        UPDATE tf_f_user_newscore SET score=rsrv_num2 WHERE user_id=:USER_ID
        AND year_id IN (SELECT param_code from td_s_commpara WHERE param_attr=899)
       AND score_type_code != '01';        
        UPDATE tf_b_trade SET rsrv_str9='' WHERE trade_id=iv_trade_id;
    EXCEPTION
        WHEN OTHERS THEN
        NULL;
        :RESULTINFO:='积分清零返销出错：'||substr(sqlerrm,1,150);
    END;     
END;