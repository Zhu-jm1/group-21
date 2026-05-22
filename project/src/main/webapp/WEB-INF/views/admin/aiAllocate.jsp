<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">AI Balanced Allocation (E11)</h2>
    <p style="margin-bottom:12px;">AI recommends TAs with the lowest workload for each open position. Maximum threshold: <strong>${maxPositions}</strong> positions per TA.</p>

    <c:if test="${empty recommendations}">
        <p>No open jobs available for allocation.</p>
    </c:if>
    <c:forEach var="rec" items="${recommendations}">
        <div class="card" style="border:1px solid #ddd;">
            <h3>${rec.job.title} <span class="badge badge-open">${rec.job.type}</span></h3>
            <p>Module: ${rec.job.moduleName} | Required Skills: ${rec.job.requiredSkills}</p>
            <c:if test="${empty rec.candidates}">
                <p style="color:#e74c3c;">No available TAs (all at maximum workload).</p>
            </c:if>
            <c:if test="${not empty rec.candidates}">
                <table>
                    <tr>
                        <th>TA Name</th><th>Current Workload</th><th>Skills</th><th>Action</th>
                    </tr>
                    <c:forEach var="cand" items="${rec.candidates}" end="4">
                        <tr>
                            <td>${cand.ta.name}</td>
                            <td>${cand.currentLoad} positions</td>
                            <td>${cand.ta.skills}</td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/admin" style="display:inline;">
                                    <input type="hidden" name="action" value="confirmAllocate">
                                    <input type="hidden" name="jobId" value="${rec.job.id}">
                                    <input type="hidden" name="taId" value="${cand.ta.id}">
                                    <button type="submit" class="btn btn-success" onclick="return confirm('Assign this TA to the position?')">Assign</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>
        </div>
    </c:forEach>
</div>
<%@ include file="../common/footer.jsp" %>
