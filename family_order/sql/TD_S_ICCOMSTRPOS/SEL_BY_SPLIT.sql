--IS_CACHE=Y
SELECT switch_pos,value_type,switch_value,pre_value,remark,mml_value 
  FROM td_s_iccomstrpos
 WHERE trade_type_code=:TRADE_TYPE_CODE
   AND dir_tag=:DIR_TAG