<%@ include file="../common/header.jsp" %>
<div class="card" style="max-width:500px;">
    <h2 style="margin-bottom:16px;">Edit User</h2>
    <form method="post" action="${pageContext.request.contextPath}/admin">
        <input type="hidden" name="action" value="updateUser">
        <input type="hidden" name="id" value="${editUser.id}">
        <div class="form-group">
            <label>Username</label>
            <input type="text" value="${editUser.username}" disabled>
        </div>
        <div class="form-group">
            <label for="name">Full Name</label>
            <input type="text" id="name" name="name" value="${editUser.name}" required>
        </div>
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="${editUser.email}">
        </div>
        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone" value="${editUser.phone}">
        </div>
        <div class="form-group">
            <label for="role">Role</label>
            <select id="role" name="role">
                <option value="TA" ${editUser.role == 'TA' ? 'selected' : ''}>TA</option>
                <option value="MO" ${editUser.role == 'MO' ? 'selected' : ''}>MO</option>
                <option value="ADMIN" ${editUser.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primary">Save Changes</button>
        <a href="${pageContext.request.contextPath}/admin?action=users" style="margin-left:12px;">Cancel</a>
    </form>
</div>
<%@ include file="../common/footer.jsp" %>
