SELECT a.contract_id,to_char(a.batch_id) batch_id,a.res_kind_code, a.CAPACITY_TYPE_CODE,
       a.factory_code, SUM(NVL(para_value9,0)) rsrv_num1
  FROM tf_b_resorder_contract a, tf_b_resdaystat_log b
 WHERE a.res_type_code=:RES_TYPE_CODE
   AND b.para_value1='BLANKSTOCK'
   AND b.para_value3=a.contract_id
   AND b.para_value4=to_char(a.BATCH_ID)
   AND b.res_type_code='9'
   AND b.depart_id=:DEPART_ID
GROUP BY a.contract_id, a.batch_id, a.res_kind_code,a.capacity_type_code,a.factory_code