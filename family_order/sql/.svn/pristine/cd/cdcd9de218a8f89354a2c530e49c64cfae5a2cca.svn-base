SELECT SUB_IBSYSID, RECORD_NUM
  FROM TF_BH_EOP_ATTR A
 WHERE A.ATTR_CODE = :ATTR_CODE
   AND A.ATTR_VALUE = :ATTR_VALUE
   AND A.SUB_IBSYSID = (SELECT MAX(SUB_IBSYSID)
                          FROM TF_BH_EOP_ATTR T
                         WHERE T.ATTR_CODE = :ATTR_CODE
                           AND T.ATTR_VALUE = :ATTR_VALUE
                           AND T.NODE_ID = :NODE_ID)
                           