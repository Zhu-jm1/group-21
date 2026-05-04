<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">My Posted Jobs</h2>
    <a href="${pageContext.request.contextPath}/jobs?action=create" class="btn btn-success" style="margin-bottom:12px;">+ Post New Job</a>
    <c:if test="${empty jobs}">
        <p>You haven't posted any jobs yet.</p>
    </c:if>
    <c:if test="${not empty jobs}">
        <table>
            <tr>
                <th>Title</th>
                <th>Module</th>
                <th>Type</th>
                <th>Status</th>
                <th>Posted</th>
                <th>Actions</th>
            </tr>
            <c:forEach var="job" items="${jobs}">
                <tr>
                    <td>${job.title}</td>
                    <td>${job.moduleName}</td>
                    <td>${job.type}</td>
                    <td><span class="badge badge-${job.status == 'OPEN' ? 'open' : 'closed'}">${job.status}</span></td>
                    <td>${job.createdDate}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/applications?action=review&jobId=${job.id}" class="btn btn-primary">Review</a>
                        <c:if test="${job.status == 'OPEN'}">
                            <form method="post" action="${pageContext.request.contextPath}/jobs" style="display:inline;">
                                <input type="hidden" name="action" value="close">
                                <input type="hidden" name="id" value="${job.id}">
                                <button type="submit" class="btn btn-danger">Close</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
<%@ include file="../common/footer.jsp" %>
