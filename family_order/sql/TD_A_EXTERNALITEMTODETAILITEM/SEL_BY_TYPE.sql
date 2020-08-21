--IS_CACHE=Y
SELECT external_item_code,remark 
  FROM td_a_externalitemtodetailitem
 WHERE external_sys_type=:EXTERNAL_SYS_TYPE