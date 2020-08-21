SELECT DISTINCT PARTITION_ID,
                USER_ID,
                USER_ID_A,
                PRODUCT_ID,
                PACKAGE_ID,
                DISCNT_CODE,
                SPEC_TAG,
                RELATION_TYPE_CODE,
                INST_ID,
                CAMPN_ID,
                START_DATE,
                END_DATE
  FROM (SELECT A.PARTITION_ID,
               TO_CHAR(A.USER_ID) USER_ID,
               TO_CHAR(A.USER_ID_A) USER_ID_A,
               TO_CHAR(A.PRODUCT_ID) PRODUCT_ID,
               TO_CHAR(A.PACKAGE_ID) PACKAGE_ID,
               TO_CHAR(A.DISCNT_CODE) DISCNT_CODE,
               A.SPEC_TAG,
               A.RELATION_TYPE_CODE,
               TO_CHAR(A.INST_ID) INST_ID,
               TO_CHAR(A.CAMPN_ID) CAMPN_ID,
               TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,
               TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,
               TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,
               A.UPDATE_STAFF_ID,
               A.UPDATE_DEPART_ID,
               A.REMARK,
               TO_CHAR(A.RSRV_NUM1) RSRV_NUM1,
               TO_CHAR(A.RSRV_NUM2) RSRV_NUM2,
               TO_CHAR(A.RSRV_NUM3) RSRV_NUM3,
               TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,
               TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,
               A.RSRV_STR1,
               A.RSRV_STR2,
               A.RSRV_STR3,
               A.RSRV_STR4,
               A.RSRV_STR5,
               TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,
               TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,
               TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,
               A.RSRV_TAG1,
               A.RSRV_TAG2,
               A.RSRV_TAG3
          FROM TF_F_USER_DISCNT A, TD_S_COMMPARA C
         WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
           AND A.USER_ID = :USER_ID
           AND TRUNC(LAST_DAY(SYSDATE) + 1) BETWEEN A.START_DATE AND
               A.END_DATE
           AND A.DISCNT_CODE = TO_NUMBER(C.PARA_CODE1)
           AND C.SUBSYS_CODE = 'CSM'
           AND C.PARAM_ATTR = :PARAM_ATTR
           AND C.PARAM_CODE = :PARAM_CODE
           AND (C.EPARCHY_CODE = :EPARCHY_CODE OR C.EPARCHY_CODE = 'ZZZZ')
           AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE
        union all
        SELECT A.PARTITION_ID,
               TO_CHAR(A.USER_ID) USER_ID,
               TO_CHAR(A.USER_ID_A) USER_ID_A,
               TO_CHAR(A.PRODUCT_ID) PRODUCT_ID,
               TO_CHAR(A.PACKAGE_ID) PACKAGE_ID,
               TO_CHAR(A.DISCNT_CODE) DISCNT_CODE,
               A.SPEC_TAG,
               A.RELATION_TYPE_CODE,
               TO_CHAR(A.INST_ID) INST_ID,
               TO_CHAR(A.CAMPN_ID) CAMPN_ID,
               TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,
               TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,
               TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,
               A.UPDATE_STAFF_ID,
               A.UPDATE_DEPART_ID,
               A.REMARK,
               TO_CHAR(A.RSRV_NUM1) RSRV_NUM1,
               TO_CHAR(A.RSRV_NUM2) RSRV_NUM2,
               TO_CHAR(A.RSRV_NUM3) RSRV_NUM3,
               TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,
               TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,
               A.RSRV_STR1,
               A.RSRV_STR2,
               A.RSRV_STR3,
               A.RSRV_STR4,
               A.RSRV_STR5,
               TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,
               TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,
               TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,
               A.RSRV_TAG1,
               A.RSRV_TAG2,
               A.RSRV_TAG3
          FROM TF_F_USER_DISCNT A
         WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
           AND A.USER_ID = :USER_ID
           AND TRUNC(LAST_DAY(SYSDATE) + 1) BETWEEN A.START_DATE AND
               A.END_DATE
           AND A.PRODUCT_ID = 705001
           AND A.PACKAGE_ID = 70500102) F
