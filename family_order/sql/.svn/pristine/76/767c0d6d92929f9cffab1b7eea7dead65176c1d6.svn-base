SELECT count(1) recordcount
 FROM  tf_b_trade_scoresub a
 WHERE a.trade_id=:TRADE_ID
 AND (:ACTION_CODE IS NULL OR a.action_code=:ACTION_CODE)
 AND  exists (
          select 1 from td_b_score_action b
          where b.action_code=a.action_code
          and (exchange_type_code=:EXCHANGE_TYPE_CODE OR :EXCHANGE_TYPE_CODE IS NULL)
          and eparchy_code=:EPARCHY_CODE
      )