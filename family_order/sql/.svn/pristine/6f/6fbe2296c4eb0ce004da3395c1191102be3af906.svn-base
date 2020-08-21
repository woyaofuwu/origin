SELECT USER_ID,
       INST_ID,
       SHEET_TYPE,
       PRODUCT_NO,
       LINE_NUMBER,
       LINE_NAME,
       BAND_WIDTH,
       LINE_PRICE,
       LINE_STLBUG,
       LINE_STLCM,
       PROVINCE_A,
       CITY_A,      
       PROVINCE_Z,
       CITY_Z,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       RSRV_STR5
  FROM TF_F_USER_DATALINE T
 WHERE T.RSRV_STR5 = :RSRV_STR5
   AND T.END_DATE > SYSDATE