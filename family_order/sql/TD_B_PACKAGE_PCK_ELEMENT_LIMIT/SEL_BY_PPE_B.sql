--IS_CACHE=Y
SELECT d.PACKAGE_ID_A AS PACKAGE_ID_A, d.ELEMENT_TYPE_CODE_A AS ELEMENT_TYPE_CODE_A,
       d.ELEMENT_ID_A AS ELEMENT_ID_A, d.PACKAGE_ID_B AS PACKAGE_ID_B,
       d.ELEMENT_TYPE_CODE_B AS ELEMENT_TYPE_CODE_B, d.ELEMENT_ID_B AS ELEMENT_ID_B,
       d.LIMIT_TAG AS LIMIT_TAG, TO_CHAR(d.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
       TO_CHAR(d.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE,
       TO_CHAR(d.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_TIME,
       d.UPDATE_STAFF_ID AS UPDATE_STAFF_ID, d.UPDATE_DEPART_ID AS UPDATE_DEPART_ID,
       d.REMARK AS REMARK
FROM   TD_B_PACKAGE_PCK_ELEMENT_LIMIT d
where  d.start_date <= sysdate
  and  d.end_date >= sysdate
  and  d.element_type_code_a in('S','D')
  and  (d.eparchy_code = :EPARCHY_CODE or d.eparchy_code = 'ZZZZ')
  and  d.limit_tag = :LIMIG_TAG
  and  d.PACKAGE_ID_B = :PACKAGE_ID
  and  d.element_type_code_b = :ELEMENT_TYPE_ID_B
  and  d.element_id_b = :ELEMENT_ID_B
union all
SELECT d.PACKAGE_ID AS PACKAGE_ID_A, d.ELEMENT_TYPE_CODE_A AS ELEMENT_TYPE_CODE_A,
       d.ELEMENT_ID_A AS ELEMENT_ID_A, d.PACKAGE_ID AS PACKAGE_ID_B,
       d.ELEMENT_TYPE_CODE_B AS ELEMENT_TYPE_CODE_B, d.ELEMENT_ID_B AS ELEMENT_ID_B,
       d.LIMIT_TAG AS LIMIT_TAG, TO_CHAR(d.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
       TO_CHAR(d.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE,
       TO_CHAR(d.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_TIME,
       d.UPDATE_STAFF_ID AS UPDATE_STAFF_ID, d.UPDATE_DEPART_ID AS UPDATE_DEPART_ID,
       d.REMARK AS REMARK
FROM   TD_B_PACKAGE_ELEMENT_LIMIT d
where  d.start_date <= sysdate
  and  d.end_date >= sysdate
  and  d.element_type_code_a in('S','D')
  and  (d.eparchy_code = :EPARCHY_CODE or d.eparchy_code = 'ZZZZ')
  and  d.limit_tag = :LIMIG_TAG
  and  d.package_id = :PACKAGE_ID
  and  d.element_type_code_b = :ELEMENT_TYPE_ID_B
  and  d.element_id_b = :ELEMENT_ID_B