insert into TF_F_USER_Check4BuyIstead
  (CHECK_ID     ,
   SERIAL_NUMBER,
   USER_ID      ,
   STAFF_ID     ,
   CHECK_TIME   ,
   CHECK_STATE  ,
   REMARK       ,
   RSRV_NUM1    ,
   RSRV_NUM2    ,
   RSRV_NUM3    ,
   RSRV_NUM4    ,
   RSRV_NUM5    ,
   RSRV_STR1    ,
   RSRV_STR2    ,
   RSRV_STR3    ,
   RSRV_STR4    ,
   RSRV_STR5    ,
   RSRV_DATE1   ,
   RSRV_DATE2   ,
   RSRV_DATE3   ,
   RSRV_TAG1    ,
   RSRV_TAG2    ,
   RSRV_TAG3    ,
   IN_DATE
   )
values
  (:CHECK_ID     ,
   :SERIAL_NUMBER,
   :USER_ID      ,
   :STAFF_ID     ,
   :CHECK_TIME   ,
   :CHECK_STATE  ,
   :REMARK       ,
   :RSRV_NUM1    ,
   :RSRV_NUM2    ,
   :RSRV_NUM3    ,
   :RSRV_NUM4    ,
   :RSRV_NUM5    ,
   :RSRV_STR1    ,
   :RSRV_STR2    ,
   :RSRV_STR3    ,
   :RSRV_STR4    ,
   :RSRV_STR5    ,
   :RSRV_DATE1   ,
   :RSRV_DATE2   ,
   :RSRV_DATE3   ,
   :RSRV_TAG1    ,
   :RSRV_TAG2    ,
   :RSRV_TAG3    ,
   SYSDATE)