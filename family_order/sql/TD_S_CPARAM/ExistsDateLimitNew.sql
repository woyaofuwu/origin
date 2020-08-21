SELECT count(1) recordcount
  FROM dual
 WHERE SYSDATE < ADD_MONTHS(to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss'),:NUM)
 AND to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss') >= to_date('2015-04-01','yyyy-mm-dd hh24:mi:ss')