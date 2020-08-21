UPDATE TF_B_TRADE_NP
   SET MESSAGE_ID     = :MESSAGE_ID,
       BOOK_SEND_TIME = TO_DATE(:RETRY_TIME, 'YYYY-MM-DD HH24:MI:SS'),
       MSG_CMD_CODE   = :MSG_CMD_CODE,
       SEND_TIMES     = SEND_TIMES + 1,
       STATE          = :STATE,
       REMARK         = :REMARK
 WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
   AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))