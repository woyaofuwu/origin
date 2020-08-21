SELECT partition_id,to_char(cust_id) cust_id,cust_name,cust_type,cust_state,pspt_type_code,pspt_id,open_limit,eparchy_code,city_code,cust_passwd,to_char(score_value) score_value,credit_class,to_char(basic_credit_value) basic_credit_value,to_char(credit_value) credit_value,remove_tag,to_char(remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date,develop_depart_id,develop_staff_id,in_depart_id,in_staff_id,to_char(in_date,'yyyy-mm-dd hh24:mi:ss') in_date,remark,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10 
  FROM tf_f_customer
 WHERE cust_id in (select b.cust_id from tf_f_relation_uu a,tf_f_user b
                  where a.user_id_b=TO_NUMBER(:USER_ID)
		 and a.partition_id=mod(TO_NUMBER(:USER_ID),10000)
		 and a.relation_type_code in ('20','21')
		 and a.user_id_a=b.user_id
		 and b.partition_id=mod(a.user_id_a,10000)
                 and sysdate between a.start_date and a.end_date
              )