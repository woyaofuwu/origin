SELECT COUNT(1) recordcount
  FROM (
        SELECT 1 FROM tf_bh_trade
         WHERE trade_staff_id=:STAFF_ID
           AND (trade_type_code = :TRADE_TYPE_CODE OR :TRADE_TYPE_CODE = -1)
           AND accept_date BETWEEN to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') AND to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
        UNION ALL
        SELECT 1 FROM tf_b_trade
         WHERE trade_staff_id=:STAFF_ID
           AND (trade_type_code = :TRADE_TYPE_CODE OR :TRADE_TYPE_CODE = -1)
           AND accept_date BETWEEN to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') AND to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
       )