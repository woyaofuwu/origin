SELECT t.PARA_CODE5,t.PARAM_CODE
  FROM TD_S_COMMPARA t
  WHERE t.PARAM_ATTR =:PARAM_ATTR
  AND t.PARAM_CODE =:PARAM_CODE