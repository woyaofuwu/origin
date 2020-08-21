INSERT INTO TF_B_TICKET_PREPMG_DETAIL
  (LOG_ID,
   START_VALUE,
   END_VALUE,
   FIELD_NAME,
   OPER_NUM,
   RSRV_STR1,
   RSRV_STR2,
   REMARK)
  SELECT RSRV_STR5,
         MIN(TICKET_ID) START_NO,
         MAX(TICKET_ID) END_NO,
         '5',
         COUNT(*) COUNT_NUM,
         :TAX_NO,
         :RES_KIND_CODE,
         :REMARK / / BATCHID
    FROM (SELECT A.*,
                 TO_NUMBER(TRANSLATE(A.TICKET_ID,
                                     IS_TANSLATE(0),
                                     IS_TANSLATE(1)) - ROWNUM) C
            FROM "+param.getString("INSERT_TABLE_NAME")+" A　
           WHERE LENGTH(TICKET_ID) = LENGTH(:X_RES_NO_S)
             AND TICKET_ID >= :X_RES_NO_S
             AND TICKET_ID <= :X_RES_NO_E
             AND TAX_NO = :TAX_NO
             AND LOG_ID = :LOG_ID
           ORDER BY TICKET_ID) B
   GROUP BY B.C, B.RSRV_STR5