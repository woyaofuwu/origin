SELECT count(*) recordcount
       FROM tf_f_user_discnt a 
       WHERE a.User_Id = :USER_ID
       AND   a.discnt_code IN (SELECT param_code
       FROM td_s_commpara WHERE param_attr=:PARAM_ATTR
       AND  SYSDATE BETWEEN start_date AND end_date)
       AND  SYSDATE BETWEEN a.start_date AND a.end_date
       AND  EXISTS(SELECT 1 FROM tf_b_trade_discnt b
       WHERE b.trade_id=:TRADE_ID
       AND   b.accept_month=:ACCEPT_MONTH
       AND   b.discnt_code=a.discnt_code
       AND   b.modify_tag='1')
       AND  NOT EXISTS(SELECT 1 FROM tf_b_trade_discnt c
       WHERE c.trade_id=:TRADE_ID
       AND   c.accept_month=:ACCEPT_MONTH
       AND   c.modify_tag IN ('0','U')
       AND   c.discnt_code IN (SELECT param_code
       FROM td_s_commpara WHERE param_attr=:PARAM_ATTR
       AND  SYSDATE BETWEEN start_date AND end_date))