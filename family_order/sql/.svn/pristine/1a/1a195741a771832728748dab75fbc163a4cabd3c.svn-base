--IS_CACHE=Y
select TO_CHAR(sysdate, 'yyyymmddhh24miss') SYSDT,
       TO_CHAR(NVL(max(START_DATE), TO_DATE('19900101', 'yyyymmdd')), 'yyyymmddhh24miss') STARTDT,
       TO_CHAR(NVL(min(END_DATE), TO_DATE('20201231', 'yyyymmdd')), 'yyyymmddhh24miss') ENDDT,
       TO_CHAR(NVL(max(START_TIME), TO_DATE('000000', 'hh24miss')), 'hh24miss') STARTTM,
       TO_CHAR(NVL(min(END_TIME), TO_DATE('235959', 'hh24miss')), 'hh24miss') ENDTM
 from  TF_M_STAFFDATETIME
 where (SYS_TAG = '0' or SYS_TAG ='1')
       and STAFF_ID = :STAFF_ID