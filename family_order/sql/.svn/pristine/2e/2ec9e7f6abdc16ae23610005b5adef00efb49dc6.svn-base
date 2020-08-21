--IS_CACHE=Y
SELECT eparchy_code,item_prior_rule_id,integrate_item_code,operate_type,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_a_itemtyperule
 WHERE eparchy_code=:EPARCHY_CODE
   AND item_prior_rule_id=:ITEM_PRIOR_RULE_ID
   AND operate_type=:OPERATE_TYPE