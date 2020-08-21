SELECT  A.State,A.BUSIFORM_ID,
        B.BPM_TEMPLET_ID,
        A.BUSIFORM_NODE_ID,
        to_char(A.CREATE_DATE,'yyyy-mm-dd hh24:mi:ss') as CREATE_DATE,
        A.FLOW_EXPECT_TIME,
        A.NODE_ID,
        A.DEAL_STAFF_ID do_staff_id,
        B.BUSIFORM_ID     
 FROM  TF_B_EWE_NODE A,TF_B_EWE B
 where A.BUSIFORM_ID = B.BUSIFORM_ID
 and   B.BI_SN = :IBSYSID
 and   B.BPM_TEMPLET_ID = :BPM_TEMPLET_ID