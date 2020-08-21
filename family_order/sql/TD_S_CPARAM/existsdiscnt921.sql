SELECT count(*) recordcount
       FROM tf_f_user_discnt a
       WHERE a.User_Id=:USER_ID
       AND   a.discnt_code=:DISCNT_CODE
       AND   SYSDATE BETWEEN a.start_date AND a.end_date
       AND   NOT EXISTS(SELECT 1 FROM ucr_crm1.tf_b_trade_discnt a
       WHERE a.trade_id=:TRADE_ID
       AND   a.accept_month = :ACCEPT_MONTH
       AND   a.discnt_code=:DISCNT_CODE
       AND   a.modify_tag=:MODIFY_TAG)