INSERT INTO tf_f_whiteuser(pspt_type_code,pspt_id,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,eparchy_code,start_date,end_date,in_date,staff_id,depart_id,remark)
 VALUES(:PSPT_TYPE_CODE,:PSPT_ID,null,null,null,null,null,:EPARCHY_CODE,sysdate,TO_DATE('2050-12-31 23:59:59','YYYY-MM-DD HH24:MI:SS'),sysdate,:STAFF_ID,:DEPART_ID,:REMARK)