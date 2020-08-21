update TD_B_BARCODE t
   set 
       t.IN_MODE_CODE     = :IN_MODE_CODE,
       t.BARCODE_NAME     = :BARCODE_NAME,
       t.FILE_ID          = :FILE_ID,
       t.BARCODE_TYPE          = :BARCODE_TYPE,
       t.EPARCHY_CODE     = :EPARCHY_CODE,
       t.BARCODE_EXPLAIN  = :BARCODE_EXPLAIN,
       t.END_DATE         = TO_DATE(:END_DATE,'yyyy-mm-dd'),
       t.START_DATE       = TO_DATE(:START_DATE,'yyyy-mm-dd'),
       t.UPDATE_TIME      = TO_DATE(:UPDATE_TIME,'yyyy-mm-dd hh24:Mi:ss'),
       t.UPDATE_DEPART_ID = :UPDATE_DEPART_ID,
       t.UPDATE_STAFF_ID  = :UPDATE_STAFF_ID
 where t.BARCODE_ID = :BARCODE_ID