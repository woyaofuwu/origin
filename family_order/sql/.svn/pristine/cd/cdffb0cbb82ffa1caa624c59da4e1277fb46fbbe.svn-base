SELECT partition_id,to_char(cust_id) cust_id
   ,cust_name,cust_type,cust_state
   ,pspt_type_code,pspt_id,open_limit
   ,eparchy_code,city_code,cust_passwd
   ,to_char(score_value) score_value,credit_class
   ,to_char(basic_credit_value) basic_credit_value
   ,to_char(credit_value) credit_value
   ,remove_tag
   ,to_char(remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date
   ,develop_depart_id,develop_staff_id
   ,in_depart_id,in_staff_id
   ,to_char(in_date,'yyyy-mm-dd hh24:mi:ss') in_date
   ,remark,rsrv_str1
   ,rsrv_str2,rsrv_str3,rsrv_str4
   ,rsrv_str5,rsrv_str6,rsrv_str7
   ,rsrv_str8,rsrv_str9,rsrv_str10
   ,remove_staff_id,remove_change
   ,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
   ,update_staff_id,update_depart_id
   ,rsrv_num1,rsrv_num2
   ,rsrv_num3,rsrv_num4
   ,rsrv_num5
   ,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1
   ,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2
   ,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3
   ,to_char(rsrv_date4,'yyyy-mm-dd hh24:mi:ss') rsrv_date4
   ,to_char(rsrv_date5,'yyyy-mm-dd hh24:mi:ss') rsrv_date5
   ,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_tag4,rsrv_tag5
   ,cust_kind
  FROM tf_f_customer
 WHERE pspt_type_code=:PSPT_TYPE_CODE
   AND pspt_id=:PSPT_ID
   AND rownum <= 30