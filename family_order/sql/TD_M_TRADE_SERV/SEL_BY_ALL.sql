--IS_CACHE=Y
SELECT trade_type_code,trade_type,serv_order,olcomserv_code,remark 
  FROM td_m_trade_serv
 WHERE trade_type_code=:TRADE_TYPE_CODE
   AND serv_order=:SERV_ORDER