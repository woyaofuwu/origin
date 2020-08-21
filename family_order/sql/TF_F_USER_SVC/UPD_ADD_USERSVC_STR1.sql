UPDATE TF_F_USER_SVC A
   SET A.RSRV_STR1 = (SELECT DISCNT_CODE
                        FROM TF_B_TRADE_DISCNT A
                       WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
                         AND ACCEPT_MONTH =
                             TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
                         AND MODIFY_TAG = '0')
 WHERE A.USER_ID = TO_NUMBER(:USER_ID)
 AND a.service_id ='22'