SELECT COUNT(1) recordcount FROM dual
WHERE to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss') BETWEEN trunc(SYSDATE,'mm')+:NUM-1 AND trunc(last_day(SYSDATE))+1-0.00001