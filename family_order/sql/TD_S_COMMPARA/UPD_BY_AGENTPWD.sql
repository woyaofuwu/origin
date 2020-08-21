UPDATE td_s_commpara
   set para_code1 = :PARA_CODE1,
       update_staff_id = :UPDATE_STAFF_ID,
       update_depart_id = :UPDATE_DEPART_ID,
       update_time = SYSDATE
 WHERE subsys_code = :SUBSYS_CODE
   AND param_attr = :PARAM_ATTR
   AND param_code = :PARAM_CODE
   AND eparchy_code = :EPARCHY_CODE