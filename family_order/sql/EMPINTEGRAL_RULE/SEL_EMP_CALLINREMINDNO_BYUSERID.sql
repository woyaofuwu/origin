--IS_CACHE=N
SELECT decode(COUNT(1),0,0,1) 是否指定套餐
FROM ucr_crm1.TF_BH_TRADE A, ucr_crm1.TF_B_TRADE_SVC B
       WHERE A.USER_ID = TO_NUMBER(:USER_ID)
         AND B.USER_ID = TO_NUMBER(:USER_ID)
         AND A.TRADE_TYPE_CODE IN (10, 110) --开户和产品变更
         AND A.PRODUCT_ID IN ('10002115',
                              '10002116',
                              '10001316',
                              '10001317',
                              '10001318',
                              '10001319')
         AND A.TRADE_ID = B.TRADE_ID
         AND A.ACCEPT_MONTH = TO_NUMBER(TO_CHAR(SYSDATE, 'MM'))
         AND B.ACCEPT_MONTH = TO_NUMBER(TO_CHAR(SYSDATE, 'MM'))
         AND B.SERVICE_ID = 231
         AND B.MODIFY_TAG = '0'