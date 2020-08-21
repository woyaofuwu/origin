SELECT DECODE(RESULT_TYPE,
              '1',
              'BOSS比平台多',
              '2',
              'BOSS比平台少',
              RESULT_TYPE) RESULT_TYPE,
       EXERN_ORDER,
       INNER_ORDER,
       SERIAL_NUMBER,
       EXERN_CHENNEL,
       GOODS_CODE,
       GOODS_RATE,
       UDISCOUNT,
       AMOUNT_PAY,
       OPR_NUM
  FROM TF_B_OPEN_BALANCE_RESULT A
 WHERE 1 = 1
   AND A.RECON_DATE = :RECON_DATE
   AND A.RESULT_TYPE = :RESULT_TYPE