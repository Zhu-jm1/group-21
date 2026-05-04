<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">Available Jobs</h2>
    <c:if test="${empty jobs}">
        <p>No open jobs at the moment.</p>
    </c:if>
    <c:if test="${not empty jobs}">
        <table>
            <tr>
                <th>Title</th>
                <th>Module</th>
                <th>Type</th>
                <th>Required Skills</th>
                <c:if test="${currentUser != null && currentUser.role == 'TA'}">
                    <th>Match</th>
                </c:if>
                <th>Posted Date</th>
                <th>Action</th>
            </tr>
            <c:forEach var="job" items="${jobs}">
                <tr>
                    <td>${job.title}</td>
                    <td>${job.moduleName}</td>
                    <td>${job.type}</td>
                    <td>${job.requiredSkills}</td>
                    <c:if test="${currentUser != null && currentUser.role == 'TA'}">
                        <td>
                            <c:choose>
                                <c:when test="${job.matchScore > 0}">${job.matchScore}%</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </td>
                    </c:if>
                    <td>${job.createdDate}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/jobs?action=detail&id=${job.id}" class="btn btn-primary">View</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
<%@ include file="../common/footer.jsp" %>
