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
     ---本次兑换积分
     select  /*+use_nl(a,b)*/NVL(abs(Sum(a.score_changed_sub)),0)
     Into iv_adjust_score
    From tf_b_trade_scoresub a ,td_b_score_action b
    Where a.trade_id=iv_trade_id
    And a.action_code=b.action_code 
    And b.exchange_type_code = 'E';
     --奖励积分
     iv_adjust_score:=iv_adjust_score*10/100;
    --查询已赠送过多少积分
    Select NVL(Sum(b.score_changed),0) Into iv_adjust_score_his0
    From tf_bh_trade a,tf_b_trade_score b
    Where a.trade_type_code=350
    And a.user_id=iv_user_id
    And a.accept_date+0>to_date('20070919','yyyymmdd')
    And a.trade_staff_id='SUPERUSR'
    And a.rsrv_str10='1'              --表示此条记录是此次活动的记录
    And a.cancel_tag='0'
    And a.trade_id=b.trade_id ;
     
     --查询办理过的积分兑奖应该兑换多少积分(已打印过免填单的)
     Select NVL(abs(Sum(b.score_changed_sub)),0) Into iv_adjust_score_his
    From tf_bh_trade a,tf_b_trade_scoresub b ,td_b_score_action c
    Where a.trade_type_code=330 And a.accept_date+0>to_date('20070920','yyyymmdd')
    And a.cancel_tag='0'
    And a.trade_id=b.trade_id And b.action_code=c.action_code 
    And c.exchange_type_code = 'E'
    And a.user_id= iv_user_id;
 
     iv_adjust_score_his:=(iv_adjust_score_his)*10/100;
     if iv_adjust_score_his>500 then
        iv_adjust_score_his:=500;
     end if;
     If (iv_adjust_score_his+iv_adjust_score)>500 Then
           iv_adjust_score:=500-iv_adjust_score_his;
     End If;
     
     iv_score:=iv_adjust_score;
    :SCORE_ADJUST   := iv_score;
    :CODE           := 0;
    :INFO := '错误';   
END;