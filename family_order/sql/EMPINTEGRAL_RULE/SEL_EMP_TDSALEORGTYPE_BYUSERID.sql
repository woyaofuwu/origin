--IS_CACHE=N
SELECT T.orgtype 部门类型
FROM IM.ORGANIZATION@DB_ZDGL T
WHERE T.ORGID = :SALE_DEPART_ID