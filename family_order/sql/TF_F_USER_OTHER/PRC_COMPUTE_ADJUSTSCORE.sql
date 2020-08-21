DECLARE
    iv_user_id       NUMBER(16)      := TO_NUMBER(:USER_ID);
    iv_trade_id      NUMBER(16)        := TO_NUMBER(:TRADE_ID);
    iv_score         NUMBER(16);
    iv_adjust_score   NUMBER(16);
    iv_adjust_score_his    NUMBER(16);
    iv_adjust_score_his0   NUMBER(16);
    iv_curdate     DATE            :=SYSDATE;
    iv_count       NUMBER;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
     
     iv_adjust_score:=0;
     Select nvl(sum(b.action_nv1*a.action_count),0) 
     Into iv_adjust_score
     From tf_b_trade_scoresub a,td_b_score_action b 
     Where a.trade_id=iv_trade_id
     And a.action_code=b.action_code;
     iv_score:=iv_adjust_score;
    :SCORE_ADJUST   := iv_score;
    :CODE           := 0;
    :INFO := 'TRADE OK!';   
END;