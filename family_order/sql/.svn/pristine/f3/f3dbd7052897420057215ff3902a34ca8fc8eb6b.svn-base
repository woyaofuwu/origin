UPDATE TF_F_RELATION_AA
   SET END_DATE         = :END_DATE,
       ACT_TAG           = :ACT_TAG,
       UPDATE_TIME      = to_date(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),
       UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       UPDATE_DEPART_ID = :UPDATE_DEPART_ID,
       REMARK           = :REMARK
 WHERE PARTITION_ID = MOD(to_number(:ACCT_ID_B), 10000)
   and ACCT_ID_A = to_number(:ACCT_ID_A)
   and ACCT_ID_B = to_number(:ACCT_ID_B)
   and RELATION_TYPE_CODE = :RELATION_TYPE_CODE
   and START_DATE = :START_DATE