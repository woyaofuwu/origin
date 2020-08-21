--IS_CACHE=Y
SELECT NVL(b.data_code,'DATA'||a.data_no) data_code,b.data_name
  FROM (SELECT ROWNUM data_no
          FROM user_objects
		 WHERE ROWNUM <= :DATA_NO
       ) a,
       (SELECT data_no,data_code,data_name
	      FROM td_b_batchdatatype
		 WHERE batch_oper_type = :BATCH_OPER_TYPE
	   ) b
 WHERE a.data_no = b.data_no(+)
ORDER BY a.data_no