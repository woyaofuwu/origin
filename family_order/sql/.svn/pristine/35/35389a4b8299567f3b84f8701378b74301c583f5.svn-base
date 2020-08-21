insert into TF_B_EOP_BUSI_CHECK_RECORD
  (CHECK_ID,
   IBSYSID,
   CHECK_TYPE,
   BUSI_TYPE,
   CHECK_STAFF_ID,
   CHECK_TIME,
   CHECK_RESULT,
   INSERT_TIME,
   REMARK)
values
  (:CHECK_ID,
   :IBSYSID,
   :CHECK_TYPE,
   :BUSI_TYPE,
   :CHECK_STAFF_ID,
   to_date(:CHECK_TIME, 'yyyy-mm-dd'),
   :CHECK_RESULT,
   to_date(:INSERT_TIME, 'yyyy-mm-dd'),
   :REMARK)