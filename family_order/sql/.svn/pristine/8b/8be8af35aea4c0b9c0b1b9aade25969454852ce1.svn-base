select partition_id,user_id,inst_id,mp_group_cust_code,group_id,biz_mode,province_code,cust_name,merch_spec_code,product_spec_code,product_id,product_offer_id,pay_type,oper_type,service_id,limit_fee,start_date,end_date,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
from TF_F_USER_GRP_CENPAY
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND END_DATE>SYSDATE