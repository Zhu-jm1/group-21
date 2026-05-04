<%@ include file="../common/header.jsp" %>
<div class="card">
    <h2 style="margin-bottom:16px;">${job.title}</h2>
    <p><strong>Module:</strong> ${job.moduleName}</p>
    <p><strong>Type:</strong> ${job.type}</p>
    <p><strong>Description:</strong> ${job.description}</p>
    <p><strong>Required Skills:</strong> ${job.requiredSkills}</p>
    <p><strong>Status:</strong>
        <span class="badge badge-${job.status == 'OPEN' ? 'open' : 'closed'}">${job.status}</span>
    </p>
    <p><strong>Posted:</strong> ${job.createdDate}</p>

    <c:if test="${currentUser.role == 'TA' && job.status == 'OPEN'}">
        <hr style="margin:16px 0;">
        <h3>AI 智能匹配与简历建议</h3>
        <p style="color:#555;font-size:14px;margin-bottom:10px;">
            根据您在个人资料中填写的信息与职位要求，估算匹配度并生成可粘贴至求职信的草稿（若服务器配置了 OPENAI_API_KEY 则使用大模型分析）。
        </p>
        <button type="button" class="btn btn-primary" id="btnAiMatch">生成匹配分析与求职信建议</button>
        <span id="aiMatchStatus" style="margin-left:12px;color:#7f8c8d;font-size:14px;"></span>
        <div id="aiMatchPanel" style="display:none;margin-top:16px;padding:14px;background:#f8f9fa;border-radius:6px;border:1px solid #e9ecef;">
            <p><strong>匹配度：</strong><span id="aiMatchScore">—</span>/100
                <span id="aiMatchSource" style="margin-left:8px;font-size:12px;color:#95a5a6;"></span></p>
            <div class="form-group" style="margin-bottom:10px;">
                <label>分析说明</label>
                <div id="aiMatchAnalysis" style="white-space:pre-wrap;font-size:14px;line-height:1.5;color:#333;"></div>
            </div>
            <div class="form-group">
                <label for="aiResumeSuggestion">求职信 / 亮点草稿（可编辑后填入下方 Cover Note）</label>
                <textarea id="aiResumeSuggestion" rows="8" style="width:100%;"></textarea>
            </div>
            <button type="button" class="btn btn-warning" id="btnCopyToNote">复制到 Cover Note</button>
        </div>

        <hr style="margin:16px 0;">
        <h3>Apply for this Job</h3>
        <form method="post" action="${pageContext.request.contextPath}/applications">
            <input type="hidden" name="action" value="apply">
            <input type="hidden" name="jobId" value="${job.id}">
            <div class="form-group">
                <label for="note">Cover Note</label>
                <textarea id="note" name="note" rows="3" placeholder="Why are you a good fit?"></textarea>
            </div>
            <button type="submit" class="btn btn-success">Submit Application</button>
        </form>
        <script>
        (function () {
            var btn = document.getElementById('btnAiMatch');
            var statusEl = document.getElementById('aiMatchStatus');
            var panel = document.getElementById('aiMatchPanel');
            var scoreEl = document.getElementById('aiMatchScore');
            var srcEl = document.getElementById('aiMatchSource');
            var analysisEl = document.getElementById('aiMatchAnalysis');
            var sugg = document.getElementById('aiResumeSuggestion');
            var note = document.getElementById('note');
            var copyBtn = document.getElementById('btnCopyToNote');
            if (!btn) return;
            btn.addEventListener('click', function () {
                statusEl.textContent = '分析中…';
                panel.style.display = 'none';
                var url = '${pageContext.request.contextPath}/api/ai/match?jobId=' + encodeURIComponent('${job.id}');
                fetch(url, { credentials: 'same-origin' })
                    .then(function (r) {
                        if (!r.ok) throw new Error('HTTP ' + r.status);
                        return r.json();
                    })
                    .then(function (data) {
                        if (data.errorMessage) {
                            statusEl.textContent = data.errorMessage;
                            return;
                        }
                        statusEl.textContent = '完成';
                        panel.style.display = 'block';
                        scoreEl.textContent = data.matchScore != null ? data.matchScore : '—';
                        var src = data.source || '';
                        srcEl.textContent = src === 'AI' ? '（大模型）' : (src === 'HEURISTIC' ? '（本地估算）' : '');
                        analysisEl.textContent = data.analysis || '';
                        sugg.value = data.resumeSuggestion || '';
                    })
                    .catch(function (e) {
                        statusEl.textContent = '请求失败：' + e.message;
                    });
            });
            copyBtn.addEventListener('click', function () {
                note.value = sugg.value || '';
            });
        })();
        </script>
    </c:if>

    <c:if test="${currentUser.role == 'MO'}">
        <hr style="margin:16px 0;">
        <a href="${pageContext.request.contextPath}/applications?action=review&jobId=${job.id}" class="btn btn-primary">Review Applications</a>
    </c:if>
</div>
<%@ include file="../common/footer.jsp" %>
