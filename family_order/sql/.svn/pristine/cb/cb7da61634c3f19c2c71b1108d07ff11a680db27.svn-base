select check_no, oper_date_str, oper_time, res_type_code, oper_flag, stat_type, oper_staff_id,
       check_result_tag, res_kind_code, capacity_type_code, produce_batch_id, eparchy_code, city_code,
       depart_id, staff_id, fact_value, update_time, update_staff_id, rsrv_tag1, rsrv_tag2, rsrv_tag3, 
       para_value1, para_value2, para_value3, para_value4, para_value5, para_value6, para_value7, 
       para_value8, para_value9, para_value10, para_value11, para_value12, para_value13, para_value14, 
       para_value15, para_value16, para_value17, para_value18, rdvalue1, rdvalue2, remark2 
from TF_B_RESCHECK_LOG
where oper_time>=TO_DATE(:RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS')
  and oper_time<=TO_DATE(:RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS')
  and (:RES_TYPE_CODE is null or res_type_code = :RES_TYPE_CODE)
  and (:RES_KIND_CODE is null or res_kind_code = :RES_KIND_CODE)
  and (:STAT_TYPE is null or stat_type = :STAT_TYPE)
  and (:CHECK_RESULT_TAG is null or check_result_tag = :CHECK_RESULT_TAG)
  and (:EPARCHY_CODE is null or eparchy_code = :EPARCHY_CODE)
  and (:CITY_CODE is null or city_code = :CITY_CODE)
  and (:DEPART_ID is null or depart_id = :DEPART_ID)