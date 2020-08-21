SELECT *
  FROM TF_B_TRADE_DISCNT B
 WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
   AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND B.USER_ID = TO_NUMBER(:USER_ID)
   AND MODIFY_TAG =:MODIFY_TAG
   AND EXISTS
 (SELECT 1
          FROM TD_S_COMMPARA
         WHERE SUBSYS_CODE = 'CSM'
           AND PARAM_ATTR = 2001
           AND PARAM_CODE = :PARAM_CODE
           AND PARA_CODE1 = TO_CHAR(B.DISCNT_CODE)
           AND (EPARCHY_CODE = :EPARCHY_CODE OR EPARCHY_CODE = 'ZZZZ')
           AND SYSDATE BETWEEN START_DATE AND END_DATE)