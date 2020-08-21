INSERT INTO tf_b_trade_scoresub(trade_id,accept_month,user_id,action_code,score_type_code,score_changed_sub,value_changed_sub,action_count,remark)
        SELECT :TRADE_ID,substr(:TRADE_ID,5,2),:USER_ID,year_id,'0',-score,0,1,year_id||'年度积分'
        FROM tf_f_user_newscore WHERE user_id=:USER_ID AND score_type_code='02'
        AND year_id IN (SELECT param_code from td_s_commpara WHERE param_attr=899 And subsys_code ='CSM')