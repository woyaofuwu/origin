INSERT INTO tf_f_user_file(partition_id,user_id,assure_cust_id,assure_type_code,assure_date,develop_eparchy_code,develop_city_code,develop_depart_id,develop_staff_id,develop_date,develop_no,in_depart_id,in_staff_id,remove_tag,remark)
 VALUES(:PARTITION_ID,TO_NUMBER(:USER_ID),TO_NUMBER(:ASSURE_CUST_ID),:ASSURE_TYPE_CODE,TO_DATE(:ASSURE_DATE,'YYYY-MM-DD HH24:MI:SS'),:DEVELOP_EPARCHY_CODE,:DEVELOP_CITY_CODE,:DEVELOP_DEPART_ID,:DEVELOP_STAFF_ID,TO_DATE(:DEVELOP_DATE,'YYYY-MM-DD HH24:MI:SS'),:DEVELOP_NO,:IN_DEPART_ID,:IN_STAFF_ID,:REMOVE_TAG,:REMARK)