--IS_CACHE=Y
select SUBSYS_CODE,
       PARAM_ATTR,
       PARAM_CODE,
       PARAM_NAME,
       PARA_CODE1,
       PARA_CODE2,
       PARA_CODE3,
       PARA_CODE4,
       PARA_CODE5,
       PARA_CODE6,
       PARA_CODE7,
       PARA_CODE8,
       PARA_CODE9,
       PARA_CODE10,
       PARA_CODE11,
       PARA_CODE12,
       PARA_CODE13,
       PARA_CODE14,
       PARA_CODE15,
       PARA_CODE16,
       PARA_CODE17,
       PARA_CODE18,
       PARA_CODE19,
       PARA_CODE20,
       PARA_CODE21,
       PARA_CODE22,
       PARA_CODE23,
       PARA_CODE24,
       PARA_CODE25,
       TO_CHAR(PARA_CODE26, 'yyyy-mm-dd hh24:mi:ss') PARA_CODE26,
       TO_CHAR(PARA_CODE27, 'yyyy-mm-dd hh24:mi:ss') PARA_CODE27,
       TO_CHAR(PARA_CODE28, 'yyyy-mm-dd hh24:mi:ss') PARA_CODE28,
       TO_CHAR(PARA_CODE29, 'yyyy-mm-dd hh24:mi:ss') PARA_CODE29,
       TO_CHAR(PARA_CODE30, 'yyyy-mm-dd hh24:mi:ss') PARA_CODE30,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       EPARCHY_CODE,
       REMARK,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
  from TD_S_COMMPARA A
 where A.SUBSYS_CODE = :SUBSYS_CODE
   and A.PARAM_ATTR = :PARAM_ATTR
   and A.PARAM_CODE = :PARAM_CODE
   and A.PARA_CODE1 = :PARA_CODE1
   and A.PARA_CODE2 = :PARA_CODE2
   and (A.EPARCHY_CODE = :EPARCHY_CODE or A.EPARCHY_CODE = 'ZZZZ')
   and sysdate between A.START_DATE and A.END_DATE