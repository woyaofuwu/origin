UPDATE td_s_commpara
   SET para_code2=:PARA_CODE2,
       para_code3=:PARA_CODE3,
       para_code23=:PARA_CODE23,
       para_code24=:PARA_CODE24,
       eparchy_code=:EPARCHY_CODE,
       remark=:REMARK,
       update_staff_id=:UPDATE_STAFF_ID,
       update_depart_id=:UPDATE_DEPART_ID,
       start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),
       end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 WHERE subsys_code=:SUBSYS_CODE
   AND param_attr=:PARAM_ATTR
   AND param_code=:PARAM_CODE
   AND para_code1=:PARA_CODE1