SELECT INST_ID,EC_ID,PROD_ID,BIZ_CODE,PROD_NAME,SERV_CODE,OP_TYPE,OP_CODE,SERIAL_NUMBER,EFFT_TIME,DATA_TYPE,
SYNC_FLAG,REMARK,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 FROM TI_B_WHITECONFIRM T
WHERE T.EC_ID = :EC_ID AND T.SERV_CODE = :SERV_CODE AND T.SERIAL_NUMBER = :SERIAL_NUMBER 
