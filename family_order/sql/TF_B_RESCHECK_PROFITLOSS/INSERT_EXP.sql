INSERT INTO TF_B_RESCHECK_PROFITLOSS
(check_no, res_no, oper_date_str, oper_time, res_type_code, oper_staff_id, check_result_tag, res_kind_code,
 capacity_type_code, eparchy_code, city_code, depart_id, staff_id, deal_tag, update_time, update_staff_id,
 remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,
 rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3)
VALUES(:CHECK_NO,:RES_NO,to_char(sysdate,'YYYYMMDD'),sysdate,:RES_TYPE_CODE,
:OPER_STAFF_ID,:CHECK_RESULT_TAG,:RES_KIND_CODE,:CAPACITY_TYPE_CODE,:EPARCHY_CODE,:CITY_CODE,
:DEPART_ID,:STAFF_ID,:DAEAL_TAG,sysdate,:UPDATE_STAFF_ID,:REMARK,
:RSRV_TAG1,:RSRV_TAG2,:RSRV_TAG3,:RSRV_DATE1,:RSRV_DATE2,:RSRV_DATE3,:RSRV_STR1,:RSRV_STR2,:RSRV_STR3,
:RSRV_STR4,:RSRV_STR5,:RSRV_STR6,:RSRV_STR7,:RSRV_NUM1,:RSRV_NUM2,:RSRV_NUM3)