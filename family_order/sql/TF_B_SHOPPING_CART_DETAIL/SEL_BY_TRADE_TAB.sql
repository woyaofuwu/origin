--IS_CACHE=N
select A.TRADE_TABLE_NAME, A.TABLE_NAME TABLENAME, A.SQL_REF SQLREF
  from TD_S_SHOPPING_SQL A
 where A.STATE = '1'
   and A.TAG_NAME = :TAG_NAME
   AND A.TRADE_TABLE_NAME = :TRADE_TABLE_NAME