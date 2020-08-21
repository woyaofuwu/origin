UPDATE tf_b_resapply_detail
   SET 
        AUDIT_NO = :AUDIT_NO,AUDIT_STAFF_ID =:STAFF_ID,
        AUDIT_DEPART_ID = :AUDIT_DEPART_ID,AUDIT_DATE = sysdate,
       AUDIT_REMARK = :AUDIT_REMARK,AUDIT_TYPE_CODE = :AUDIT_TYPE_CODE , 
        start_res_no=decode(:START_RES_NO,NULL,start_res_no,:START_RES_NO),
       end_res_no=decode(:END_RES_NO,NULL,end_res_no,:END_RES_NO), 
       apply_num=decode(:APPLY_NUM,-1,apply_num,:APPLY_NUM) 
 WHERE APPLY_NO=:APPLY_NO
   AND APPLY_BATCH_ID = :APPLY_BATCH_ID
   AND APPLY_DETAIL_NO = :APPLY_DETAIL_NO