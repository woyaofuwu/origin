INSERT INTO tf_b_trade_score(trade_id, accept_month, user_id, serial_number, score, score_changed, value_changed, remark,CANCEL_TAG)
        SELECT :TRADE_ID,substr(:TRADE_ID,5,2),:USER_ID,:SERIAL_NUMBER,nvl(sum(score),0),nvl(-sum(score),0),0,'产品变更积分清零' ,'0'
        FROM tf_f_user_newscore WHERE user_id=:USER_ID AND score_type_code='02'
        AND year_id IN (SELECT param_code from td_s_commpara WHERE param_attr=899 And subsys_code='CSM')