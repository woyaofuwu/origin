update TF_F_PRESENT_DISCNT
   set PRESENT_SERIAL_NUMBER = :PRESENT_SERIAL_NUMBER,
       PRESENT_MONEY         = :PRESENT_MONEY * 100,
       PRESENT_DATE          = sysdate,
       UPDATE_TIME           = sysdate,
       UPDATE_STAFF_ID       = :UPDATE_STAFF_ID,
       UPDATE_DEPART_ID      = :UPDATE_DEPART_ID,
       REMARK                = :REMARK,
       RSRV_STR1             = :RSRV_STR1
 where PARTITION_ID = mod(to_number(:USER_ID), 10000)
   and USER_ID = :USER_ID
   and INST_ID = :INST_ID