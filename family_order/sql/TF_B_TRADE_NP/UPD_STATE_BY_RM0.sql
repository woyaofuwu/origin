UPDATE TF_B_TRADE_NP
   SET BOOK_SEND_TIME = TO_DATE(:RETRY_TIME, 'YYYY-MM-DD HH24:MI:SS'),
       STATE          = :STATE,
       MSG_CMD_CODE   = :MSG_CMD_CODE,
       RESULT_CODE    = :RESULT_CODE,
       RESULT_MESSAGE = :RESULT_MESSAGE,
       REMARK         = :REMARK
 WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
   AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))