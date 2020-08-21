SELECT 1 recordcount
  FROM dual
 WHERE TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss') + 1/24/60/60 >= TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')