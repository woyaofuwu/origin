insert into TF_B_TRADE_POPOLICY                                                                                
(TRADE_ID     ,PARTITION_ID ,USER_ID      ,POSPECNUMBER ,PORATENUMBER ,OPER_CODE    ,START_DATE   ,END_DATE     )
values                                                                                                         
(:TRADE_ID,   MOD(TO_NUMBER(:USER_ID),10000) ,:USER_ID,     :POSpecNumber,:Poratenumber,:OPER_CODE,   TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'), TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'))