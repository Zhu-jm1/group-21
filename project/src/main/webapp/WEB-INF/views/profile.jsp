<%@ include file="common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">My Profile</h2>
    <c:if test="${not empty message}">
        <p class="success">${message}</p>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/profile">
        <div class="form-group">
            <label>Username</label>
            <input type="text" value="${currentUser.username}" disabled>
        </div>
        <div class="form-group">
            <label>Role</label>
            <input type="text" value="${currentUser.role}" disabled>
        </div>
        <div class="form-group">
            <label for="name">Full Name</label>
            <input type="text" id="name" name="name" value="${currentUser.name}" required>
        </div>
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="${currentUser.email}">
        </div>
        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone" value="${currentUser.phone}">
        </div>
        <c:if test="${currentUser.role == 'TA'}">
            <div class="form-group">
                <label for="studentId">Student ID</label>
                <input type="text" id="studentId" name="studentId" value="${currentUser.studentId}">
            </div>
            <div class="form-group">
                <label for="skills">Skills (comma-separated)</label>
                <input type="text" id="skills" name="skills" value="${currentUser.skills}"
                       placeholder="e.g. Java, Python, C++">
            </div>
            <div class="form-group">
                <label for="reminderMethod">Reminder Method</label>
                <select id="reminderMethod" name="reminderMethod">
                    <option value="EMAIL" ${currentUser.reminderMethod == 'EMAIL' ? 'selected' : ''}>Email</option>
                    <option value="SMS" ${currentUser.reminderMethod == 'SMS' ? 'selected' : ''}>SMS</option>
                </select>
            </div>
        </c:if>
        <button type="submit" class="btn btn-primary">Update Profile</button>
    </form>
</div>
<%@ include file="common/footer.jsp" %>
