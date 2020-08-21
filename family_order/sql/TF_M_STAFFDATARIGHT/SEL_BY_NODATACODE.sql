--IS_CACHE=Y
SELECT COUNT(1) PARAM_CODE FROM (
  SELECT 1 FROM TF_M_STAFFDATARIGHT t WHERE t.DATA_CODE=:DATA_CODE 
  AND t.DATA_TYPE='1'
  AND t.RIGHT_ATTR='0'
  AND t.RIGHT_TAG='1'
  AND t.STAFF_ID=:STAFF_ID
  UNION 
  SELECT 1 FROM TF_M_STAFFDATARIGHT t,TF_M_ROLEDATARIGHT a WHERE t.DATA_CODE=a.ROLE_CODE 
  AND t.DATA_TYPE='1'
  AND t.RIGHT_ATTR='1'
  AND t.RIGHT_TAG='1'
  AND t.STAFF_ID=:STAFF_ID
  AND a.DATA_CODE=:DATA_CODE
)