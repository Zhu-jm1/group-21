<%@ include file="common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">Application Deadline Reminders (E12)</h2>
    <p>Your reminder method: <strong>${reminderMethod}</strong>
        (Change in <a href="${pageContext.request.contextPath}/profile">Profile</a>)
    </p>

    <c:if test="${empty reminders}">
        <p style="color:#27ae60;">No upcoming deadlines within 24 hours, or you've already applied for all positions.</p>
    </c:if>
    <c:if test="${not empty reminders}">
        <table>
            <tr>
                <th>Job Title</th>
                <th>Module</th>
                <th>Deadline</th>
                <th>Action</th>
            </tr>
            <c:forEach var="job" items="${reminders}">
                <tr>
                    <td>${job.title}</td>
                    <td>${job.moduleName}</td>
                    <td><span style="color:#e74c3c;font-weight:bold;">${job.deadline}</span></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/jobs?action=detail&id=${job.id}" class="btn btn-primary">Apply Now</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <p style="margin-top:12px;color:#7f8c8d;">
            * A simulated ${reminderMethod} reminder has been sent to your registered contact.
        </p>
    </c:if>
</div>
<%@ include file="common/footer.jsp" %>
