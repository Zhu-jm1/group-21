<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">Admin Dashboard</h2>
    <div style="display:flex;gap:16px;flex-wrap:wrap;">
        <div class="card" style="flex:1;min-width:200px;text-align:center;background:#ecf0f1;">
            <h3>${totalUsers}</h3><p>Total Users</p>
        </div>
        <div class="card" style="flex:1;min-width:200px;text-align:center;background:#ecf0f1;">
            <h3>${totalTAs}</h3><p>Teaching Assistants</p>
        </div>
        <div class="card" style="flex:1;min-width:200px;text-align:center;background:#ecf0f1;">
            <h3>${totalJobs}</h3><p>Total Jobs</p>
        </div>
        <div class="card" style="flex:1;min-width:200px;text-align:center;background:#ecf0f1;">
            <h3>${totalApps}</h3><p>Total Applications</p>
        </div>
    </div>
    <div style="margin-top:20px;">
        <a href="${pageContext.request.contextPath}/admin?action=users" class="btn btn-primary">Manage Users</a>
        <a href="${pageContext.request.contextPath}/admin?action=workload" class="btn btn-success">View Workload</a>
        <a href="${pageContext.request.contextPath}/admin?action=aiAllocate" class="btn btn-warning">AI Allocate</a>
        <a href="${pageContext.request.contextPath}/ai?action=match" class="btn btn-primary">AI Match</a>
        <a href="${pageContext.request.contextPath}/export" class="btn btn-success">Export Records</a>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
