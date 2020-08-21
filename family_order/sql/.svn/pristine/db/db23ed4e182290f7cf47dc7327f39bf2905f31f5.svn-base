--IS_CACHE=Y
SELECT rsrv_str1 paracode,serv_name paraname
  FROM tf_cop_service a
 WHERE EXISTS(SELECT 1 FROM td_s_commpara b WHERE b.param_attr = '958' AND b.para_code1=a.serv_kind)
   AND a.state='40'
   AND (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)