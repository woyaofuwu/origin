UPDATE tf_f_customer
SET (cust_name,cust_type,cust_state,pspt_type_code,pspt_id,open_limit,eparchy_code,city_code,cust_passwd,
score_value,credit_class,basic_credit_value,credit_value,remove_tag,remove_date,develop_depart_id,develop_staff_id,
in_depart_id,in_staff_id,in_date,remark,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,
rsrv_str8,rsrv_str9,rsrv_str10)
 = (SELECT cust_name,cust_type,cust_state,pspt_type_code,pspt_id,open_limit,eparchy_code,city_code,
cust_passwd,score_value,credit_class,basic_credit_value,credit_value,remove_tag,remove_date,develop_depart_id,develop_staff_id,in_depart_id,in_staff_id,in_date,remark,rsrv_str1,rsrv_str2,rsrv_str3,
rsrv_str4,rsrv_str5, rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10
  FROM Tf_b_Trade_Customer_Bak
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND cust_id = TO_NUMBER(:CUST_ID))
 WHERE cust_id = TO_NUMBER(:CUST_ID)
   AND partition_id = MOD(TO_NUMBER(:CUST_ID),10000)