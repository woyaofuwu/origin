UPDATE TF_B_TRADE_NP
   SET STATE          = :STATE,
       MESSAGE_ID     = :MESSAGE_ID,
       MSG_CMD_CODE   = :MSG_CMD_CODE,
       RSRV_STR4      = :DEALMETHOD,
       RESULT_CODE    = :RESULT_CODE,
       RESULT_MESSAGE = :RESULT_MESSAGE
 WHERE TRADE_ID = :TRADE_ID