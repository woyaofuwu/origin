--IS_CACHE=Y
SELECT count(*)  X_IS_BLACK_USER
  FROM td_o_blackuser
 WHERE pspt_type_code=:PSPT_TYPE_CODE
   AND pspt_id=:PSPT_ID
   AND sysdate BETWEEN start_date AND end_date
   AND rownum < 2