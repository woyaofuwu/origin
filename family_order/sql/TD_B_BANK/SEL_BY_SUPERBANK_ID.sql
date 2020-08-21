--IS_CACHE=Y
SELECT type_id,
       data_id,
       data_name,
       pdata_id,
       subsys_code,
       eparchy_code,
       remark,
       update_staff_id,
       update_depart_id,
       update_time
  FROM td_s_static
 WHERE type_id = 'TD_B_BANK'
   AND pdata_id = :PDATA_ID