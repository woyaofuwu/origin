--IS_CACHE=Y
SELECT integrate_item_code,bank_item_name,eparchy_code 
  FROM td_a_bankintegrateitem
 WHERE eparchy_code=:EPARCHY_CODE