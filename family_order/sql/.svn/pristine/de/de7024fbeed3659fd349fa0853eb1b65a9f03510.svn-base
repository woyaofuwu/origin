SELECT to_char(a.trade_id) trade_id,CUST_CERT_NO,cust_name,

decode( CREDIT_LEVEL,'00','准星','01','一星','02','二星','03','三星','04','四星','05','五星','06','五星金','07','五星钻','09','未评级','未评级') CREDIT_LEVEL , 

serial_number,ICCID ,CHARGE_FEE,CARD_FEE,EMPTY_CARD_ID,staff_id,channel_id,update_time,transaction_id,ORD_CODE 
  FROM TF_F_RECARD_INFO a
  WHERE 1=1
  AND SERIAL_NUMBER=:SERIAL_NUMBER 
  AND update_time >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
  AND update_time <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')

  ORDER BY UPDATE_TIME DESC  