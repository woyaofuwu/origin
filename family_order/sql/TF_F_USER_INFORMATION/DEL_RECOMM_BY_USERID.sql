UPDATE TF_F_USER_INFORMATION
SET ENABLE_TAG = '0',END_DATE = SYSDATE
WHERE USER_ID = :USER_ID
AND TRADE_ATTR = '3'
AND ENABLE_TAG = '1'
AND END_DATE > SYSDATE