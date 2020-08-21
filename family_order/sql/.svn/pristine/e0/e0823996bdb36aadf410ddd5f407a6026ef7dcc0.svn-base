UPDATE TS_A_DIFFERINFO
   SET AUDIT_DATE = to_date(:AUDIT_DATE,'yyyy-mm-dd hh24:mi:ss'),
       AUDIT_TAG = :AUDIT_TAG
 WHERE TRADE_ID = to_number(:TRADE_ID)