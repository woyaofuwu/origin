UPDATE TL_O_REALTIMEMARKETINGTRADE A
   SET RSRV_NUM1 = NVL(RSRV_NUM1,0) + 1
 WHERE STEP_ID = :STEPID
   AND exists (select 1 from TL_O_REALTIMEMARKETING b where a.req_id = b.req_id and b.serial_number = :SERIAL_NUMBER)