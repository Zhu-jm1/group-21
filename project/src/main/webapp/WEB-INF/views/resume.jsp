<%@ include file="common/header.jsp" %>
<div class="card" style="max-width:600px;">
    <h2 style="margin-bottom:16px;">My Resume / CV</h2>
    <c:if test="${not empty message}">
        <p class="success">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>

    <c:if test="${not empty currentUser.cvPath}">
        <p>Current resume: <strong>${currentUser.cvPath}</strong>
            <a href="${pageContext.request.contextPath}/resume?action=download" class="btn btn-primary">Download</a>
        </p>
    </c:if>
    <c:if test="${empty currentUser.cvPath}">
        <p>No resume uploaded yet.</p>
    </c:if>

    <hr style="margin:16px 0;">
    <h3>Upload Resume</h3>
    <form method="post" action="${pageContext.request.contextPath}/resume" enctype="multipart/form-data">
        <div class="form-group">
            <label for="resume">Select file (PDF - max 5MB)</label>
            <input type="file" id="resume" name="resume" accept=".pdf,.doc,.docx" required>
        </div>
        <button type="submit" class="btn btn-success">Upload</button>
    </form>
</div>
<%@ include file="common/footer.jsp" %>
