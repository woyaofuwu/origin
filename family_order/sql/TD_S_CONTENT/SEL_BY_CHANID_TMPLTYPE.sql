--IS_CACHE=Y
SELECT CONTENT 
FROM TD_S_CONTENT 
WHERE CONTENT_CODE =(
   SELECT TMPL_CONTENT FROM TD_S_FORMAT_TMPL 
   WHERE CHAN_ID=:CHAN_ID
     AND TMPL_TYPE=:TMPL_TYPE)