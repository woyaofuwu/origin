INSERT INTO td_m_res_stockjob (eparchy_code,stock_pos,job,res_type_code,city_code,add_tag,res_kind_code,warnning_value_u,warnning_value_d,link_phone,manager_staff_id,rsrv_tag1,rsrv_tag2,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_date1,rsrv_date2,remark,update_time,update_staff_id,update_depart_id) 
 VALUES(:EPARCHY_CODE,:STOCK_POS,:JOB,:RES_TYPE_CODE,:CITY_CODE,:ADD_TAG,:RES_KIND_CODE,TO_NUMBER(:WARNNING_VALUE_U),TO_NUMBER(:WARNNING_VALUE_D),:LINK_PHONE,:MANAGER_STAFF_ID,:RSRV_TAG1,:RSRV_TAG2,:RSRV_STR1,:RSRV_STR2,:RSRV_STR3,:RSRV_STR4,:RSRV_NUM1,:RSRV_NUM2,:RSRV_NUM3,TO_NUMBER(:RSRV_NUM4),TO_DATE(:RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS'),:REMARK,sysdate,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID)