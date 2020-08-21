SELECT DISTINCT TO_CHAR(A.TRADE_ID) TRADE_ID,
                A.TRADE_TYPE_CODE,
                A.IN_MODE_CODE,
                TO_CHAR(A.USER_ID) USER_ID,
                TO_CHAR(A.CUST_ID) CUST_ID,
                A.SERIAL_NUMBER,
                TO_CHAR(A.ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,
                A.TRADE_STAFF_ID,
                A.TRADE_DEPART_ID,
                A.TRADE_CITY_CODE,
                A.TRADE_EPARCHY_CODE,
                A.CITY_CODE,
                C.RULE_ID, 
                E.PARA_CODE1, 
                E.PARA_CODE3, 
                C.RES_ID, 
                C.GOODS_NAME, 
                C.ACTION_COUNT, 
                C.SCORE_CHANGED, 
                A.REMARK
  FROM TF_BH_TRADE      A,
       TF_B_TRADE_SCORE C,
       TD_S_COMMPARA    E
 WHERE A.USER_ID = TO_NUMBER(:USER_ID)
   AND A.ACCEPT_MONTH = SUBSTR(A.TRADE_ID, 5, 2)
   AND C.ACCEPT_MONTH = SUBSTR(C.TRADE_ID, 5, 2)
   AND A.CANCEL_TAG = '0'
   AND A.TRADE_TYPE_CODE = 330
   AND A.TRADE_ID = C.TRADE_ID
   AND C.RULE_ID = E.PARAM_CODE
   AND E.PARAM_ATTR = '59'