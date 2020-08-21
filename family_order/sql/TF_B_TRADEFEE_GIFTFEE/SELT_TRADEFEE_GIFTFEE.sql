 SELECT 
   B.USER_ID,
   B.TRADE_ID,
       DECODE(B.FEE_MODE,
              '0','营业费用',
              '1','押金',
              '2','赠预存',
              '3','帐前调帐',
              '其他') FEE_MODE,
       B.FEE
  FROM TF_B_TRADEFEE_GIFTFEE B
 WHERE B.TRADE_ID = :TRADE_ID
   AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))