<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">TA Workload Overview (S10)</h2>

    <form method="get" action="${pageContext.request.contextPath}/admin" style="margin-bottom:16px;">
        <input type="hidden" name="action" value="workload">
        <label for="filterType">Filter by Position Type: </label>
        <select id="filterType" name="filterType" onchange="this.form.submit()">
            <option value="">All Types</option>
            <option value="MODULE" ${filterType == 'MODULE' ? 'selected' : ''}>Module TA</option>
            <option value="INVIGILATION" ${filterType == 'INVIGILATION' ? 'selected' : ''}>Invigilation</option>
            <option value="OTHER" ${filterType == 'OTHER' ? 'selected' : ''}>Other</option>
        </select>
    </form>

    <c:if test="${empty workloadData}">
        <p>No TA data available.</p>
    </c:if>
    <c:if test="${not empty workloadData}">
        <table>
            <tr>
                <th>TA Name</th>
                <th>Student ID</th>
                <th>Assigned Positions</th>
                <th>Position Types</th>
                <th>Total Class Hours</th>
                <th>Job Count</th>
            </tr>
            <c:forEach var="row" items="${workloadData}">
                <tr>
                    <td>${row.taName}</td>
                    <td>${row.studentId}</td>
                    <td>
                        <c:forEach var="j" items="${row.assignedJobs}">
                            <span class="badge badge-open">${j.title}</span>
                        </c:forEach>
                        <c:if test="${empty row.assignedJobs}">-</c:if>
                    </td>
                    <td>
                        <c:forEach var="j" items="${row.assignedJobs}">
                            <span>${j.type}</span>
                        </c:forEach>
                        <c:if test="${empty row.assignedJobs}">-</c:if>
                    </td>
                    <td>${row.totalHours}</td>
                    <td>${row.jobCount}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
<%@ include file="../common/footer.jsp" %>
