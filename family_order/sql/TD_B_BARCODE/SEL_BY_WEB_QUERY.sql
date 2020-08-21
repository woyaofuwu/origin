--IS_CACHE=N
SELECT BARCODE_ID,BARCODE_TYPE,BARCODE_NAME,BARCODE_EXPLAIN ,T.FILE_ID,F.FILE_NAME,t.URL ,IN_MODE_CODE ,EPARCHY_CODE,STATE ,
TO_CHAR(T.UPDATE_TIME,'yyyy-mm-dd hh24:Mi:ss') UPDATE_TIME,TO_CHAR(T.START_DATE,'yyyy-mm-dd') START_DATE,
TO_CHAR(T.END_DATE,'yyyy-mm-dd') END_DATE,
T.UPDATE_STAFF_ID,T.UPDATE_DEPART_ID,T.REMARK
  FROM TD_B_BARCODE T left join WD_F_FTPFILE f
 on t.file_id = f.file_id
WHERE 1=1
AND (:BARCODE_ID IS NULL OR T.BARCODE_ID =:BARCODE_ID)
AND (:BARCODE_TYPE IS NULL OR T.BARCODE_TYPE =:BARCODE_TYPE)
AND (:STATE IS NULL OR T.STATE =:STATE)
AND (:UPDATE_STAFF_ID IS NULL OR T.UPDATE_STAFF_ID=:UPDATE_STAFF_ID)
AND (:START_DATE IS NULL OR T.UPDATE_TIME >=TO_DATE(:START_DATE,'yyyy-mm-dd  hh24:Mi:ss') )
AND (:END_DATE IS NULL OR T.UPDATE_TIME <=TO_DATE(:END_DATE,'yyyy-mm-dd  hh24:Mi:ss') )
AND (:BARCODE_NAME IS NULL OR T.BARCODE_NAME like :BARCODE_NAME)
AND (:EPARCHY_CODE IS NULL OR T.EPARCHY_CODE = :EPARCHY_CODE)
ORDER BY T.UPDATE_TIME desc