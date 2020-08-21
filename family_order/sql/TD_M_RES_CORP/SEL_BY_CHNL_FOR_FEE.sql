SELECT CHNL_ID,
       FACTORY_CODE,
       BALANCE / 100 BALANCE,
       YEAR,
       TO_CHAR(ACCEPT_MONTH) ACCEPT_MONTH,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       DECODE(STATUS, '0', '未缴清', '1', '已缴清', '未知') RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       UPDATE_STAFF,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       STATUS,
       REMARK
  FROM TF_R_FEE_MARKET
 WHERE trim(CHNL_ID) = :CHNL_ID
   AND trim(FACTORY_CODE) = :FACTORY_CODE
   AND trim(STATUS) = :STATUS