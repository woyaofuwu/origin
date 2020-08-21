update tf_b_rescheck_profitloss set 
check_result_tag = decode(:CHECK_RESULT_TAG,-1,check_result_tag,:CHECK_RESULT_TAG),
deal_tag = decode(:DEAL_TAG,-1,deal_tag,:DEAL_TAG),
remark = decode(:REMARK,'',remark,:REMARK),
rsrv_str2 = decode(:RSRV_STR2,'',rsrv_str2,:RSRV_STR2),
update_staff_id = :UPDATE_STAFF_ID,
update_time = sysdate
where check_no = :CHECK_NO
And (:RES_NO is null or res_no = :RES_NO)