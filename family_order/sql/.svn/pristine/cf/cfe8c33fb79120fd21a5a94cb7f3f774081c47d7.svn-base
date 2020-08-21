--IS_CACHE=Y
select TRADE_TYPE_CODE,
       IN_MODE_CODE,
       VIP_CLASS_ID,
       PRODUCT_ID,
       PACKAGE_ID,
       ELEMENT_TYPE_CODE,
       ELEMENT_ID,
       TO_CHAR(CAMPN_ID) CAMPN_ID,
       FEE_MODE,
       FEE_TYPE_CODE,
       TO_CHAR(FEE) FEE,
       TO_CHAR(LIMIT_MONEY) LIMIT_MONEY,
       PAY_MODE,
       IN_DEPOSIT_CODE,
       OUT_DEPOSIT_CODE,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       EPARCHY_CODE,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_STR6,
       RSRV_STR7,
       RSRV_STR8,
       RSRV_STR9,
       RSRV_STR10
  from TD_B_PRODUCT_TRADEFEE T
 where (T.TRADE_TYPE_CODE = :TRADE_TYPE_CODE or TRADE_TYPE_CODE = '-1')
   and t.pay_mode = '0'
   and (T.ELEMENT_TYPE_CODE = :ELEMENT_TYPE_CODE or
       ELEMENT_TYPE_CODE is null)
   and (T.PRODUCT_ID = :PRODUCT_ID or T.PRODUCT_ID = '-1')
   and (T.PACKAGE_ID = :PACKAGE_ID or T.PACKAGE_ID = '-1')
   and (T.ELEMENT_ID = :ELEMENT_ID or T.ELEMENT_ID = '-1')
   and (T.EPARCHY_CODE = :EPARCHY_CODE or T.EPARCHY_CODE = 'ZZZZ')
   and sysdate between T.START_DATE and T.END_DATE