<%@ page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<%@ include file="/WEB-INF/includes/sidebar.jsp"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>

<!-- Nội dung chính (đã được mở sẵn từ sidebar.jsp) -->
<div class="main-content">
    <h1>📊 Thống kê doanh thu</h1>
    <p class="sub">Tổng quan tình hình kinh doanh</p>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="icon green"><i class="bi bi-coin"></i></div>
            <div class="info">
                <div class="label">Tổng doanh thu</div>
                <div class="value">
                    <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                </div>
            </div>
        </div>
        <div class="stat-card">
            <div class="icon blue"><i class="bi bi-check-circle"></i></div>
            <div class="info">
                <div class="label">Đơn hàng hoàn thành</div>
                <div class="value">${completedOrders}</div>
            </div>
        </div>
        <div class="stat-card">
            <div class="icon yellow"><i class="bi bi-clock"></i></div>
            <div class="info">
                <div class="label">Đơn hàng chờ xử lý</div>
                <div class="value">${pendingOrders}</div>
            </div>
        </div>
    </div>

    <!-- ===== BIỂU ĐỒ ===== -->
    <div style="background: #fff; border-radius: 16px; padding: 24px; box-shadow: 0 4px 20px rgba(0,0,0,0.04);">
        <h3 style="margin-bottom: 16px;">📈 Doanh thu theo tháng (12 tháng gần nhất)</h3>
        <canvas id="revenueChart" style="max-height: 300px; width: 100%;"></canvas>
    </div>
</div>

<style>
    /* Giữ nguyên style cũ của bạn, chỉ bổ sung thêm nếu cần */
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 20px;
        margin-bottom: 32px;
    }
    .stat-card {
        background: #fff;
        border-radius: 16px;
        padding: 20px 24px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.04);
        display: flex;
        align-items: center;
        gap: 16px;
        transition: 0.3s;
    }
    .stat-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 32px rgba(0,0,0,0.08);
    }
    .stat-card .icon {
        width: 56px;
        height: 56px;
        border-radius: 14px;
        background: #f0f2f5;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        flex-shrink: 0;
    }
    .stat-card .icon.green { background: #dcfce7; color: #166534; }
    .stat-card .icon.blue  { background: #dbeafe; color: #1e40af; }
    .stat-card .icon.yellow{ background: #fef3c7; color: #92400e; }
    .stat-card .info .label { font-size: 14px; color: #888; }
    .stat-card .info .value { font-size: 28px; font-weight: 700; }
    .sub { color: #666; margin-bottom: 24px; }
    .main-content { flex: 1; padding: 24px 32px; overflow-y: auto; background: #f4f6f9; }
    .main-content h1 { font-size: 28px; font-weight: 700; margin-bottom: 6px; }
</style>

<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Lấy dữ liệu từ server (dùng JSTL tạo mảng)
        const rawData = [
            <c:forEach items="${monthlyData}" var="row" varStatus="status">
                { month: '${row[0]}', revenue: ${row[1] != null ? row[1] : 0} }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        // Sắp xếp theo tháng tăng dần (để biểu đồ đẹp)
        rawData.sort((a, b) => a.month.localeCompare(b.month));

        const labels = rawData.map(item => item.month);
        const revenues = rawData.map(item => item.revenue);

        const ctx = document.getElementById('revenueChart').getContext('2d');

        // Gradient fill
        const gradient = ctx.createLinearGradient(0, 0, 0, 300);
        gradient.addColorStop(0, 'rgba(166, 243, 107, 0.6)');
        gradient.addColorStop(1, 'rgba(166, 243, 107, 0.0)');

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Doanh thu (₫)',
                    data: revenues,
                    backgroundColor: gradient,
                    borderColor: '#a6f36b',
                    borderWidth: 2,
                    borderRadius: 6,
                    barPercentage: 0.6,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                let value = context.raw;
                                return value.toLocaleString('vi-VN') + ' ₫';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return value.toLocaleString('vi-VN') + ' ₫';
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    });
</script>

<%@ include file="/WEB-INF/includes/footer.jsp" %>