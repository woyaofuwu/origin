UPDATE TD_S_DISCNT_SVC_LIMIT 
SET SERVICE_ID = :SERVICE_ID,
LIMIT_TAG = :LIMIT_TAG,
START_DATE = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),
END_DATE = to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),
EPARCHY_CODE = :EPARCHY_CODE
WHERE DISCNT_CODE = :DISCNT_CODE