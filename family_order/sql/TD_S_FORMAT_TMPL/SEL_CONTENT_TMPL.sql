--IS_CACHE=Y
SELECT TMPL_ID,CHAN_ID,TMPL_NAME,TMPL_TYPE,TMPL_SUB_TYPE,STAFF_ID,UPDATE_DATE,APPROVE_STAFF,APPROVE_RESULT,NEED_APPROVE,TEMP_STATUS 
FROM TD_S_FORMAT_TMPL
WHERE 1=1
AND CHAN_ID=:CHAN_ID 
AND TMPL_NAME=:TMPL_NAME 
AND TMPL_TYPE=:TMPL_TYPE
AND TEMP_STATUS>:TEMP_STATUS