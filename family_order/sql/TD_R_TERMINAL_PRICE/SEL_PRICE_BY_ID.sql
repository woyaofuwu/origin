--IS_CACHE=Y
SELECT 
PRICE_ID,
DEAL_PRICE,
SALE_PRICE,
UPDATE_TIME,
F_RES_GETCODENAME('staff_id',UPDATE_STAFF_ID, '', '')UPDATE_STAFF_ID 
FROM TD_R_TERMINAL_PRICE  WHERE PRICE_ID=:PRICE_ID