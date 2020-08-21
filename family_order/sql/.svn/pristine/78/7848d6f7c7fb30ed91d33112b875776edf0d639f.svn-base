UPDATE TF_F_USER_VPN_MEB SET 
REMOVE_TAG = :REMOVE_TAG, REMOVE_DATE = to_date(:REMOVE_DATE,'yyyy-mm-dd hh24:mi:ss') , UPDATE_TIME = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') , UPDATE_STAFF_ID = :UPDATE_STAFF_ID, UPDATE_DEPART_ID = :UPDATE_DEPART_ID, REMARK = :REMARK
WHERE 
PARTITION_ID = MOD(to_number(:USER_ID),10000) and USER_ID = to_number(:USER_ID) and USER_ID_A = to_number(:USER_ID_A) and OPEN_DATE = to_date(:OPEN_DATE,'yyyy-mm-dd hh24:mi:ss')