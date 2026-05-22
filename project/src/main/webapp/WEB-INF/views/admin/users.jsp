<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">User Management</h2>

    <h3 style="margin-bottom:8px;">Create New User</h3>
    <form method="post" action="${pageContext.request.contextPath}/admin" style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:20px;">
        <input type="hidden" name="action" value="createUser">
        <input type="text" name="username" placeholder="Username" required style="padding:6px;">
        <input type="password" name="password" placeholder="Password" required style="padding:6px;">
        <input type="text" name="name" placeholder="Full Name" required style="padding:6px;">
        <input type="email" name="email" placeholder="Email" style="padding:6px;">
        <select name="role" style="padding:6px;">
            <option value="TA">TA</option>
            <option value="MO">MO</option>
            <option value="ADMIN">ADMIN</option>
        </select>
        <button type="submit" class="btn btn-success">Create</button>
    </form>

    <table>
        <tr>
            <th>ID</th><th>Username</th><th>Name</th><th>Role</th><th>Email</th><th>Phone</th><th>Actions</th>
        </tr>
        <c:forEach var="u" items="${users}">
            <tr>
                <td>${u.id}</td>
                <td>${u.username}</td>
                <td>${u.name}</td>
                <td>${u.role}</td>
                <td>${u.email}</td>
                <td>${u.phone}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin?action=editUser&id=${u.id}" class="btn btn-primary">Edit</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin" style="display:inline;">
                        <input type="hidden" name="action" value="deleteUser">
                        <input type="hidden" name="id" value="${u.id}">
                        <button type="submit" class="btn btn-danger" onclick="return confirm('Delete this user?')">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
<%@ include file="../common/footer.jsp" %>
