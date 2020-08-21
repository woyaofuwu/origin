--IS_CACHE=Y
SELECT integrate_item_code,eparchy_code,integrate_item 
  FROM td_a_integrateitem
 WHERE eparchy_code=:EPARCHY_CODE