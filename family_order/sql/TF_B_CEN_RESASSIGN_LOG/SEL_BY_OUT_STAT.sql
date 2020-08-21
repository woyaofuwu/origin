SELECT res_kind_code,capacity_type_code,assign_staff_id,
sum(assign_num) assign_num,eparchy_code_n 
  FROM tf_b_cen_resassign_log
 WHERE assign_tag=1
 and   res_type_code='3'
 AND assign_time >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') 
 AND assign_time <=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 AND (:OPER_STAFF_ID is null or assign_staff_id=:OPER_STAFF_ID)
 AND (:VALUE_CARD_TYPE_CODE is null or res_kind_code=:VALUE_CARD_TYPE_CODE)
 AND (:VALUE_CODE is null or capacity_type_code=:VALUE_CODE)
 AND (:EPARCHY_CODE is null or eparchy_code_n=:EPARCHY_CODE)
group by res_kind_code,capacity_type_code,assign_staff_id,eparchy_code_n