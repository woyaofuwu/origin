 SELECT 
 A.SMS_SCRIPT      SMS_CONTENT
 FROM TL_O_REALTIMEMARKETINGTRADE A
 WHERE A.REQ_ID = :REQ_ID  AND A.SALE_ACT_ID = :SALE_ACT_ID 
