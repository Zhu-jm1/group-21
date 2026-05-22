<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">${job.title}</h2>
    <p><strong>Module:</strong> ${job.moduleName}</p>
    <p><strong>Type:</strong> ${job.type}</p>
    <p><strong>Description:</strong> ${job.description}</p>
    <p><strong>Required Skills:</strong> ${job.requiredSkills}</p>
    <p><strong>Status:</strong>
        <span class="badge badge-${job.status == 'OPEN' ? 'open' : 'closed'}">${job.status}</span>
    </p>
    <p><strong>Posted:</strong> ${job.createdDate}</p>
    <c:if test="${not empty job.deadline}">
        <p><strong>Application Deadline:</strong> ${job.deadline}</p>
    </c:if>
    <c:if test="${job.classHours > 0}">
        <p><strong>Class Hours:</strong> ${job.classHours}</p>
    </c:if>

    <c:if test="${currentUser.role == 'TA' && job.status == 'OPEN'}">
        <hr style="margin:16px 0;">
        <h3>Apply for this Job</h3>
        <form method="post" action="${pageContext.request.contextPath}/applications">
            <input type="hidden" name="action" value="apply">
            <input type="hidden" name="jobId" value="${job.id}">
            <div class="form-group">
                <label for="note">Cover Note</label>
                <textarea id="note" name="note" rows="3" placeholder="Why are you a good fit?"></textarea>
            </div>
            <button type="submit" class="btn btn-success">Submit Application</button>
        </form>
    </c:if>

    <c:if test="${currentUser.role == 'MO'}">
        <hr style="margin:16px 0;">
        <a href="${pageContext.request.contextPath}/applications?action=review&jobId=${job.id}" class="btn btn-primary">Review Applications</a>
    </c:if>
</div>
<%@ include file="../common/footer.jsp" %>
