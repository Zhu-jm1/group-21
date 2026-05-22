<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">AI Skill Matching (E9)</h2>
    <c:if test="${not empty llmMessage}">
        <p style="margin-bottom:12px;padding:8px 12px;background:${llmUsed ? '#e8f8f0' : '#fff3cd'};border-radius:4px;font-size:14px;">
            ${llmMessage}
        </c:if>

    <form method="get" action="${pageContext.request.contextPath}/ai" style="margin-bottom:16px;">
        <input type="hidden" name="action" value="match">
        <label for="jobId">Filter by Job: </label>
        <select id="jobId" name="jobId" onchange="this.form.submit()">
            <option value="">All Jobs</option>
            <c:forEach var="job" items="${jobs}">
                <option value="${job.id}" ${filterJobId == job.id ? 'selected' : ''}>${job.title}</option>
            </c:forEach>
        </select>
        <a href="${pageContext.request.contextPath}/ai?action=exportMatch" class="btn btn-success" style="margin-left:12px;">Export CSV</a>
    </form>

    <c:if test="${empty matchResults}">
        <p>No matching data available.</p>
    </c:if>
    <c:if test="${not empty matchResults}">
        <table>
            <tr>
                <th>Job Title</th>
                <th>TA Name</th>
                <th>Job Required Skills</th>
                <th>TA Skills</th>
                <th>Match Level</th>
                <th>Match Ratio</th>
                <th>AI Analysis</th>
            </tr>
            <c:forEach var="row" items="${matchResults}">
                <tr>
                    <td>${row.jobTitle}</td>
                    <td>${row.taName}</td>
                    <td>${row.jobSkills}</td>
                    <td>${row.taSkills}</td>
                    <td>
                        <span class="badge" style="background:${row.matchLevel == 'High' ? '#27ae60' : (row.matchLevel == 'Medium' ? '#f39c12' : '#e74c3c')}">${row.matchLevel}</span>
                    </td>
                    <td>${row.matchRatio}</td>
                    <td style="max-width:280px;font-size:13px;">${row.reason}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
<%@ include file="../common/footer.jsp" %>
