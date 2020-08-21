--IS_CACHE=Y
SELECT A.EPARCHY_CODE EPARCHY_CODE,
       MOFFICE_ID,
       SWITCH_ID,
       SERIALNUMBER_S,
       SERIALNUMBER_E,
       IMSI_S,
       IMSI_E,
       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       A.UPDATE_STAFF_ID UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID UPDATE_DEPART_ID
  FROM TD_M_MOFFICE A,TD_M_RES_COMMPARA B
 WHERE A.EPARCHY_CODE = :EPARCHY_CODE
   AND A.EPARCHY_CODE=B.EPARCHY_CODE
   AND B.PARA_ATTR='36'
   AND B.PARA_CODE1=SUBSTR(:SIM_CARD_NO,1,1)
   AND B.PARA_VALUE1||SUBSTR(:SIM_CARD_NO,2,1)
       BETWEEN SUBSTR(SERIALNUMBER_S, 2, 3) 
       AND     SUBSTR(SERIALNUMBER_E, 2, 3)
   AND SUBSTR(MOFFICE_ID, 2, LENGTH(MOFFICE_ID) - 1) NOT IN
       ('130003', '130004', '130009', '150002')
   AND ROWNUM < 2