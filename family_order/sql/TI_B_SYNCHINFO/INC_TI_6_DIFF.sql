SELECT USER_ID,MAIN_TAG,SERVICE_ID,STATE_CODE,START_DATE,END_DATE
    FROM TF_F_USER_SVCSTATE
    WHERE user_id = :USER_ID
     And  PARTITION_ID =Mod(to_number(:USER_ID ),10000)
    MINUS
    SELECT USER_ID,MAIN_TAG,SERVICE_ID,STATE_CODE,START_DATE,END_DATE
    FROM TF_B_TRADE_SVCSTATE_BAK
    WHERE TRADE_ID = :TRADE_ID
    AND ACCEPT_MONTH = :ACCEPT_MONTH