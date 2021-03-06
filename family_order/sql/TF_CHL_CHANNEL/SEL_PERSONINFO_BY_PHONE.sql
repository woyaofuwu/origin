SELECT C.CHNL_ID, C.CHNL_CODE, C.CHNL_NAME, C.STATE
  FROM TF_CHL_CHANNEL C, TF_CHL_PERSON_ARCHIVE P, TF_CHL_ARCHIVE A
 WHERE 1 = 1
   AND P.SERIAL_NUMBER = :SERIAL_NUMBER
   AND C.STATE = '0'
   AND P.DEPART_ID = C.CHNL_ID
   AND A.CHNL_ID = C.CHNL_ID
   AND A.LINKMAN_PHONE IS NOT NULL
   AND P.STAFF_ID LIKE 'AGT%'