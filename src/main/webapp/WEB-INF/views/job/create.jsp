<%@ include file="../common/header.jsp" %>
<div class="card" style="max-width:600px;">
    <h2 style="margin-bottom:16px;">Post a New Job</h2>
    <form method="post" action="${pageContext.request.contextPath}/jobs">
        <input type="hidden" name="action" value="create">
        <div class="form-group">
            <label for="title">Job Title</label>
            <input type="text" id="title" name="title" required>
        </div>
        <div class="form-group">
            <label for="moduleName">Module Name</label>
            <input type="text" id="moduleName" name="moduleName" required>
        </div>
        <div class="form-group">
            <label for="type">Type</label>
            <select id="type" name="type">
                <option value="MODULE">Module TA</option>
                <option value="INVIGILATION">Invigilation</option>
                <option value="OTHER">Other</option>
            </select>
        </div>
        <div class="form-group">
            <label for="requiredSkills">Required Skills (comma-separated)</label>
            <input type="text" id="requiredSkills" name="requiredSkills" placeholder="e.g. Java, Python">
        </div>
        <div class="form-group">
            <label for="description">Description</label>
            <textarea id="description" name="description" rows="4"></textarea>
        </div>
        <button type="submit" class="btn btn-success">Post Job</button>
    </form>
</div>
<%@ include file="../common/footer.jsp" %>
