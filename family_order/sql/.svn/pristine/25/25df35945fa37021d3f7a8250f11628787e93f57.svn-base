--IS_CACHE=Y
select DISCNT_CODE,DISCNT_EXPLAIN,START_DATE,END_DATE,EPARCHY_CODE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5
  from TD_B_QRY_DISCNT d 
 where d.discnt_code = :DISCNT_CODE
   and d.rsrv_str1 = :DISCNT_TYPE
   and sysdate between d.start_date and d.end_date
   and (d.eparchy_code = :EPARCHY_CODE or 'ZZZZ' = d.eparchy_code)