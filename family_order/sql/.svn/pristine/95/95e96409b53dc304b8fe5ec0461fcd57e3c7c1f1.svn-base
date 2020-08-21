UPDATE TL_B_PLATSVC_STOP_MNG
   SET SP_CODE    = :SP_CODE,
       BIZ_CODE   = :BIZ_CODE,
       END_DATE   = to_date(:END_DATE, 'yyyy-mm-dd'),
       STATUS     = :STATUS,
       START_DATE = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')
 WHERE 1 = 1
   AND SP_CODE = :SP_CODE
   AND BIZ_CODE = :BIZ_CODE