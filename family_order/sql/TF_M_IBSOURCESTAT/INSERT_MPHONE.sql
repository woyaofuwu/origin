INSERT INTO tf_m_ibsourcestat(log_id,eparchy_code,stat_month,stat_type_code,stat_type,stat_item1,stat_item2,stat_item3,stat_item4,stat_item5,stat_value1,stat_value2,stat_value3,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark,oper_date,oper_staff_id,oper_depart_id)
 VALUES(TO_NUMBER(:LOG_ID),:EPARCHY_CODE,to_char(sysdate,'YYYYMM'),:STAT_TYPE_CODE,:STAT_TYPE,:STAT_ITEM1,:STAT_ITEM2,:STAT_ITEM3,:STAT_ITEM4,:STAT_ITEM5,TO_NUMBER(:STAT_VALUE1),TO_NUMBER(:STAT_VALUE2),TO_NUMBER(:STAT_VALUE3),:RSRV_TAG1,:RSRV_TAG2,:RSRV_TAG3,TO_DATE(:RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS'),:RSRV_NUM1,:RSRV_NUM2,:RSRV_NUM3,:RSRV_STR1,:RSRV_STR2,:RSRV_STR3,:RSRV_STR4,:RSRV_STR5,:REMARK,sysdate,:OPER_STAFF_ID,:OPER_DEPART_ID)