SELECT to_char(trade_id) trade_id,DEAL_TAG
  FROM TF_BH_TRADE_FTTH
 WHERE DEAL_TAG = :TRADE_ID
