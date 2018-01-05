<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>

    <title>秒杀列表页</title>
    <%@include file="common/header.jsp" %>
</head>
<body>

<div class="container">
    <div class="panel panel-default">
        <div class="page-header text-center">
            <h2>秒杀列表</h2>
        </div>

        <div class="panel-body">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>名称</th>
                        <th>库存</th>
                        <th>开始时间</th>
                        <th>结束时间</th>
                        <th>创建时间</th>
                        <th>详情页</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${list}" var="sk">
                        <tr>
                            <td>${sk.productName}</td>
                            <td>${sk.number}</td>
                            <td><fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                            <td><fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                            <td><fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                            <td><a class="btn btn-info" href="/seckill/${sk.seckillId}/detail" target="_blank">秒杀详情</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@include file="common/footer.jsp"%>
</body>
</html>
