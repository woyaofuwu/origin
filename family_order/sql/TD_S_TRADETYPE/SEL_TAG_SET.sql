--IS_CACHE=Y
SELECT SUBSTR(TAG_SET, 9, 1) TAG_SET
  FROM TD_S_TRADETYPE T
 WHERE T.TRADE_TYPE_CODE = :TRADE_TYPE_CODE
   AND (T.EPARCHY_CODE = :EPARCHY_CODE or T.EPARCHY_CODE='ZZZZ')