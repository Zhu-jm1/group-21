<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">Review Applications</h2>
    <c:if test="${empty applications}">
        <p>No applications for this job yet.</p>
    </c:if>
    <c:if test="${not empty applications}">
        <table>
            <tr>
                <th>Applicant</th>
                <th>Skills</th>
                <th>Applied Date</th>
                <th>Note</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            <c:forEach var="app" items="${applications}">
                <tr>
                    <td>${applicantMap[app.applicantId].name}</td>
                    <td>${applicantMap[app.applicantId].skills}</td>
                    <td>${app.applyDate}</td>
                    <td>${app.note}</td>
                    <td>
                        <span class="badge badge-${app.status == 'PENDING' ? 'pending' : (app.status == 'ACCEPTED' ? 'accepted' : 'rejected')}">${app.status}</span>
                    </td>
                    <td>
                        <c:if test="${app.status == 'PENDING'}">
                            <form method="post" action="${pageContext.request.contextPath}/applications" style="display:inline;">
                                <input type="hidden" name="action" value="updateStatus">
                                <input type="hidden" name="appId" value="${app.id}">
                                <input type="hidden" name="jobId" value="${jobId}">
                                <input type="hidden" name="status" value="ACCEPTED">
                                <button type="submit" class="btn btn-success">Accept</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/applications" style="display:inline;">
                                <input type="hidden" name="action" value="updateStatus">
                                <input type="hidden" name="appId" value="${app.id}">
                                <input type="hidden" name="jobId" value="${jobId}">
                                <input type="hidden" name="status" value="REJECTED">
                                <button type="submit" class="btn btn-danger">Reject</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
    <a href="${pageContext.request.contextPath}/jobs?action=myJobs" style="display:inline-block;margin-top:12px;">Back to My Jobs</a>
</div>
<%@ include file="../common/footer.jsp" %>
