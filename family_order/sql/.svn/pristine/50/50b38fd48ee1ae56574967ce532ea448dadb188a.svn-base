INSERT INTO TL_B_USER_GIVE_CLASS_DETAIL
  (

TRADE_ID,
USER_CLASS,
USER_ID,
SERIAL_NUMBER,
GIVE_USER_ID,
GIVE_SERIAL_NUMBER,
FROMTRADEID,
UPDATE_TIME,
UPDATE_STAFF_ID,
UPDATE_DEPART_ID,
UPDATE_CITY_CODE,
START_DATE,
END_DATE,
REMARK,
RSRV_NUM1,
RSRV_STR1,
RSRV_STR2,
RSRV_STR3,
RSRV_DATE1,
RSRV_TAG1

  )
VALUES
  (

:TRADE_ID,           
:USER_CLASS,
:USER_ID,
:SERIAL_NUMBER,
:GIVE_USER_ID,
:GIVE_SERIAL_NUMBER,
:FROMTRADEID,                    
 TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'),
:UPDATE_STAFF_ID,                    
:UPDATE_DEPART_ID,                    
:UPDATE_CITY_CODE,     
 nvl(TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss'),sysdate),
 nvl(TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'),TO_DATE('2050-12-31 23:59:59', 'yyyy-mm-dd hh24:mi:ss')),
:REMARK,                            
:RSRV_NUM1,                    
:RSRV_STR1,                    
:RSRV_STR2,                    
:RSRV_STR3,                    
 TO_DATE(:RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS'),                    
:RSRV_TAG1                    

   )