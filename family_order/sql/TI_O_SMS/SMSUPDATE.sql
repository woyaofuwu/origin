UPDATE ti_o_sms SET deal_state='3'
 WHERE recv_object_type=:RECV_OBJECT_TYPE
   AND recv_object=:RECV_OBJECT
   AND sms_type_code=:SMS_TYPE_CODE
   AND deal_state=:DEAL_STATE