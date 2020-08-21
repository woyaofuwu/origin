SELECT COUNT(1) recordcount
  FROM DUAL U
 WHERE :SERIAL_NUMBER NOT LIKE '0898%' 
    OR (:SERIAL_NUMBER LIKE '0898%' AND 
        EXISTS (SELECT 1 FROM TD_S_COMMPARA r
                 WHERE r.SUBSYS_CODE ='CGM'
                   AND r.PARAM_ATTR = '960'
                   AND r.PARAM_CODE = :TRADE_TYPE_CODE
                   AND SYSDATE BETWEEN r.START_DATE AND r.END_DATE
               )
        )