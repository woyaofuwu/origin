SELECT to_char(USER_ID) USER_ID,
       QUERY_TYPE,
       QRY_ID,
       ID,
       SERIAL_NUMBER,
       NUM,
       PROVINCENAME,
       RECEIVENUM,
       SERVICENAME,
       SERVICEFLAT,
       SERVICEID,
       CONTENTNAME,
       SPNAME,
       SPCSTEL,
       FEETYPE,
       SERFEE,
       INFOFEE,
       STARTTIME,
       ENDTIME,
       DURTIME,
       ORDERTIME,
       ORDERSTATUS,
       FEESTATUS,
       CHANNELNAME,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5
  FROM TF_F_MDO_QUERY_RESULT
 where USER_ID = to_number(:USER_ID)
   and QUERY_TYPE = :QUERY_TYPE
   and QRY_ID = :QRY_ID
   and RSRV_STR1 = :RSRV_STR1
   and RSRV_STR2 = :RSRV_STR2
   and RSRV_STR3 = :RSRV_STR3