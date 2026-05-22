<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">Skill Gap Analysis (E10)</h2>
    <c:if test="${not empty llmMessage}">
        <p style="margin-bottom:12px;padding:8px 12px;background:${llmUsed ? '#e8f8f0' : '#fff3cd'};border-radius:4px;font-size:14px;">
            ${llmMessage}
        </c:if>

    <form method="get" action="${pageContext.request.contextPath}/ai" style="margin-bottom:16px;">
        <input type="hidden" name="action" value="skillGap">
        <label for="jobId">Select Target Position: </label>
        <select id="jobId" name="jobId" onchange="this.form.submit()">
            <option value="">-- Choose a position --</option>
            <c:forEach var="job" items="${jobs}">
                <option value="${job.id}" ${selectedJobId == job.id ? 'selected' : ''}>${job.title} (${job.moduleName})</option>
            </c:forEach>
        </select>
    </form>

    <c:if test="${not empty targetJob}">
        <div class="card" style="border:1px solid #ddd;">
            <h3>Analysis: ${targetJob.title}</h3>
            <p><strong>Position Required Skills:</strong> ${targetJob.requiredSkills}</p>
            <p><strong>Your Skills:</strong> ${currentUser.skills}</p>

            <c:if test="${not empty aiSummary}">
                <h4 style="margin-top:12px;">AI Overall Assessment</h4>
                <p style="line-height:1.6;">${aiSummary}</p>
            </c:if>

            <h4 style="margin-top:12px;color:#27ae60;">Matched Skills</h4>
            <c:if test="${empty matchedSkills}">
                <p>No matching skills found.</p>
            </c:if>
            <c:forEach var="s" items="${matchedSkills}">
                <span class="badge badge-accepted">${s}</span>
            </c:forEach>

            <h4 style="margin-top:12px;color:#e74c3c;">Missing Skills</h4>
            <c:if test="${empty missingSkills}">
                <p>You have all required skills!</p>
            </c:if>
            <c:forEach var="s" items="${missingSkills}">
                <span class="badge badge-rejected">${s}</span>
            </c:forEach>

            <c:if test="${not empty suggestions}">
                <h4 style="margin-top:12px;">Improvement Suggestions</h4>
                <ul>
                    <c:forEach var="sug" items="${suggestions}">
                        <li>${sug}</li>
                    </c:forEach>
                </ul>
            </c:if>

            <div style="margin-top:16px;">
                <a href="${pageContext.request.contextPath}/ai?action=downloadGapReport&jobId=${targetJob.id}" class="btn btn-primary">Download Report (TXT)</a>
            </div>
        </div>
    </c:if>
</div>
<%@ include file="../common/footer.jsp" %>
