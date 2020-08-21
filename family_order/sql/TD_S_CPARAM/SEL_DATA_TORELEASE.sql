SELECT count(1) recordcount
  from TF_F_GROUP_BADINFO t
 WHERE 1 = 1
   AND (t.SERIAL_NUMBER = :SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
   AND (t.SOURCE_DATA = :SOURCE_DATA OR :SOURCE_DATA IS NULL)
   AND (trunc(t.HANDLING_TIME) <= to_date(:REPORT_END_TIME, 'yyyy-mm-dd') OR to_date(:REPORT_END_TIME, 'yyyy-mm-dd') IS NULL)
   AND (trunc(t.HANDLING_TIME) >= to_date(:REPORT_START_TIME, 'yyyy-mm-dd') OR to_date(:REPORT_START_TIME, 'yyyy-mm-dd') IS NULL)
   AND BLACK_STATE = :BLACK_STATE