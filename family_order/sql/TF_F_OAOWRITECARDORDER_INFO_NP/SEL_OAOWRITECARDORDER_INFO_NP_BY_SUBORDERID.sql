select * from TF_F_OAOWRITECARDORDER_INFO_NP
    where 1=1
    AND STATE=:STATE
    AND ORDER_ID=:ORDER_ID
    AND SUBORDER_ID=:SUBORDER_ID
    AND BILL_ID=:BILL_ID
    AND EXEC_STATE=:EXEC_STATE