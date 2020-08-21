UPDATE td_a_recv_bankaccountno
   SET eparchy_code      = :EPARCHY_CODE,
       city_code         = :CITY_CODE,
       bank_code         = :BANK_CODE,
       recv_city_code    = :RECV_CITY_CODE,
       recv_bank_code    = :RECV_BANK_CODE,
       recv_bank_acct_no = :RECV_BANK_ACCT_NO,
       recv_name         = :RECV_NAME,
       remark            = :REMARK,
       trust_type        = :TRUST_TYPE,
       update_time       =  SYSDATE
       update_staff_id   = :UPDATE_STAFF_ID,
       update_depart_id  = :UPDATE_DEPART_ID
 WHERE eparchy_code = :EPARCHY_CODE
   AND bank_code = :BANK_CODE