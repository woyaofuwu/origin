UPDATE TD_M_CORPORATION_SP
   SET END_DATE = to_date(:END_DATE,'yyyy-mm-dd'),
       UPDATE_TIME = sysdate,
       FILE_NAME = :FILE_NAME
 WHERE SP_CODE = :SP_CODE