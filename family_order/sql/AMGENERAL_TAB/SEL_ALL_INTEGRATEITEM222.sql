--IS_CACHE=Y
SELECT integrate_item_code RSRV_NUM1,
       integrate_item PARA_CODE15
  FROM td_a_integrateitem
 order by integrate_item_code