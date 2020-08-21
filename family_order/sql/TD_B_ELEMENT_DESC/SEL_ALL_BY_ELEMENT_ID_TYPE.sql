--IS_CACHE=Y
select ELEMENT_ID,
       ELEMENT_TYPE_CODE,
       CHANNEL_ID,
       ELEMENT_DESC,
       to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5
  from TD_B_ELEMENT_DESC
 where ELEMENT_ID = :ELEMENT_ID
   and ELEMENT_TYPE_CODE = :ELEMENT_TYPE_CODE
   and CHANNEL_ID = :CHANNEL_ID
   and sysdate between START_DATE and END_DATE