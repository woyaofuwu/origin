SELECT SUBSTR(A.INFO_CONTENT, INSTR(A.INFO_CONTENT, 'CRM订单号:') + LENGTH('CRM订单号:'),
              INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, 'CRM订单号:')) -
              (INSTR(A.INFO_CONTENT, 'CRM订单号:') + LENGTH('CRM订单号:'))) IBSYSID,
       SUBSTR(A.INFO_CONTENT, INSTR(A.INFO_CONTENT, '集团编码:') + LENGTH('集团编码:'), 
              INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '集团编码:')) - 
              (INSTR(A.INFO_CONTENT, '集团编码:') + LENGTH('集团编码:'))) GROUP_ID,
       SUBSTR(A.INFO_CONTENT,
              INSTR(A.INFO_CONTENT, '集团名称:') + LENGTH('集团名称:'),
              INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '集团名称:')) -
              (INSTR(A.INFO_CONTENT, '集团名称:') + LENGTH('集团名称:'))) CUST_NAME,
       SUBSTR(A.INFO_CONTENT,
              INSTR(A.INFO_CONTENT, '产品:') + LENGTH('产品:'),
              INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '产品:')) -
              (INSTR(A.INFO_CONTENT, '产品:') + LENGTH('产品:'))) PRODUCT_NAME,
       SUBSTR(A.INFO_CONTENT,
              INSTR(A.INFO_CONTENT, '业务类型:') + LENGTH('业务类型:'),
              INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '业务类型:')) -
              (INSTR(A.INFO_CONTENT, '业务类型:') + LENGTH('业务类型:'))) OPER_TYPE,
       SUBSTR(A.INFO_CONTENT,
              INSTR(A.INFO_CONTENT, '当前节点:') + LENGTH('当前节点:'),
              INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '当前节点:')) -
              (INSTR(A.INFO_CONTENT, '当前节点:') + LENGTH('当前节点:'))) NODE_NAME,
       SUBSTR(A.INFO_CONTENT,
              INSTR(A.INFO_CONTENT, '业务创建人:') + LENGTH('业务创建人:'),
              INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '业务创建人:')) -
              (INSTR(A.INFO_CONTENT, '业务创建人:') + LENGTH('业务创建人:'))) STAFF_ID,
       A.INFO_ID,
       A.INFO_SIGN,
       A.INFO_TOPIC,
       A.INFO_CHILD_TYPE,
       A.INFO_TYPE,
       A.INFO_STATUS,
       A.INFO_LEVEL,
       A.INFO_URL,
       A.INFO_CONTENT,
       A.INFO_AUTH,
       TO_CHAR(A.INFO_SEND_TIME, 'YYYY-MM-DD HH24:MI:SS') INFO_SEND_TIME,
       TO_CHAR(A.END_TIME, 'YYYY-MM-DD HH24:MI:SS') END_TIME,
       B.INST_ID,
       B.RECE_OBJ,
       B.RECE_OBJ_TYPE,
       B.INST_STATUS,
       TO_CHAR(B.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,
       TO_CHAR(B.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE
  FROM TF_F_INFO A, TF_F_INFO_INSTANCE B
 WHERE A.INFO_ID = B.INFO_ID
   AND A.INFO_TOPIC LIKE '%' || :INFO_TOPIC || '%'
   AND A.INFO_CONTENT LIKE '%CRM订单号:' || :IBSYSID || '%'
   AND A.INFO_CONTENT LIKE '%集团名称:' || :CUST_NAME || '%'
   AND A.INFO_CONTENT LIKE '%集团编码:' || :GROUP_ID || '%'
   AND A.INFO_TYPE = :INFO_TYPE
   AND A.INFO_CHILD_TYPE = :INFO_CHILD_TYPE
   AND A.INFO_STATUS = :INFO_STATUS
   AND B.START_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD')
   AND B.START_DATE < TO_DATE(:END_DATE, 'YYYY-MM-DD') + 1
   AND B.RECE_OBJ_TYPE = :RECE_OBJ_TYPE
   AND B.RECE_OBJ = :STAFF_ID
   AND B.INST_ID = :INST_ID
 ORDER BY B.INST_ID DESC
