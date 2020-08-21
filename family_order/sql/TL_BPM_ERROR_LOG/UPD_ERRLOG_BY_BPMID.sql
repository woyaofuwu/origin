UPDATE tl_bpm_error_log
   SET token_id=TO_NUMBER(:TOKEN_ID),err_code=substr(:ERR_CODE,1,6),err_desc=:ERR_DESC,err_detail_desc=substrb(:ERR_DETAIL_DESC,1,2000),err_time=SYSDATE,deal_tag='0',deal_desc='未处理',rsrv_str1=(RSRV_STR1+1),rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3  
 WHERE bpm_id=TO_NUMBER(:BPM_ID)