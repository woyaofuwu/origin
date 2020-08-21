--IS_CACHE=Y
SELECT sp_class,remark,eparchy_code,tag_code 
  FROM td_a_sp_class
 WHERE eparchy_code=:EPARCHY_CODE