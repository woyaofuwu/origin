--IS_CACHE=Y
SELECT eparchy_code,deposit_code,integrate_item_code,limit_mode,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,remark 
  FROM td_a_depositlimitrule
 WHERE eparchy_code=:EPARCHY_CODE