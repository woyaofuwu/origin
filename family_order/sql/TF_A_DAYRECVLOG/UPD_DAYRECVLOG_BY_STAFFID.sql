UPDATE tf_a_dayrecvlog
   SET recv_fee_num=recv_fee_num + TO_NUMBER(:RECV_FEE_NUM),cancel_num=cancel_num + TO_NUMBER(:CANCEL_NUM),recv_fee_cash=recv_fee_cash + TO_NUMBER(:RECV_FEE_CASH),recv_fee_card=recv_fee_card + TO_NUMBER(:RECV_FEE_CARD),recv_fee_check=recv_fee_check + TO_NUMBER(:RECV_FEE_CHECK),recv_fee_xfk=recv_fee_xfk + TO_NUMBER(:RECV_FEE_XFK),recv_fee_other=recv_fee_other + TO_NUMBER(:RECV_FEE_OTHER),cancel_fee=cancel_fee + TO_NUMBER(:CANCEL_FEE),invoice_num= invoice_num + TO_NUMBER(:INVOICE_NUM),imprest_num=imprest_num + TO_NUMBER(:IMPREST_NUM)  
 WHERE eparchy_code=:EPARCHY_CODE
   AND staff_id=:STAFF_ID
   AND recv_day=:RECV_DAY