SELECT
                TO_CHAR(B.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
                DECODE(B.RSRV_VALUE_CODE, 'SI', '1', '0') GOODS_STATE, 
                B.RSRV_VALUE, 
                B.RSRV_STR1, 
                B.RSRV_STR2, 
                B.RSRV_STR3  
FROM
       TF_F_USER_OTHER  B

WHERE
    B.PARTITION_ID(+) = MOD(:USER_ID,10000)
   AND B.USER_ID(+) = :USER_ID
   AND B.RSRV_VALUE_CODE(+) = 'SI'
   AND B.RSRV_VALUE(+) = :TRADE_ID
   AND B.RSRV_STR1(+) = :RULE_ID
   AND EXISTS (SELECT 1
          FROM TD_B_EXCHANGE_RULE D
         WHERE D.RULE_ID = :RULE_ID
           AND D.EXCHANGE_TYPE_CODE = '9'
           AND D.END_DATE > D.START_DATE)