INSERT INTO td_b_si_product(si_product_id,si_product_name,si_biz_mode,is_si_prod,reg_date,start_date,end_date,fee_code,fee_type,func_fee,comu_fee,si_id,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_date1,rsrv_date2,rsrv_date3,tag_set,update_time)
 VALUES(:SI_PRODUCT_ID,:SI_PRODUCT_NAME,:SI_BIZ_MODE,:IS_SI_PROD,TO_DATE(:REG_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),:FEE_CODE,:FEE_TYPE,TO_NUMBER(:FUNC_FEE),TO_NUMBER(:COMU_FEE),:SI_ID,:RSRV_STR1,:RSRV_STR2,:RSRV_STR3,TO_NUMBER(:RSRV_NUM1),TO_NUMBER(:RSRV_NUM2),TO_NUMBER(:RSRV_NUM3),TO_DATE(:RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS'),:TAG_SET,sysdate)