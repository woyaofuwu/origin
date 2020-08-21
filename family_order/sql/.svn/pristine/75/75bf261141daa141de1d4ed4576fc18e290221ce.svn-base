select start_res_no,end_res_no,apply_num,rsrv_num1,rsrv_tag1,audit_remark,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,remark
from TF_B_RESAPPLY_DETAIL
where ((:RES_NO_S is null and :RES_NO_E is null) or (start_res_no>=:RES_NO_S and end_res_no<= :RES_NO_E))
  and (:RSRV_STR4 is null or rsrv_str3 = :RSRV_STR4)
  and (:RSRV_STR5 is null or rsrv_str4 = :RSRV_STR5)
  and (apply_no is not null and apply_no in (select apply_no 
                        from tf_b_resapply_main 
                       where apply_date>=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS')
                         and apply_date<=TO_DATE(:RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS')
                         and (:APPLY_AREA_CODE is null or apply_area_code = :APPLY_AREA_CODE)
                         and apply_type_code = '3'
                         and (:RES_STATE_CODE is null or rsrv_tag1 = :RES_STATE_CODE)
                      ))