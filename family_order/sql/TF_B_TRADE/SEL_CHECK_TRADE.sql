SELECT TRADE_ID, TRADE_TYPE_CODE
  FROM TF_B_TRADE T
 WHERE T.USER_ID = TO_NUMBER(:USER_ID)
   AND TRADE_ID = (SELECT MAX(A.TRADE_ID)
                     FROM TF_B_TRADE A
                    WHERE A.USER_ID = to_number(:USER_ID)
                      AND A.TRADE_ID != TO_NUMBER(:TRADE_ID))