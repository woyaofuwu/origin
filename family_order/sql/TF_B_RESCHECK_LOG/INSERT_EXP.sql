INSERT INTO TF_B_RESCHECK_LOG
(check_no, oper_date_str, oper_time, res_type_code, oper_flag, stat_type, oper_staff_id, check_result_tag,
 res_kind_code, capacity_type_code, produce_batch_id, eparchy_code, city_code, depart_id, staff_id,
 fact_value, update_time, update_staff_id, rsrv_tag1, rsrv_tag2, rsrv_tag3, para_value1, para_value2,
 para_value3, para_value4, para_value5, para_value6, para_value7, para_value8, para_value9, para_value10,
 para_value11, para_value12, para_value13, para_value14, para_value15, para_value16, para_value17,
 para_value18, rdvalue1, rdvalue2, remark2 )
VALUES
(:CHECK_NO,to_char(sysdate,'YYYYMMDD'),sysdate,:RES_TYPE_CODE,:OPER_FLAG,:STAT_TYPE,:OPER_STAFF_ID,
:CHECK_RESULT_TAG,:RES_KIND_CODE,:CAPACITY_TYPE_CODE,:PRODUCE_BATCH_ID,:EPARCHY_CODE,
:CITY_CODE,:DEPART_ID,:STAFF_ID,:FACT_VALUE,sysdate,:UPDATE_STAFF_ID,:RSRV_TAG1,:RSRV_TAG2,:RSRV_TAG3,
:PARA_VALUE1,:PARA_VALUE2,:PARA_VALUE3,:PARA_VALUE4,:PARA_VALUE5,:PARA_VALUE6,:PARA_VALUE7,:PARA_VALUE8,
:PARA_VALUE9,:PARA_VALUE10,:PARA_VALUE11,:PARA_VALUE12,:PARA_VALUE13,:PARA_VALUE14,:PARA_VALUE15,
:PARA_VALUE16,:PARA_VALUE17,:PARA_VALUE18,:RDVALUE1,:RDVALUE2,:REMARK2)