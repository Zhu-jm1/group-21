<%@ include file="common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">My Profile</h2>
    <c:if test="${not empty message}">
        <p class="success">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/profile"
          enctype="multipart/form-data">
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
                <label for="skills">Skills (comma-separated)</label>
                <input type="text" id="skills" name="skills" value="${currentUser.skills}"
                       placeholder="e.g. Java, Python, C++">
            </div>
            <div class="form-group">
                <label for="cvFile">Resume (PDF, DOC, or DOCX, max 10MB)</label>
                <input type="file" id="cvFile" name="cvFile"
                       accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document">
                <c:if test="${not empty currentUser.cvPath}">
                    <p style="margin-top:8px;font-size:14px;">
                        Current file on record.
                        <a class="btn btn-primary" style="margin-left:8px;padding:4px 10px;font-size:13px;"
                           href="${pageContext.request.contextPath}/profile?action=downloadCv">Download resume</a>
                    </p>
                </c:if>
            </div>
        </c:if>
        <button type="submit" class="btn btn-primary">Update Profile</button>
    </form>
</div>
<%@ include file="common/footer.jsp" %>
