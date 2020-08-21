SELECT to_char(TRADE_ID) TRADE_ID,ACCEPT_MONTH,to_char(USER_ID) USER_ID,PRODUCT_ID,BRAND_CODE,SERIAL_NUMBER,IMSI,
  to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE
  FROM TF_B_TRADE_INFOCHANGE_BAK
 WHERE TRADE_ID = to_number(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND USER_ID = to_number(:USER_ID)
   AND start_date+0 < end_date+0
   AND end_date+0 > SYSDATE