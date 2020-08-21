DECLARE
    iv_trade_id     NUMBER(16) := TO_NUMBER(:TRADE_ID);
    iv_user_id      NUMBER(16) := TO_NUMBER(:USER_ID);
    iv_eparchy_code CHAR(4)    := :EPARCHY_CODE;
    iv_rsrv_str1    CHAR(1)    := :RSRV_STR1;
    iv_count        NUMBER;
    iv_brand         CHAR(10);
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    iv_count        := 0;
    BEGIN
        UPDATE tf_b_trade SET rsrv_str9='UnScoreCleared' WHERE trade_id=iv_trade_id;
    EXCEPTION
    WHEN OTHERS THEN
    :INFO:='更新业务台帐字段出错'||substr(sqlerrm,1,150);
    RETURN;
    END;    
    SELECT COUNT(1) INTO iv_count FROM dual WHERE
    (SELECT score FROM tf_f_user_newscore WHERE user_id=iv_user_id AND year_id='1000' AND score_type_code='10')>=
    (SELECT tag_number FROM td_s_tag WHERE tag_code=decode(iv_rsrv_str1,'3','CS_MZONEXCHANGENEW_MIN_LIMIT','CS_SCOREEXCHANGENEW_MIN_LIMIT')
    AND eparchy_code=iv_eparchy_code);
    IF iv_count > 0 THEN
        SELECT decode(iv_rsrv_str1,'3','动感地带','全球通') INTO iv_brand FROM dual;
        :NOTICE_CONTENT := '尊敬的'||Trim(iv_brand)||'客户，您的产品变更已成功办理，请您在本月内前往当地移动【沟通100】服务厅进行积分兑换。';
    ELSE
        :NOTICE_CONTENT := 'X';
    END IF;
    :CODE           := 0;
END;