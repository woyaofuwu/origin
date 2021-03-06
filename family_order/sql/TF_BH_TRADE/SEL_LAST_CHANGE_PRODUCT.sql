SELECT trade_id
  FROM TF_BH_TRADE A
 WHERE A.USER_ID = TO_NUMBER(:USER_ID)
   AND A.TRADE_TYPE_CODE = :TRADE_TYPE_CODE
   AND A.CANCEL_TAG = '0'
   AND A.FINISH_DATE >= (SELECT FINISH_DATE B
                           FROM TF_BH_TRADE B
                          WHERE B.TRADE_ID = :TRADE_ID
                            AND B.CANCEL_TAG = '0')
   AND A.FINISH_DATE < SYSDATE