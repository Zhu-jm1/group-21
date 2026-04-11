<%@ include file="common/header.jsp" %>
<div class="card" style="max-width:400px;margin:60px auto;">
    <h2 style="margin-bottom:16px;">Register</h2>
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/register">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-group">
            <label for="name">Full Name</label>
            <input type="text" id="name" name="name" required>
        </div>
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div class="form-group">
            <label for="role">Role</label>
            <select id="role" name="role" required>
                <option value="TA">Teaching Assistant (TA)</option>
                <option value="MO">Module Organiser (MO)</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primary">Register</button>
        <a href="${pageContext.request.contextPath}/login" style="margin-left:12px;">Back to Login</a>
    </form>
</div>
<%@ include file="common/footer.jsp" %>
