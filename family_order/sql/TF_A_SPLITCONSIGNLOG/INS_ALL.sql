INSERT INTO tf_a_splitconsignlog(serial_number,user_id,acct_id,pay_name,acyc_id,bcyc_id,recv_acyc_id,eparchy_code,city_code,city_name,bank_code,bank,bank_acct_no,bill_id,intffee01,intffee02,intffee03,intffee04,intffee05,intffee06,intffee07,intffee08,intffee09,intffee10,intffee11,intffee12,intffee13,intffee14,intffee15,intffee16,intffee17,intffee18,rsrv_info1,rsrv_info2,rsrv_inf03,rsrv_info4)
 VALUES(:SERIAL_NUMBER,TO_NUMBER(:USER_ID),TO_NUMBER(:ACCT_ID),:PAY_NAME,:ACYC_ID,:BCYC_ID,:RECV_ACYC_ID,:EPARCHY_CODE,:CITY_CODE,:CITY_NAME,:BANK_CODE,:BANK,:BANK_ACCT_NO,TO_NUMBER(:BILL_ID),TO_NUMBER(:INTFFEE01),TO_NUMBER(:INTFFEE02),TO_NUMBER(:INTFFEE03),TO_NUMBER(:INTFFEE04),TO_NUMBER(:INTFFEE05),TO_NUMBER(:INTFFEE06),TO_NUMBER(:INTFFEE07),TO_NUMBER(:INTFFEE08),TO_NUMBER(:INTFFEE09),TO_NUMBER(:INTFFEE10),TO_NUMBER(:INTFFEE11),TO_NUMBER(:INTFFEE12),TO_NUMBER(:INTFFEE13),TO_NUMBER(:INTFFEE14),TO_NUMBER(:INTFFEE15),TO_NUMBER(:INTFFEE16),TO_NUMBER(:INTFFEE17),TO_NUMBER(:INTFFEE18),:RSRV_INFO1,:RSRV_INFO2,:RSRV_INF03,:RSRV_INFO4)