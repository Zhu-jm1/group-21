<%@ include file="common/header.jsp" %>
<div class="card" style="max-width:400px;margin:60px auto;">
    <h2 style="margin-bottom:16px;">Login</h2>
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>
    <c:if test="${param.registered == 'true'}">
        <p class="success">Registration successful. Please login.</p>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit" class="btn btn-primary">Login</button>
        <a href="${pageContext.request.contextPath}/register" style="margin-left:12px;">Register</a>
    </form>
</div>
<%@ include file="common/footer.jsp" %>
