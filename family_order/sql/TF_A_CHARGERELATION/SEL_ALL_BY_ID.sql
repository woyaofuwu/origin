SELECT TO_CHAR(OPERATE_ID1) OPERATE_ID1,
       OPERATE_TYPE1,
       TO_CHAR(OPERATE_ID2) OPERATE_ID2,
       OPERATE_TYPE2,
       RELATION_TYPE,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       PARTITION_ID,
       EPARCHY_CODE,
       TO_CHAR(TRADE_ID) TRADE_ID
  FROM TF_A_CHARGERELATION
 WHERE DECODE(:MODE, '0', TRADE_ID, '1', OPERATE_ID1, '2', OPERATE_ID2) =
       :OPARATE_ID
   AND DECODE(:TYPE,
              '0',
              OPERATE_TYPE1,
              '1',
              OPERATE_TYPE2,
              '2',
              RELATION_TYPE,
              '3',
              1) = :TYPECODE