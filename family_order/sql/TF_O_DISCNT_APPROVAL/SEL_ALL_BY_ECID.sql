 SELECT OPR_SEQ,
  EC_ID,
  DISCNT_RATE,
  CARD_BIND,
  PROV_DOC,
  NVL(APPROVAL_RSLT,9) APPROVAL_RSLT,
  APPROVAL_NO,
  APPROVAL_COMM,
  APPROVAL_DOC,
  CREATE_TIME,
  CREATE_STAFF_ID,
  UPDATE_TIME,
  APPLICANT,
  APPLICANT_PHONE,
  APPLY_REASON,
  APPLY_DATE,
  NVL(RSRV_STR1,2) RSRV_STR1,
  NVL(RSRV_STR2,1) RSRV_STR2,
  RSRV_STR3,
  RSRV_STR4,
  RSRV_STR5
  FROM TF_O_DISCNT_APPROVAL
  WHERE 1=1
  AND EC_ID = :EC_ID 
  AND OPR_SEQ = :OPR_SEQ
  AND RSRV_STR2 = :RSRV_STR2
  AND CREATE_TIME  >= to_date(:START_DATE,'YYYY-MM-DD')
  AND CREATE_TIME <= to_date(:END_DATE,'YYYY-MM-DD')