--IS_CACHE=Y
SELECT 
  CONTENT_CODE,
  CONTENT_NAME,
  NODE_CODE,
  NODE_NAME 
FROM  
  TD_S_CONT_CATEGORY_RELATION
WHERE 
   NODE_CODE=:NODE_CODE