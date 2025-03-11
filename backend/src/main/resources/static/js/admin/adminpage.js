/**
 * 
 */
$(function() {
    function loadShares() {
        $.ajax({
            url: "/admin/shares",
            type: "GET",
            success: function(data) {
                let tbody = $("#post-monitoring tbody");
                tbody.empty();
                
                if (data.length === 0) {
                    tbody.append("<tr><td colspan='5' class='text-center'>등록된 게시글이 없습니다.</td></tr>");
                } else {
                    data.forEach((item, index) => {
                        let row = `<tr>
                            <td>${index + 1}</td>
                            <td>${item.title}</td>
                            <td>${item.author}</td>
                            <td>${new Date(item.createdAt).toLocaleString()}</td>
                            <td><button class="btn btn-danger delete-btn" data-id="${item.id}">삭제</button></td>
                        </tr>`;
                        tbody.append(row);
                    });
                }
            }
        });
    }

    $(document).on("click", ".delete-btn", function() {
        let id = $(this).data("id");
        if (!confirm("정말 삭제하시겠습니까?")) return;

        $.ajax({
            url: `/admin/shares/${id}`,
            type: "DELETE",
            success: function() {
                alert("삭제되었습니다.");
                loadShares();
            }
        });
    });

    loadShares();
});
