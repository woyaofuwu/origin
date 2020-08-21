UPDATE TF_F_RELATION_UU SET 
END_DATE = to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') , UPDATE_TIME = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') , UPDATE_STAFF_ID = :UPDATE_STAFF_ID, UPDATE_DEPART_ID = :UPDATE_DEPART_ID, REMARK = :REMARK
WHERE 
PARTITION_ID = MOD(to_number(:USER_ID_B),10000) and USER_ID_A = to_number(:USER_ID_A) and USER_ID_B = to_number(:USER_ID_B) and RELATION_TYPE_CODE = :RELATION_TYPE_CODE and START_DATE = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')