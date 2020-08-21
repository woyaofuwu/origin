select count(*) record_count
  from (select a.trade_id
          from tf_b_trade a
           where a.user_id=:USER_ID
           and a.trade_type_code in (11, 10, 160)
           and a.accept_date > sysdate - (:TIMEINTERVAL) / 24
           and a.ACCEPT_DATE < sysdate
        union all
        select a.trade_id
          from tf_bh_trade a
          where a.user_id=:USER_ID
           and a.trade_type_code in (11, 10, 160)
           and a.accept_date > sysdate - (:TIMEINTERVAL) / 24
           and a.ACCEPT_DATE < sysdate )
 where rownum < 2