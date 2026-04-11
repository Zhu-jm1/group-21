<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">TA Workload Overview</h2>
    <c:if test="${empty workloadMap}">
        <p>No accepted applications yet.</p>
    </c:if>
    <c:if test="${not empty workloadMap}">
        <table>
            <tr>
                <th>TA Name</th>
                <th>Accepted Jobs Count</th>
            </tr>
            <c:forEach var="entry" items="${workloadMap}">
                <tr>
                    <td>${taNames[entry.key]}</td>
                    <td>${entry.value}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
<%@ include file="../common/footer.jsp" %>
