update TF_F_SELFHELPCARD_FLOW
   set state           = :STATE,
       remark          = :REMARK,
       update_time     = to_date(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),
       RSRV_STR1       = :RSRV_STR1,
       PIN_NEW         = :PIN_NEW,
       PIN2_NEW        = :PIN2_NEW,
       PUK_NEW         = :PUK_NEW,
       PUK2_NEW        = :PUK2_NEW,
       KI_NEW          = :KI_NEW,
       IMSI_NEW        = :IMSI_NEW,
       OPC_NEW         = :OPC_NEW,
       SIM_CARD_NO_NEW = :SIM_CARD_NO_NEW,
       RSRV_STR5=:RSRV_STR5
 WHERE TRANS_ID = :TRANS_ID