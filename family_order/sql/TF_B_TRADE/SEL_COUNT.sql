SELECT nvl(a.count,0) + nvl(b.count,0) X_RECORDNUM
FROM (SELECT COUNT(*) COUNT
     FROM tf_b_trade
     WHERE 1 = 1 
     AND TRADE_STAFF_ID = :TRADE_STAFF_ID
     AND ('-1' = :TRADE_TYPE_CODE OR TRADE_TYPE_CODE = :TRADE_TYPE_CODE)
     AND ACCEPT_DATE >= TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mm:dd')
     AND ACCEPT_DATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mm:dd')
     ) a,
     (SELECT COUNT(*) COUNT
     FROM tf_bh_trade
     WHERE 1 = 1 
     AND TRADE_STAFF_ID = :TRADE_STAFF_ID
     AND ('-1' = :TRADE_TYPE_CODE OR TRADE_TYPE_CODE = :TRADE_TYPE_CODE)
     AND ACCEPT_DATE >= TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mm:dd')
     AND ACCEPT_DATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mm:dd')
     ) b