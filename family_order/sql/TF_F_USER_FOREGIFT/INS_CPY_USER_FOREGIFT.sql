INSERT INTO tf_f_user_foregift(user_id,foregift_code,money,cust_name,pspt_id,update_time,rsrv_date1, rsrv_date2,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5)
SELECT to_number(:USER_ID_A),foregift_code,money,cust_name,pspt_id,sysdate,rsrv_date1, rsrv_date2,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
 FROM tf_f_user_foregift WHERE user_id = to_number(:USER_ID_B)