--IS_CACHE=Y
SELECT 'Effectconditem' key,effect_cond_item_code value1,-1 value2,effect_cond_item vresult
  FROM td_a_effectconditem
 WHERE 'Effectconditem'=:KEY