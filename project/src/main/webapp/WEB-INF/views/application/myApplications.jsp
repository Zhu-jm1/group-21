<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">My Applications</h2>

    <div style="margin-bottom:16px;">
        <form method="get" action="${pageContext.request.contextPath}/export" style="display:inline;">
            <label>Export: </label>
            <input type="date" name="startDate" placeholder="Start Date">
            <input type="date" name="endDate" placeholder="End Date">
            <button type="submit" class="btn btn-success">Export CSV</button>
        </form>
    </div>

    <c:if test="${empty applications}">
        <p>You haven't applied for any jobs yet. <a href="${pageContext.request.contextPath}/jobs?action=list">Browse jobs</a></p>
    </c:if>
    <c:if test="${not empty applications}">
        <table>
            <tr>
                <th>Job ID</th>
                <th>Applied Date</th>
                <th>Status</th>
                <th>Note</th>
            </tr>
            <c:forEach var="app" items="${applications}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/jobs?action=detail&id=${app.jobId}">${app.jobId}</a></td>
                    <td>${app.applyDate}</td>
                    <td>
                        <span class="badge badge-${app.status == 'PENDING' ? 'pending' : (app.status == 'ACCEPTED' ? 'accepted' : 'rejected')}">${app.status}</span>
                    </td>
                    <td>${app.note}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
<%@ include file="../common/footer.jsp" %>
